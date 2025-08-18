package be.pxl.services.services.util;

import be.pxl.services.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String jwtSecret = "mySecretKey";
    private final int jwtExpirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", jwtExpirationMs);
    }

    @Test
    void testGenerateToken() {
        // Arrange: Create a sample user
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        // Act: Generate a token for the user
        String token = jwtUtil.generateToken(user);
        assertNotNull(token, "Token should not be null");

        // Assert: Parse the token to validate its content
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        assertEquals("test@example.com", claims.getSubject(), "Subject should be user's email");
        assertEquals("John", claims.get("firstName"), "First name claim should match");
        assertEquals("Doe", claims.get("lastName"), "Last name claim should match");

        // Check that the token has valid issued and expiration dates
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        assertNotNull(issuedAt, "IssuedAt should not be null");
        assertNotNull(expiration, "Expiration should not be null");
        assertTrue(expiration.after(issuedAt), "Expiration should be after issuedAt");

        // Verify the expiration period is roughly as configured
        long diff = expiration.getTime() - issuedAt.getTime();
        // Allow some slack in milliseconds due to execution time
        assertTrue(diff >= jwtExpirationMs - 1000 && diff <= jwtExpirationMs + 1000,
                "Token expiration time should be close to configured value");
    }
}

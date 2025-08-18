package be.pxl.services.services.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SecurityConfig.class)
@ImportAutoConfiguration(SecurityAutoConfiguration.class)
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void testPasswordEncoderBean() {
        assertNotNull(passwordEncoder, "PasswordEncoder bean should not be null");
        String rawPassword = "password";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword),
                "Encoded password should match raw password");
    }

    @Test
    void testSecurityFilterChainBean() {
        assertNotNull(securityFilterChain, "SecurityFilterChain bean should not be null");
    }
}

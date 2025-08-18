package be.pxl.services.services;

import be.pxl.services.domain.dto.UserRequest;
import be.pxl.services.domain.User;
import be.pxl.services.repository.UserRepository;
import be.pxl.services.services.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceValidationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private UserRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = UserRequest.builder()
                .email("valid@email.com")
                .firstName("Jan")
                .lastName("Jansen")
                .password("validPassword123")
                .build();
    }

    @Test
    void register_WithValidData_ReturnsUser() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(new User());

        assertDoesNotThrow(() -> userService.register(validRequest));
        verify(userRepository).save(any());
    }

    @Test
    void register_WithInvalidEmail_ThrowsException() {
        validRequest.setEmail("invalid-email");

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.register(validRequest));

        assertTrue(exception.getMessage().contains("Invalid email format"));
    }

    @Test
    void register_WithExistingEmail_ThrowsException() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.register(validRequest));

        assertTrue(exception.getMessage().contains("Email is already in use"));
    }

    @Test
    void register_WithShortFirstName_ThrowsException() {
        validRequest.setFirstName("Jo");

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.register(validRequest));

        assertTrue(exception.getMessage().contains("First name must be between 3 and 21 characters"));
    }

    @Test
    void register_WithLongFirstName_ThrowsException() {
        validRequest.setFirstName("J".repeat(22));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.register(validRequest));

        assertTrue(exception.getMessage().contains("First name must be between 3 and 21 characters"));
    }

    @Test
    void register_WithInvalidFirstNameCharacters_ThrowsException() {
        validRequest.setFirstName("Jan123");

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.register(validRequest));

        assertTrue(exception.getMessage().contains("First name contains invalid characters"));
    }

    @Test
    void register_WithShortLastName_ThrowsException() {
        validRequest.setLastName("Bo");

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.register(validRequest));

        assertTrue(exception.getMessage().contains("Last name must be between 3 and 21 characters"));
    }

    @Test
    void register_WithLongLastName_ThrowsException() {
        validRequest.setLastName("B".repeat(22));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.register(validRequest));

        assertTrue(exception.getMessage().contains("Last name must be between 3 and 21 characters"));
    }

    @Test
    void register_WithInvalidLastNameCharacters_ThrowsException() {
        validRequest.setLastName("Jansen@");

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.register(validRequest));

        assertTrue(exception.getMessage().contains("Last name contains invalid characters"));
    }

    @Test
    void register_WithShortPassword_ThrowsException() {
        validRequest.setPassword("short");

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.register(validRequest));

        assertTrue(exception.getMessage().contains("Password must be at least 6 characters"));
    }
}
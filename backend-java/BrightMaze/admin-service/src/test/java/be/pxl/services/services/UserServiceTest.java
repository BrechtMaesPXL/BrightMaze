package be.pxl.services.services;

import be.pxl.services.domain.User;
import be.pxl.services.domain.dto.UserRequest;
import be.pxl.services.exceptions.AuthenticationException;
import be.pxl.services.repository.UserRepository;
import be.pxl.services.services.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        userService = new UserService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void register_shouldSaveUser_whenValidRequest() {
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User savedUser = userService.register(request);

        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getFirstName()).isEqualTo("John");
        assertThat(savedUser.getLastName()).isEqualTo("Doe");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        UserRequest request = new UserRequest();
        request.setEmail("existing@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.register(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email is already in use");
    }

    @Test
    void register_shouldThrow_whenInvalidEmail() {
        UserRequest request = new UserRequest();
        request.setEmail("invalid-email");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        assertThatThrownBy(() -> userService.register(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    void login_shouldReturnToken_whenCredentialsValid() throws AuthenticationException {
        String email = "user@example.com";
        String rawPassword = "secret";
        User user = User.builder()
                .email(email)
                .password("encodedSecret")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "encodedSecret")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        String token = userService.login(email, rawPassword);

        assertThat(token).isEqualTo("jwt-token");
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login("unknown@example.com", "password"))
            .isInstanceOf(AuthenticationException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    void login_shouldThrow_whenPasswordInvalid() {
        User user = User.builder()
                .email("user@example.com")
                .password("encodedSecret")
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedSecret")).thenReturn(false);

        assertThatThrownBy(() -> userService.login("user@example.com", "wrongPassword"))
            .isInstanceOf(AuthenticationException.class)
            .hasMessageContaining("Invalid credentials");
    }
}

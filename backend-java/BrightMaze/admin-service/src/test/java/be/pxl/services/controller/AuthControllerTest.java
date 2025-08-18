package be.pxl.services.controller;

import be.pxl.services.domain.User;
import be.pxl.services.domain.dto.JwtResponse;
import be.pxl.services.domain.dto.LoginRequest;
import be.pxl.services.domain.dto.UserRequest;
import be.pxl.services.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private UserRequest userRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        // Setup test data
        userRequest = new UserRequest();
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password");
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
    }

    @Test
    void test_ShouldReturnOkResponse() {
        // Act
        ResponseEntity<String> response = authController.test();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test 3.0", response.getBody());
    }

    @Test
    void register_ShouldReturnCreatedUser() {
        // Arrange
        when(userService.register(any(UserRequest.class))).thenReturn(user);

        // Act
        ResponseEntity<User> response = authController.register(userRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService).register(userRequest);
    }

    @Test
    void login_ShouldReturnJwtToken_WhenCredentialsAreValid() {
        // Arrange
        String token = "valid.jwt.token";
        when(userService.login(anyString(), anyString())).thenReturn(token);

        // Act
        ResponseEntity<Object> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof JwtResponse);
        assertEquals(token, ((JwtResponse) response.getBody()).getToken());
        verify(userService).login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() {
        // Arrange
        String errorMessage = "Invalid credentials";
        when(userService.login(anyString(), anyString())).thenThrow(new RuntimeException(errorMessage));

        // Act
        ResponseEntity<Object> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals(errorMessage, errorResponse.get("error"));
        verify(userService).login(loginRequest.getEmail(), loginRequest.getPassword());
    }
}

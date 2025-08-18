package be.pxl.services.services;

import be.pxl.services.domain.dto.UserRequest;
import be.pxl.services.domain.User;
import be.pxl.services.exceptions.AuthenticationException;
import be.pxl.services.repository.UserRepository;
import be.pxl.services.services.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ\\s\\-']+$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public User register(UserRequest userRequest) {
        validateUserRequest(userRequest);

        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = mapToUser(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Registration failed due to data validation issues");
        }
    }

    private void validateUserRequest(UserRequest userRequest) {
        if (!EMAIL_PATTERN.matcher(userRequest.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (userRequest.getFirstName().length() < 3 || userRequest.getFirstName().length() > 21) {
            throw new IllegalArgumentException("First name must be between 3 and 21 characters");
        }
        if (userRequest.getLastName().length() < 3 || userRequest.getLastName().length() > 21) {
            throw new IllegalArgumentException("Last name must be between 3 and 21 characters");
        }
        if (!NAME_PATTERN.matcher(userRequest.getFirstName()).matches()) {
            throw new IllegalArgumentException("First name contains invalid characters");
        }
        if (!NAME_PATTERN.matcher(userRequest.getLastName()).matches()) {
            throw new IllegalArgumentException("Last name contains invalid characters");
        }
        if (userRequest.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    @Override
    public String login(String eMail, String rawPassword) throws AuthenticationException {
        User user = userRepository.findByEmail(eMail)
                .orElseThrow(() -> new AuthenticationException("User not found"));
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return jwtUtil.generateToken(user);
        } else {
            throw new AuthenticationException("Invalid credentials");
        }
    }

    private User mapToUser(UserRequest userRequest) {
        return User.builder()
                .email(userRequest.getEmail())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .password(userRequest.getPassword())
                .build();
    }
}

package be.pxl.services.controller;


import be.pxl.services.domain.dto.JwtResponse;
import be.pxl.services.domain.dto.LoginRequest;
import be.pxl.services.domain.dto.UserRequest;
import be.pxl.services.domain.User;
import be.pxl.services.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);



    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("test 3.0", HttpStatus.OK);

    }
    @PostMapping("/register")

    public ResponseEntity<User> register(@RequestBody UserRequest userRequest) {
        log.info("Register user: {}", userRequest);
        User createdUser = userService.register(userRequest);
        log.info("Created user: {}", createdUser);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
package be.pxl.services.dto;

import be.pxl.services.domain.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Assertions;

@SpringBootTest(classes = LoginRequest.class)
class LoginRequestTests {

    @Test

    void testLoginRequestConstAndGetSet(){
        LoginRequest loginRequest = new LoginRequest("admin", "password");

        Assertions.assertEquals("admin", loginRequest.getEmail());
        Assertions.assertEquals("password", loginRequest.getPassword());

    }

    @Test
    void testLoginRequestBuilder(){
        LoginRequest loginRequest =  LoginRequest.builder()
                .email("admin")
                .password("password")
                .build();

        Assertions.assertEquals("admin", loginRequest.getEmail());
        Assertions.assertEquals("password", loginRequest.getPassword());

    }
}

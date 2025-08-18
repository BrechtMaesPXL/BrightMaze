package be.pxl.services.dto;

import be.pxl.services.domain.dto.JwtResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = JwtResponse.class)
class JwtResponseTests {

    @Test
    void testJwtResponseContsAndGetSet(){
        JwtResponse jwtResponse = new JwtResponse("token");

        Assertions.assertEquals("token", jwtResponse.getToken());


    }
    @Test
    void testJwtResponseBuilder(){
        JwtResponse jwtResponse = JwtResponse.builder()
                .token("token")
                .build();
        Assertions.assertEquals("token", jwtResponse.getToken());

    }
}

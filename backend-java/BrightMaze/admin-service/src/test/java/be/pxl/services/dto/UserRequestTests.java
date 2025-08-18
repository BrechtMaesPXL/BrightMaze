package be.pxl.services.dto;

import be.pxl.services.domain.dto.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = UserRequest.class)
class UserRequestTests {

    @Test
    void testUserRequestConstAndGetSet(){
        UserRequest userRequest = new UserRequest(
                "email",
                "firstName",
                "lastName",
                "password"
        );
        Assertions.assertEquals("email", userRequest.getEmail());
        Assertions.assertEquals("password", userRequest.getPassword());
        Assertions.assertEquals("firstName", userRequest.getFirstName());
        Assertions.assertEquals("lastName", userRequest.getLastName());

    }

    @Test
    void testUserRequestBuidler(){
        UserRequest userRequest =  UserRequest.builder()
                .email("email")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .build();
        Assertions.assertEquals("email", userRequest.getEmail());
        Assertions.assertEquals("password", userRequest.getPassword());
        Assertions.assertEquals("firstName", userRequest.getFirstName());
        Assertions.assertEquals("lastName", userRequest.getLastName());

    }
}

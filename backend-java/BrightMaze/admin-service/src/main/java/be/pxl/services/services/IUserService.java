package be.pxl.services.services;

import be.pxl.services.domain.dto.UserRequest;
import be.pxl.services.domain.User;
import be.pxl.services.exceptions.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    User register(UserRequest userRequest);
    String login(String eMail, String rawPassword) throws AuthenticationException;
}
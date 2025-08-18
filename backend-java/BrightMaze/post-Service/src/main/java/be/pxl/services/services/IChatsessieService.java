package be.pxl.services.services;

import be.pxl.services.domain.dto.ChatsessieRequest;
import be.pxl.services.domain.dto.ChatsessieResponse;
import org.springframework.stereotype.Service;

@Service
public interface IChatsessieService {
    String startSession();

    ChatsessieResponse sendMessage(ChatsessieRequest chatsessieRequest);
    void endSession();


}

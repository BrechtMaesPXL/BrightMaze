package be.pxl.services.mapper;

import be.pxl.services.domain.Chatsessie;
import be.pxl.services.domain.dto.ChatsessieResponse;

public class HistoryMapper {

    private HistoryMapper() {
    }

    public static ChatsessieResponse toResponse(Chatsessie chatsessie) {
        return ChatsessieResponse.builder()
                .id(chatsessie.getId())
                .history(chatsessie.getHistory())
                .build();
    }
}

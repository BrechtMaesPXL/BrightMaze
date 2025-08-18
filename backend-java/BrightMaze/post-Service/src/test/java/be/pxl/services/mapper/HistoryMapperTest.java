package be.pxl.services.mapper;

import be.pxl.services.domain.Chatsessie;
import be.pxl.services.domain.History;
import be.pxl.services.domain.dto.ChatsessieResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryMapperTest {

    @Test
    void testToResponse() {
        String sessionId = "sessionId";
        History history = new History("historyId", List.of());

        Chatsessie chatsessie = Chatsessie.builder()
                .id(sessionId)
                .history(history)
                .build();
        ChatsessieResponse response = HistoryMapper.toResponse(chatsessie);
        assertNotNull(response);
        assertEquals(sessionId, response.getId());
        assertEquals(history, response.getHistory());
    }

    @Test
    void testToResponse_NullInput() {
        Chatsessie chatsessie = null;
        assertThrows(NullPointerException.class, () -> {
            HistoryMapper.toResponse(chatsessie);
        });
    }
}
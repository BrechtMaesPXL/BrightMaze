package be.pxl.services.domain.dto;

import be.pxl.services.domain.Chatsessie;
import be.pxl.services.domain.History;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChatsessieTest {

    @Test
    void testChatsessieCreation() {
        String id = UUID.randomUUID().toString();
        History history = new History();
        Chatsessie chatsessie = Chatsessie.builder()
                .id(id)
                .history(history)
                .build();
        assertNotNull(chatsessie);
        assertEquals(id, chatsessie.getId());
        assertEquals(history, chatsessie.getHistory());
    }

    @Test
    void testChatsessieDefaultId() {
        Chatsessie chatsessie = new Chatsessie();

        assertNotNull(chatsessie.getId());
    }

    @Test
    void testChatsessieWithHistory() {
        History history = new History();
        history.setId("historyId");
        history.setHistoryItems(List.of());

        Chatsessie chatsessie = Chatsessie.builder()
                .history(history)
                .build();

        assertNotNull(chatsessie);
        assertEquals(history, chatsessie.getHistory());
        assertEquals("historyId", chatsessie.getHistory().getId());
    }

    @Test
    void testChatsessieEqualsAndHashCode() {
        String id = UUID.randomUUID().toString();
        History history1 = new History();
        History history2 = new History();

        Chatsessie chatsessie1 = Chatsessie.builder()
                .id(id)
                .history(history1)
                .build();

        Chatsessie chatsessie2 = Chatsessie.builder()
                .id(id)
                .history(history2)
                .build();

        assertEquals(chatsessie1, chatsessie2);
        assertEquals(chatsessie1.hashCode(), chatsessie2.hashCode());
    }

    @Test
    void testChatsessieNotEquals() {
        History history = new History();

        Chatsessie chatsessie1 = Chatsessie.builder()
                .id(UUID.randomUUID().toString())
                .history(history)
                .build();

        Chatsessie chatsessie2 = Chatsessie.builder()
                .id(UUID.randomUUID().toString())
                .history(history)
                .build();

        assertNotEquals(chatsessie1, chatsessie2);
    }
}
package be.pxl.services.exceptions;

import be.pxl.services.exception.ChatSessionNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatSessionNotFoundExceptionTest {

    @Test
    void testExceptionMessageContainsSessionId() {
        String sessionId = "12345";

        ChatSessionNotFoundException exception = new ChatSessionNotFoundException(sessionId);

        assertThat(exception).hasMessageContaining("Chatsessie met ID '12345' niet gevonden.");
    }
}

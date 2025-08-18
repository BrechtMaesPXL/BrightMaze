package be.pxl.services.exceptions;

import be.pxl.services.exception.BotResponseProcessingException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BotResponseProcessingExceptionTest {

    @Test
    void testExceptionMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        String message = "Error processing bot response";

        BotResponseProcessingException exception = new BotResponseProcessingException(message, cause);

        assertThat(exception)
                .hasMessage(message)
                .hasCause(cause);
    }
}

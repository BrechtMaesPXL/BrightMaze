package be.pxl.services.exceptions;

import be.pxl.services.exception.EventValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventValidationExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Invalid event data";

        EventValidationException exception = new EventValidationException(message);

        assertThat(exception).hasMessage(message);
    }
}

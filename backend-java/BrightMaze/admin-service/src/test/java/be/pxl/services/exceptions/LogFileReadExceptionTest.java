package be.pxl.services.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogFileReadExceptionTest {

    @Test
    void testExceptionMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        String message = "Failed to read log file";

        LogFileReadException exception = new LogFileReadException(message, cause);

        assertThat(exception)
                .hasMessage(message)
                .hasCause(cause);
    }
}

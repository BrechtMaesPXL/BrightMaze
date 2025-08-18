package be.pxl.services.exceptions;

import be.pxl.services.exception.FileStorageException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileStorageExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "File could not be stored";

        FileStorageException exception = new FileStorageException(message);

        assertThat(exception).hasMessage(message);
    }
}

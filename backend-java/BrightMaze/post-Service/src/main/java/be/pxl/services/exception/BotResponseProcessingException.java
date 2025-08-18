package be.pxl.services.exception;

public class BotResponseProcessingException extends RuntimeException {
    public BotResponseProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
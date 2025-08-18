package be.pxl.services.exception;

public class ChatSessionNotFoundException extends RuntimeException {
    public ChatSessionNotFoundException(String sessionId) {
        super("Chatsessie met ID '" + sessionId + "' niet gevonden.");
    }
}

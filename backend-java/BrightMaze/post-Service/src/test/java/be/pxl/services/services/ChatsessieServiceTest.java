package be.pxl.services.services;

import be.pxl.services.client.PythonChatbotClient;
import be.pxl.services.domain.Chatsessie;
import be.pxl.services.domain.History;
import be.pxl.services.domain.HistoryItem;
import be.pxl.services.domain.dto.ChatsessieRequest;
import be.pxl.services.enums.Role;
import be.pxl.services.repository.ChatSessieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatsessieServiceTest {

    @Mock
    private ChatSessieRepository chatsessieRepository;

    @Mock
    private PythonChatbotClient pythonChatbotClient;

    @InjectMocks
    private ChatsessieService chatsessieService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testStartSession() {
        Chatsessie newSession = Chatsessie.builder()
                .id("sessionId")
                .history(new History("historyId", List.of()))
                .build();
        when(chatsessieRepository.save(any(Chatsessie.class))).thenReturn(newSession);
        String sessionId = chatsessieService.startSession();
        assertNotNull(sessionId);
        assertEquals("sessionId", sessionId);
        verify(chatsessieRepository, times(1)).save(any(Chatsessie.class));
    }

    @Test
    void testSendMessage_SessionNotFound() {
        String sessionId = "nonExistentSessionId";
        when(chatsessieRepository.findById(sessionId)).thenReturn(Optional.empty());
        ChatsessieRequest request = new ChatsessieRequest(sessionId, "Hallo");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            chatsessieService.sendMessage(request);
        });

        assertEquals("Chatsessie met ID 'nonExistentSessionId' niet gevonden.", exception.getMessage());
        verify(chatsessieRepository, times(1)).findById(sessionId);
        verify(chatsessieRepository, never()).save(any());
        verify(pythonChatbotClient, never()).sendMessageToChatbot(any());
    }

    @Test
    void testSendMessage_unknownType_shouldAppendOnlyResponse()  {
        // Arrange
        String sessionId = "sess3";
        History initialHistory = new History(sessionId, new ArrayList<>());
        Chatsessie session = Chatsessie.builder()
                .id(sessionId)
                .history(initialHistory)
                .build();
        when(chatsessieRepository.findById(sessionId)).thenReturn(Optional.of(session));

        ChatsessieRequest req = new ChatsessieRequest(sessionId, "Tell me something");

        String jsonResponse = objectMapper.createObjectNode()
                .put("response", "Just info")
                .put("type", "general_info")
                .toString();
        when(pythonChatbotClient.sendMessageToChatbot(anyMap())).thenReturn(jsonResponse);

        // Act
        chatsessieService.sendMessage(req);

        // Assert
        List<HistoryItem> items = session.getHistory().getHistoryItems();
        assertEquals(2, items.size());
        HistoryItem assistant = items.get(1);
        String content = assistant.getContent();
        assertTrue(content.contains("Just info"));
        assertFalse(content.contains("start"));
        assertFalse(content.contains("end"));
        assertFalse(content.contains("events"));
        assertEquals("general_info", assistant.getType());
    }

    @Test
    void testSendMessage_payloadContainsMessageAndHistory() {
        // Arrange
        String sessionId = "sess4";
        History initialHistory = new History(sessionId, new ArrayList<>());
        Chatsessie session = Chatsessie.builder()
                .id(sessionId)
                .history(initialHistory)
                .build();
        when(chatsessieRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // Pre-populate history with one item
        HistoryItem preItem = new HistoryItem(Role.USER, "Hi");
        session.getHistory().addItem(preItem);

        ChatsessieRequest req = new ChatsessieRequest(sessionId, "How are you?");

        String jsonResponse = objectMapper.createObjectNode()
                .put("response", "OK")
                .put("type", "general_info")
                .toString();
        when(pythonChatbotClient.sendMessageToChatbot(anyMap())).thenReturn(jsonResponse);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        // Act
        chatsessieService.sendMessage(req);

        // Assert
        verify(pythonChatbotClient).sendMessageToChatbot(captor.capture());
        Map<String, Object> sentPayload = captor.getValue();
        assertEquals("How are you?", sentPayload.get("message"));
        assertTrue(sentPayload.containsKey("history"));
        @SuppressWarnings("unchecked")
        List<HistoryItem> sentHistory = (List<HistoryItem>) sentPayload.get("history");
        assertEquals(3, sentHistory.size());
        assertEquals(preItem, sentHistory.get(0));
        assertEquals(req.getMessage(), sentHistory.get(1).getContent());
    }

    @Test
    void testSendMessage_inputIsSanitizedBeforeSending() {
        // Arrange
        String sessionId = "sanitize1";
        History history = new History(sessionId, new ArrayList<>());
        Chatsessie session = Chatsessie.builder().id(sessionId).history(history).build();
        when(chatsessieRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // Deze input bevat HTML, script en verboden tekens
        String maliciousInput = "<script>alert('XSS');</script><b>Hello</b>\"';`";
        String expectedSanitized = "alert(XSS)Hello"; //

        ChatsessieRequest request = new ChatsessieRequest(sessionId, maliciousInput);

        String jsonResponse = objectMapper.createObjectNode()
                .put("response", "response text")
                .put("type", "general_info")
                .toString();
        when(pythonChatbotClient.sendMessageToChatbot(anyMap())).thenReturn(jsonResponse);

        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);

        // Act
        chatsessieService.sendMessage(request);

        // Assert
        verify(pythonChatbotClient).sendMessageToChatbot(payloadCaptor.capture());
        Map<String, Object> sentPayload = payloadCaptor.getValue();

        assertEquals(expectedSanitized, sentPayload.get("message"));
    }

    @Test
    void testSendMessage_emptyInput_throwsException() {
        String sessionId = "sessEmpty";
        Chatsessie session = Chatsessie.builder()
                .id(sessionId)
                .history(new History(sessionId, new ArrayList<>()))
                .build();
        when(chatsessieRepository.findById(sessionId)).thenReturn(Optional.of(session));

        ChatsessieRequest emptyReq = new ChatsessieRequest(sessionId, "   ");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            chatsessieService.sendMessage(emptyReq);
        });

        assertEquals("Bericht mag niet leeg zijn.", exception.getMessage());
        verify(pythonChatbotClient, never()).sendMessageToChatbot(any());
    }

    @Test
    void testSendMessage_inputLongerThanLimit_isTruncated()  {
        // Arrange
        String sessionId = "longInput";
        Chatsessie session = Chatsessie.builder()
                .id(sessionId)
                .history(new History(sessionId, new ArrayList<>()))
                .build();
        when(chatsessieRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // Maak input > 1000 karakters
        String longMessage = "a".repeat(1500);
        ChatsessieRequest req = new ChatsessieRequest(sessionId, longMessage);

        String jsonResponse = objectMapper.createObjectNode()
                .put("response", "OK")
                .put("type", "general_info")
                .toString();
        when(pythonChatbotClient.sendMessageToChatbot(anyMap())).thenReturn(jsonResponse);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        // Act
        chatsessieService.sendMessage(req);

        // Assert
        verify(pythonChatbotClient).sendMessageToChatbot(captor.capture());
        Map<String, Object> sentPayload = captor.getValue();
        String sanitized = (String) sentPayload.get("message");

        assertEquals(1000, sanitized.length());
    }



    @Test
    void testEndSession_noException() {
        assertDoesNotThrow(() -> chatsessieService.endSession());
    }

}
package be.pxl.services.controller;

import be.pxl.services.domain.dto.ChatsessieRequest;
import be.pxl.services.domain.dto.ChatsessieResponse;
import be.pxl.services.services.IChatsessieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatsessieControllerTest {

    @Mock
    private IChatsessieService chatsessieService;

    @InjectMocks
    private ChatsessieController chatsessieController;

    @BeforeEach
    void setUp() {
        // Deze methode is momenteel leeg omdat er geen setup nodig is
        // voordat elke test wordt uitgevoerd. Wordt hier toegevoegd voor
        // toekomstige uitbreidingen of configuraties.
    }


    @Test
    void testStartNewSession_Success() {
        String sessionId = "12345";
        when(chatsessieService.startSession()).thenReturn(sessionId);

        ResponseEntity<String> response = chatsessieController.startNewSession();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sessionId, response.getBody());
        verify(chatsessieService, times(1)).startSession();
        verifyNoMoreInteractions(chatsessieService);
    }

    @Test
    void testSendChat_Success() {
        ChatsessieRequest request = new ChatsessieRequest("12345", "Hello, how are you?");
        ChatsessieResponse serviceResponse = new ChatsessieResponse("12345", null);
        when(chatsessieService.sendMessage(any(ChatsessieRequest.class))).thenReturn(serviceResponse);

        ResponseEntity<ChatsessieResponse> response = chatsessieController.sendChat(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceResponse, response.getBody());
        verify(chatsessieService, times(1)).sendMessage(request);
        verifyNoMoreInteractions(chatsessieService);
    }

    @Test
    void testSendChat_EmptyMessage_ThrowsException() {

        ChatsessieRequest request = new ChatsessieRequest("12345", "");
        when(chatsessieService.sendMessage(any(ChatsessieRequest.class)))
                .thenThrow(new IllegalArgumentException("Bericht mag niet leeg zijn."));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> chatsessieController.sendChat(request));
        assertEquals("Bericht mag niet leeg zijn.", exception.getMessage());
        verify(chatsessieService, times(1)).sendMessage(request);
        verifyNoMoreInteractions(chatsessieService);
    }
}
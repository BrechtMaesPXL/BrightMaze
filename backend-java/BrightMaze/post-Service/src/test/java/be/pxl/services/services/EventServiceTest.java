package be.pxl.services.services;

import be.pxl.services.client.PythonChatbotClient;
import be.pxl.services.domain.EventImage;
import be.pxl.services.domain.dto.EventRequest;
import be.pxl.services.domain.dto.EventResponse;
import be.pxl.services.domain.dto.EventWithImageDTO;
import be.pxl.services.repository.EventImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private PythonChatbotClient pythonChatbotClient;

    @Mock
    private EventImageRepository eventImageRepository;

    private EventService eventService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Manually instantiate service with mocks
        eventService = new EventService(pythonChatbotClient, eventImageRepository);
        // set the upload directory to a temporary folder
        eventService.uploadDir = tempDir.toString();
    }

    @Test
    void createEvent_withoutFile_shouldSaveImageAndDelegateCall() {
        // Arrange
        EventRequest request = mock(EventRequest.class);
        when(request.toMap()).thenReturn(Map.of("key", "value"));
        // stub save to assign an ID
        when(eventImageRepository.save(any(EventImage.class))).thenAnswer(invocation -> {
            EventImage img = invocation.getArgument(0);
            img.setId("img-id-123");
            return img;
        });
        EventResponse stubResponse = new EventResponse();
        when(pythonChatbotClient.createEvent(anyMap(), eq("img-id-123"))).thenReturn(stubResponse);

        // Act
        EventResponse response = eventService.createEvent(request, null);

        // Assert
        ArgumentCaptor<EventImage> imageCaptor = ArgumentCaptor.forClass(EventImage.class);
        verify(eventImageRepository).save(imageCaptor.capture());
        EventImage savedImage = imageCaptor.getValue();
        assertNotNull(savedImage);
        assertNull(savedImage.getImagePath(), "No file, so path should be null");
        verify(pythonChatbotClient).createEvent(request.toMap(), "img-id-123");
        assertSame(stubResponse, response);
    }

    @Test
    void createEvent_withFile_shouldStoreAndDelegate() throws Exception {
        String filename = "test.png";
        byte[] content = "dummy-content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", filename, MediaType.IMAGE_PNG_VALUE, content);
        EventRequest request = mock(EventRequest.class);
        when(request.toMap()).thenReturn(Collections.emptyMap());
        when(eventImageRepository.save(any(EventImage.class))).thenAnswer(invocation -> {
            EventImage img = invocation.getArgument(0);
            img.setId("file-id-456");
            return img;
        });
        EventResponse stubResponse = new EventResponse();
        when(pythonChatbotClient.createEvent(anyMap(), eq("file-id-456"))).thenReturn(stubResponse);

        EventResponse response = eventService.createEvent(request, file);

        Path stored = tempDir.resolve(filename);
        assertTrue(Files.exists(stored), "File should be stored");
        assertArrayEquals(content, Files.readAllBytes(stored));
        ArgumentCaptor<EventImage> imageCaptor = ArgumentCaptor.forClass(EventImage.class);
        verify(eventImageRepository).save(imageCaptor.capture());
        assertEquals(filename, imageCaptor.getValue().getImagePath());
        verify(pythonChatbotClient).createEvent(anyMap(), eq("file-id-456"));
        assertSame(stubResponse, response);
    }

    @Test
    void getEventById_shouldDelegateToPythonClient() {
        EventResponse stub = new EventResponse();
        when(pythonChatbotClient.getEventById("id1")).thenReturn(stub);

        EventResponse result = eventService.getEventById("id1");

        assertSame(stub, result);
        verify(pythonChatbotClient).getEventById("id1");
    }

    @Test
    void getEventByIdWithImage_shouldReturnDTOWithImage() throws Exception {
        String id = "evt1";
        EventResponse resp = new EventResponse();
        when(pythonChatbotClient.getEventById(id)).thenReturn(resp);
        EventImage eventImage = new EventImage();
        eventImage.setId(id);
        eventImage.setImagePath("pic.jpg");
        when(eventImageRepository.findById(id)).thenReturn(Optional.of(eventImage));
        byte[] fileData = "img-data".getBytes();
        Files.write(tempDir.resolve("pic.jpg"), fileData);

        EventWithImageDTO dto = eventService.getEventByIdWithImage(id);

        assertSame(resp, dto.getEvent());
        String encoded = Base64.getEncoder().encodeToString(fileData);
        assertEquals(encoded, dto.getBase64Image());
    }

    @Test
    void deleteEvent_shouldRemoveFileAndDeleteEntities() throws Exception {
        String id = "del1";
        EventImage eventImage = new EventImage();
        eventImage.setId(id);
        eventImage.setImagePath("to-delete.txt");
        when(eventImageRepository.findById(id)).thenReturn(Optional.of(eventImage));
        Path filePath = tempDir.resolve("to-delete.txt");
        Files.write(filePath, "x".getBytes());

        eventService.deleteEvent(id);

        assertFalse(Files.exists(filePath));
        verify(eventImageRepository).deleteById(id);
        verify(pythonChatbotClient).deleteEvent(id);
    }

    @Test
    void updateEvent_withNewFile_shouldReplaceImage() throws Exception {
        String id = "upd1";
        EventImage existing = new EventImage();
        existing.setId(id);
        existing.setImagePath("old.png");
        Files.write(tempDir.resolve("old.png"), "old".getBytes());
        when(eventImageRepository.findById(id)).thenReturn(Optional.of(existing));
        EventResponse stubResp = new EventResponse();
        when(pythonChatbotClient.updateEvent(eq(id), anyMap())).thenReturn(stubResp);
        MockMultipartFile newFile = new MockMultipartFile("file", "new.png", "*/*", "new".getBytes());

        EventResponse result = eventService.updateEvent(id, mock(EventRequest.class), newFile);

        assertFalse(Files.exists(tempDir.resolve("old.png")));
        assertTrue(Files.exists(tempDir.resolve("new.png")));
        verify(eventImageRepository).save(existing);
        assertEquals("new.png", existing.getImagePath());
        assertSame(stubResp, result);
    }

    @Test
    void updateEvent_withoutNewFile_shouldKeepExistingImage() {
        String id = "upd2";
        EventImage existing = new EventImage();
        existing.setId(id);
        existing.setImagePath("stay.png");
        when(eventImageRepository.findById(id)).thenReturn(Optional.of(existing));
        EventResponse stubResp = new EventResponse();
        when(pythonChatbotClient.updateEvent(eq(id), anyMap())).thenReturn(stubResp);
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "*/*", new byte[0]);

        EventResponse result = eventService.updateEvent(id, mock(EventRequest.class), emptyFile);

        verify(eventImageRepository).save(existing);
        assertEquals("stay.png", existing.getImagePath());
        assertSame(stubResp, result);
    }

    @Test
    void getEventsWithImages_shouldProduceCorrectDTOs() throws Exception {
        EventResponse e1 = new EventResponse(); e1.setId("1");
        EventResponse e2 = new EventResponse(); e2.setId("2");
        when(pythonChatbotClient.getAllEvents()).thenReturn(List.of(e1, e2));
        EventImage img1 = new EventImage(); img1.setId("1"); img1.setImagePath("a.txt");
        EventImage img2 = new EventImage(); img2.setId("2"); img2.setImagePath("");
        when(eventImageRepository.findById("1")).thenReturn(Optional.of(img1));
        when(eventImageRepository.findById("2")).thenReturn(Optional.of(img2));
        byte[] data1 = "123".getBytes();
        Files.write(tempDir.resolve("a.txt"), data1);

        List<EventWithImageDTO> dtos = eventService.getEventsWithImages();

        assertEquals(2, dtos.size());
        EventWithImageDTO dto1 = dtos.get(0);
        assertEquals(e1, dto1.getEvent());
        assertEquals(Base64.getEncoder().encodeToString(data1), dto1.getBase64Image());
        EventWithImageDTO dto2 = dtos.get(1);
        assertEquals(e2, dto2.getEvent());
        assertNull(dto2.getBase64Image());
    }
}

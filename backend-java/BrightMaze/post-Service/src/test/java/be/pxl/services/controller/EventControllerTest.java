package be.pxl.services.controller;

import be.pxl.services.domain.dto.EventRequest;
import be.pxl.services.domain.dto.EventResponse;
import be.pxl.services.domain.dto.EventWithImageDTO;
import be.pxl.services.services.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    void testCreateEventWithFile() throws Exception {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setEventName("Event Title");
        eventRequest.setLocation("Location");
        eventRequest.setEventDescription("Description");
        eventRequest.setStartDate(LocalDateTime.now());
        eventRequest.setEndDate(LocalDateTime.now().plusDays(1));

        EventResponse eventResponse = new EventResponse(
                UUID.randomUUID().toString(),
                eventRequest.getEventName(),
                eventRequest.getStartDate(),
                eventRequest.getEndDate(),
                eventRequest.getLocation(),
                eventRequest.getEventDescription()
        );

        MockMultipartFile eventPart = new MockMultipartFile("event", "", "application/json",
                objectMapper.writeValueAsBytes(eventRequest));
        MockMultipartFile filePart = new MockMultipartFile("file", "test.jpg", "image/jpeg",
                "dummy image content".getBytes());

        when(eventService.createEvent(any(EventRequest.class), any())).thenReturn(eventResponse);

        mockMvc.perform(multipart("/api/events")
                        .file(eventPart)
                        .file(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventName").value("Event Title"))
                .andExpect(jsonPath("$.location").value("Location"))
                .andExpect(jsonPath("$.eventDescription").value("Description"));

        verify(eventService).createEvent(any(EventRequest.class), any());
    }

    @Test
    void testCreateEventWithoutFile() throws Exception {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setEventName("NoFileEvent");
        eventRequest.setLocation("Location");
        eventRequest.setEventDescription("Description");
        eventRequest.setStartDate(LocalDateTime.now());
        eventRequest.setEndDate(LocalDateTime.now().plusDays(1));

        MockMultipartFile eventPart = new MockMultipartFile("event", "", "application/json",
                objectMapper.writeValueAsBytes(eventRequest));

        EventResponse response = new EventResponse(UUID.randomUUID().toString(),
                eventRequest.getEventName(),
                eventRequest.getStartDate(),
                eventRequest.getEndDate(),
                eventRequest.getLocation(),
                eventRequest.getEventDescription());

        when(eventService.createEvent(any(EventRequest.class), isNull())).thenReturn(response);

        mockMvc.perform(multipart("/api/events")
                        .file(eventPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventName").value("NoFileEvent"));

        verify(eventService).createEvent(any(EventRequest.class), isNull());
    }

    @Test
    void testGetEventById() throws Exception {
        String id = UUID.randomUUID().toString();
        EventResponse response = new EventResponse(id, "Title", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Loc", "Desc");
        when(eventService.getEventById(id)).thenReturn(response);

        mockMvc.perform(get("/api/events/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.eventName").value("Title"));

        verify(eventService).getEventById(id);
    }

    @Test
    void testGetEventByIdWithImage() throws Exception {
        String id = UUID.randomUUID().toString();
        EventWithImageDTO dto = new EventWithImageDTO();
        when(eventService.getEventByIdWithImage(id)).thenReturn(dto);

        mockMvc.perform(get("/api/events/{id}/withImage", id))
                .andExpect(status().isOk());

        verify(eventService).getEventByIdWithImage(id);
    }

    @Test
    void testGetAllEvents() throws Exception {
        List<EventWithImageDTO> events = List.of(new EventWithImageDTO(), new EventWithImageDTO());
        when(eventService.getEventsWithImages()).thenReturn(events);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(eventService).getEventsWithImages();
    }

    @Test
    void testUpdateEventWithFile() throws Exception {
        String id = UUID.randomUUID().toString();
        EventRequest eventRequest = new EventRequest();
        eventRequest.setEventName("Updated Title");
        eventRequest.setLocation("Updated Location");
        eventRequest.setEventDescription("Updated Desc");
        eventRequest.setStartDate(LocalDateTime.now());
        eventRequest.setEndDate(LocalDateTime.now().plusDays(1));

        EventResponse response = new EventResponse(id,
                eventRequest.getEventName(),
                eventRequest.getStartDate(),
                eventRequest.getEndDate(),
                eventRequest.getLocation(),
                eventRequest.getEventDescription());

        MockMultipartFile eventPart = new MockMultipartFile("event", "", "application/json",
                objectMapper.writeValueAsBytes(eventRequest));
        MockMultipartFile filePart = new MockMultipartFile("file", "update.jpg", "image/jpeg",
                "update image content".getBytes());

        when(eventService.updateEvent(eq(id), any(EventRequest.class), any())).thenReturn(response);

        mockMvc.perform(multipart("/api/events/{id}", id)
                        .file(eventPart)
                        .file(filePart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Updated Title"))
                .andExpect(jsonPath("$.location").value("Updated Location"));

        verify(eventService).updateEvent(eq(id), any(EventRequest.class), any());
    }

    @Test
    void testUpdateEventWithoutFile() throws Exception {
        String id = UUID.randomUUID().toString();
        EventRequest eventRequest = new EventRequest();
        eventRequest.setEventName("Updated Title No File");
        eventRequest.setLocation("Loc");
        eventRequest.setEventDescription("Desc");
        eventRequest.setStartDate(LocalDateTime.now());
        eventRequest.setEndDate(LocalDateTime.now().plusDays(1));

        EventResponse response = new EventResponse(id,
                eventRequest.getEventName(),
                eventRequest.getStartDate(),
                eventRequest.getEndDate(),
                eventRequest.getLocation(),
                eventRequest.getEventDescription());

        MockMultipartFile eventPart = new MockMultipartFile("event", "", "application/json",
                objectMapper.writeValueAsBytes(eventRequest));

        when(eventService.updateEvent(eq(id), any(EventRequest.class), isNull())).thenReturn(response);

        mockMvc.perform(multipart("/api/events/{id}", id)
                        .file(eventPart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Updated Title No File"));

        verify(eventService).updateEvent(eq(id), any(EventRequest.class), isNull());
    }

    @Test
    void testDeleteEvent() throws Exception {
        String id = UUID.randomUUID().toString();

        doNothing().when(eventService).deleteEvent(id);

        mockMvc.perform(delete("/api/events/{id}", id))
                .andExpect(status().isNoContent());

        verify(eventService).deleteEvent(id);
    }
}

package be.pxl.services.controller;

import be.pxl.services.domain.dto.EventRequest;
import be.pxl.services.domain.dto.EventResponse;
import be.pxl.services.domain.dto.EventWithImageDTO;
import be.pxl.services.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<EventResponse> createEvent(
            @RequestPart("event") @Valid EventRequest eventRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        EventResponse response = eventService.createEvent(eventRequest, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable String id) {
        EventResponse response = eventService.getEventById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}/withImage")
    public ResponseEntity<EventWithImageDTO> getEventByIdWithImage(@PathVariable String id) {
        EventWithImageDTO response = eventService.getEventByIdWithImage(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EventWithImageDTO>> getAllEvents() {
        List<EventWithImageDTO> events = eventService.getEventsWithImages();
        return ResponseEntity.ok(events);
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable String id,
            @RequestPart("event") @Valid EventRequest eventRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        log.info("Received update for event: {}", eventRequest);
        EventResponse response = eventService.updateEvent(id, eventRequest, file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        eventService.deleteEvent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package be.pxl.services.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;

import be.pxl.services.domain.dto.EventResponse;

@FeignClient(name = "python-chatbot", url = "${python.service.url}")
public interface PythonChatbotClient {
    @PostMapping("/chat")
    String sendMessageToChatbot(@RequestBody Map<String, Object> requestPayload);
    @PostMapping("/events")
    EventResponse createEvent(@RequestBody Map<String, Object> eventPayload, @RequestParam("id") String id);
    @GetMapping("/events")
    List<EventResponse> getAllEvents();
    @GetMapping("/events/{event_id}")
    EventResponse getEventById(@PathVariable("event_id") String id);
    @PutMapping("/events/{event_id}")
    EventResponse updateEvent(@PathVariable("event_id") String eventId, @RequestBody Map<String, Object> eventPayload);
    @DeleteMapping("/events/{event_id}")
    void deleteEvent(@PathVariable("event_id") String id);
}
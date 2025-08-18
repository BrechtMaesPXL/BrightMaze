package be.pxl.services.services;

import be.pxl.services.domain.dto.EventRequest;
import be.pxl.services.domain.dto.EventResponse;
import be.pxl.services.domain.dto.EventWithImageDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface IEventService {
    /**
     * Create a new event and send the event data to the Python chatbot service.
     * The Python service will handle the event creation in its own database.
     * @param eventRequest Event data to create
     * @param file Image file to upload
     * @return Created event response
     */
    EventResponse createEvent(EventRequest eventRequest, MultipartFile file);
    /**
     * Get all events from the Python chatbot service.
     * 
     * @param id Event ID
     * @return List of event responses with images
     */
    EventResponse getEventById(String id);
    /**
    * Update event image in the local database and send the updated event data to
    * the Python chatbot service. The Python service will handle the event update
    * in its own database.
    * @param id Event ID
    * @param eventRequest Event data to update
    * @param file New image file to upload
    * @return Updated event response
    * @throws IllegalArgumentException if the event ID is not found
    */
    EventResponse updateEvent(String id, EventRequest eventRequest, MultipartFile file);
    /**
     * Delete event image in the local database and send the event ID to the Python
     * chatbot service. The Python service will handle the event deletion in its
     * own database.
     * @param id Event ID
     * @throws IllegalArgumentException if the event ID is not found
     */
    void deleteEvent(String id);
    /**
     * Get all events from the Python chatbot service and their images from the local
     * file system. Combine both into a DTO.
     * @return List of event responses with images
     * @throws IllegalArgumentException if the event ID is not found
     */
    List<EventWithImageDTO> getEventsWithImages();
    /**
     * Get event by ID from the Python chatbot service and its image from the local
     * file system. Combine both into a DTO.
     * @param id Event ID
     * @return Event response with image
     * @throws IllegalArgumentException if an event ID is not found
     */
    EventWithImageDTO getEventByIdWithImage(String id);

}
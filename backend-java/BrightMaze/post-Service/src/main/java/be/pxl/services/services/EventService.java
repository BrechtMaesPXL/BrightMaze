package be.pxl.services.services;

import be.pxl.services.client.PythonChatbotClient;
import be.pxl.services.domain.EventImage;
import be.pxl.services.domain.dto.EventRequest;
import be.pxl.services.domain.dto.EventResponse;
import be.pxl.services.domain.dto.EventWithImageDTO;
import be.pxl.services.exception.FileStorageException;
import be.pxl.services.repository.EventImageRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    private final PythonChatbotClient pythonChatbotClient;
    private final EventImageRepository eventImageRepository;

    private static final String EVENT_IMAGE_WITH_ID = "EventImage with ID ";
    private static final String NOT_FOUND_TEXT = " not found.";

    @Value("${upload.dir}")
    protected String uploadDir;

    @Override
    public EventResponse createEvent(EventRequest eventRequest, MultipartFile file) {
        EventImage eventImage = new EventImage();
        if (file != null && !file.isEmpty()) {
            String fileName = storeFile(file);
            eventImage.setImagePath(fileName);
        }
        eventImageRepository.save(eventImage);
        return pythonChatbotClient.createEvent(eventRequest.toMap(), eventImage.getId());
    }

    protected String storeFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new FileStorageException("Bestandsnaam is ongeldig.");
            }

            Path path = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (IOException e) {
            String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "onbekend bestand";
            logger.error("Upload mislukt voor bestand '{}': {}", fileName, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public EventResponse getEventById(String id) {
        return pythonChatbotClient.getEventById(id);
    }

    @Override
    public EventWithImageDTO getEventByIdWithImage(String id) {
        EventResponse eventResponse = getEventById(id);
        EventImage eventImage = eventImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_IMAGE_WITH_ID + id + NOT_FOUND_TEXT));

        EventWithImageDTO dto = new EventWithImageDTO();
        dto.setEvent(eventResponse);

        String imagePath = eventImage.getImagePath();
        if (imagePath != null) {
            try {
                Path path = Paths.get(uploadDir, imagePath);
                if (Files.exists(path)) {
                    byte[] content = Files.readAllBytes(path);
                    String base64Image = Base64.getEncoder().encodeToString(content);
                    dto.setBase64Image(base64Image);
                }
            } catch (IOException e) {
                logger.error("Fout bij lezen van afbeelding voor event '{}': {}", id, e.getMessage(), e);
            }
        }

        return dto;
    }

    @Override
    public void deleteEvent(String id) {
        EventImage eventImage = eventImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_IMAGE_WITH_ID + id + NOT_FOUND_TEXT));

        if (eventImage.getImagePath() != null) {
            deleteFile(eventImage.getImagePath());
        }
        eventImageRepository.deleteById(id);
        pythonChatbotClient.deleteEvent(id);
    }

    protected void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Fout bij verwijderen bestand '{}': {}", fileName, e.getMessage(), e);
        }
    }

    @Override
    public EventResponse updateEvent(String id, EventRequest eventRequest, MultipartFile file) {
        EventImage existingEvent = eventImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_IMAGE_WITH_ID + id + NOT_FOUND_TEXT));

        EventResponse updatedEvent = pythonChatbotClient.updateEvent(id, eventRequest.toMap());

        if (file != null && !file.isEmpty()) {
            if (existingEvent.getImagePath() != null) {
                deleteFile(existingEvent.getImagePath());
            }
            String fileName = storeFile(file);
            existingEvent.setImagePath(fileName);
        }

        eventImageRepository.save(existingEvent);
        return updatedEvent;
    }

    @Override
    public List<EventWithImageDTO> getEventsWithImages() {
        List<EventResponse> eventResponses = pythonChatbotClient.getAllEvents();
        List<EventWithImageDTO> dtos = new ArrayList<>();

        for (EventResponse event : eventResponses) {
            EventWithImageDTO dto = new EventWithImageDTO();
            EventImage eventImage = eventImageRepository.findById(event.getId())
                    .orElseThrow(() -> new IllegalArgumentException(EVENT_IMAGE_WITH_ID + event.getId() + NOT_FOUND_TEXT));

            dto.setEvent(event);

            try {
                Path path = Paths.get(uploadDir, eventImage.getImagePath());
                if (Files.exists(path)) {
                    byte[] content = Files.readAllBytes(path);
                    String base64Image = Base64.getEncoder().encodeToString(content);
                    dto.setBase64Image(base64Image);
                } else {
                    dto.setBase64Image(null);
                }
            } catch (IOException e) {
                logger.error("Fout bij lezen van afbeelding voor event '{}': {}", event.getId(), e.getMessage(), e);
                dto.setBase64Image(null);
            }

            dtos.add(dto);
        }

        return dtos;
    }
}

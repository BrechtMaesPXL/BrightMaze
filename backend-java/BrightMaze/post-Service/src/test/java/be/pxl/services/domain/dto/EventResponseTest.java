package be.pxl.services.domain.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventResponseTest {

    @Test
    void testValidEventResponse() {
        EventResponse eventResponse = EventResponse.builder()
                .id("12345")
                .eventName("Tech Conference 2025")
                .startDate(LocalDateTime.of(2025, 5, 15, 9, 0, 0))
                .endDate(LocalDateTime.of(2025, 5, 15, 17, 0, 0))
                .endDate(LocalDateTime.of(2025, 5, 15, 17, 0, 0))
                .location("Corda 1")
                .eventDescription("A day-long event featuring talks and workshops from leaders in the tech industry.")
                .build();

        assertNotNull(eventResponse.getId(), "ID should not be null");
        assertEquals("12345", eventResponse.getId(), "ID should match");

        assertNotNull(eventResponse.getEventName(), "Event name should not be null");
        assertEquals("Tech Conference 2025", eventResponse.getEventName(), "Event name should match");

        assertNotNull(eventResponse.getStartDate(), "Start date should not be null");
        assertEquals(LocalDateTime.of(2025, 5, 15, 9, 0, 0), eventResponse.getStartDate(), "Start date should match");

        assertNotNull(eventResponse.getEndDate(), "End date should not be null");
        assertEquals(LocalDateTime.of(2025, 5, 15, 17, 0, 0), eventResponse.getEndDate(), "End date should match");

        assertNotNull(eventResponse.getLocation(), "Location should not be null");
        assertEquals("Corda 1", eventResponse.getLocation(), "Location should match");

        assertNotNull(eventResponse.getEventDescription(), "Event description should not be null");
        assertEquals("A day-long event featuring talks and workshops from leaders in the tech industry.",
                eventResponse.getEventDescription(), "Event description should match");
    }

    @Test
    void testEventResponseWithNullValues() {
        EventResponse eventResponse = EventResponse.builder()
                .id("67890")
                .eventName(null)
                .startDate(null)
                .endDate(null)
                .location(null)
                .eventDescription(null)
                .build();

        assertNotNull(eventResponse.getId(), "ID should not be null");
        assertEquals("67890", eventResponse.getId(), "ID should match");

        assertNull(eventResponse.getEventName(), "Event name should be null");
        assertNull(eventResponse.getStartDate(), "Start date should be null");
        assertNull(eventResponse.getEndDate(), "End date should be null");
        assertNull(eventResponse.getLocation(), "Location should be null");
        assertNull(eventResponse.getEventDescription(), "Event description should be null");
    }

    @Test
    void testEmptyEventResponse() {
        EventResponse eventResponse = new EventResponse();

        assertNull(eventResponse.getId(), "ID should be null");
        assertNull(eventResponse.getEventName(), "Event name should be null");
        assertNull(eventResponse.getStartDate(), "Start date should be null");
        assertNull(eventResponse.getEndDate(), "End date should be null");
        assertNull(eventResponse.getLocation(), "Location should be null");
        assertNull(eventResponse.getEventDescription(), "Event description should be null");
    }

    @Test
    void testEventResponseEquality() {
        EventResponse eventResponse1 = EventResponse.builder()
                .id("11111")
                .eventName("AI Conference 2025")
                .startDate(LocalDateTime.of(2025, 6, 20, 9, 0, 0))
                .endDate(LocalDateTime.of(2025, 6, 20, 17, 0, 0))
                .location("Corda 2")
                .eventDescription("A conference focused on the latest trends in AI technology.")
                .build();

        EventResponse eventResponse2 = EventResponse.builder()
                .id("11111")
                .eventName("AI Conference 2025")
                .startDate(LocalDateTime.of(2025, 6, 20, 9, 0, 0))
                .endDate(LocalDateTime.of(2025, 6, 20, 17, 0, 0))
                .location("Corda 2")
                .eventDescription("A conference focused on the latest trends in AI technology.")
                .build();

        assertEquals(eventResponse1, eventResponse2, "The two EventResponse objects should be equal");
    }

    @Test
    void testEventResponseHashCode() {
        EventResponse eventResponse1 = EventResponse.builder()
                .id("22222")
                .eventName("Innovation Summit 2025")
                .startDate(LocalDateTime.of(2025, 7, 10, 9, 0, 0))
                .endDate(LocalDateTime.of(2025, 7, 10, 17, 0, 0))
                .location("Corda 3")
                .eventDescription("Summit focused on the latest innovations in various industries.")
                .build();

        EventResponse eventResponse2 = EventResponse.builder()
                .id("22222")
                .eventName("Innovation Summit 2025")
                .startDate(LocalDateTime.of(2025, 7, 10, 9, 0, 0))
                .endDate(LocalDateTime.of(2025, 7, 10, 17, 0, 0))
                .location("Corda 3")
                .eventDescription("Summit focused on the latest innovations in various industries.")
                .build();

        assertEquals(eventResponse1.hashCode(), eventResponse2.hashCode(), "The hash codes should be equal");
    }
}

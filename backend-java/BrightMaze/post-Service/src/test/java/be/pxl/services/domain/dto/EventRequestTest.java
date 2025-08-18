package be.pxl.services.domain.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventRequestTest {

    @Test
    void testValidEventRequest() {
        EventRequest eventRequest = EventRequest.builder()
                .eventName("Tech Conference 2025")
                .startDate(LocalDateTime.of(2025, 5, 15, 9, 0, 0))
                .endDate(LocalDateTime.of(2025, 5, 15, 17, 0, 0))
                .location("Corda 1")
                .eventDescription("A day-long event featuring talks and workshops from leaders in the tech industry.")
                .build();

        assertNotNull(eventRequest.getEventName(), "Event name should not be null");
        assertEquals("Tech Conference 2025", eventRequest.getEventName(), "Event name should match");

        assertNotNull(eventRequest.getStartDate(), "Start date should not be null");
        assertEquals(LocalDateTime.of(2025, 5, 15, 9, 0, 0), eventRequest.getStartDate(), "Start date should match");

        assertNotNull(eventRequest.getEndDate(), "End date should not be null");
        assertEquals(LocalDateTime.of(2025, 5, 15, 17, 0, 0), eventRequest.getEndDate(), "End date should match");

        assertNotNull(eventRequest.getLocation(), "Location should not be null");
        assertEquals("Corda 1", eventRequest.getLocation(), "Location should match");

        assertNotNull(eventRequest.getEventDescription(), "Event description should not be null");
        assertEquals("A day-long event featuring talks and workshops from leaders in the tech industry.",
                eventRequest.getEventDescription(), "Event description should match");
    }

    @Test
    void testInvalidEventRequest_emptyEventName() {
        EventRequest eventRequest = EventRequest.builder()
                .eventName("")
                .startDate(LocalDateTime.of(2025, 6, 10, 9, 0, 0))
                .endDate(LocalDateTime.of(2025, 6, 10, 17, 0, 0))
                .location("Corda 2")
                .eventDescription("A conference about AI and future technologies.")
                .build();

        assertEquals("", eventRequest.getEventName(), "Event name should be empty");
        assertNotNull(eventRequest.getStartDate(), "Start date should not be null");
        assertNotNull(eventRequest.getEndDate(), "End date should not be null");
        assertNotNull(eventRequest.getLocation(), "Location should not be null");
        assertEquals("Corda 2", eventRequest.getLocation(), "Location should match");
        assertNotNull(eventRequest.getEventDescription(), "Event description should not be null");
    }

    @Test
    void testInvalidEventRequest_nullStartDate() {
        EventRequest eventRequest = EventRequest.builder()
                .eventName("AI Summit 2025")
                .startDate(null)
                .endDate(LocalDateTime.of(2025, 7, 1, 17, 0, 0))
                .location("Corda 3")
                .eventDescription("An AI summit gathering experts from around the globe.")
                .build();

        assertNull(eventRequest.getStartDate(), "Start date should be null");
        assertNotNull(eventRequest.getEndDate(), "End date should not be null");
        assertNotNull(eventRequest.getLocation(), "Location should not be null");
        assertEquals("Corda 3", eventRequest.getLocation(), "Location should match");
        assertNotNull(eventRequest.getEventDescription(), "Event description should not be null");
    }

    @Test
    void testInvalidEventRequest_endDateBeforeStartDate() {
        EventRequest eventRequest = EventRequest.builder()
                .eventName("Global Business Summit 2025")
                .startDate(LocalDateTime.of(2025, 8, 20, 10, 0, 0))
                .endDate(LocalDateTime.of(2025, 8, 20, 8, 0, 0))
                .location("Corda 4")
                .eventDescription("A summit discussing the future of global business trends and opportunities.")
                .build();

        assertNotNull(eventRequest.getEventName(), "Event name should not be null");
        assertNotNull(eventRequest.getStartDate(), "Start date should not be null");
        assertNotNull(eventRequest.getEndDate(), "End date should not be null");
        assertTrue(eventRequest.getEndDate().isBefore(eventRequest.getStartDate()), "End date should be before start date");
        assertNotNull(eventRequest.getLocation(), "Location should not be null");
        assertEquals("Corda 4", eventRequest.getLocation(), "Location should match");
        assertNotNull(eventRequest.getEventDescription(), "Event description should not be null");
    }

    @Test
    void testInvalidEventRequest_locationOutOfRange() {
        EventRequest eventRequest = EventRequest.builder()
                .eventName("International Conference on Innovation 2025")
                .startDate(LocalDateTime.of(2025, 9, 12, 9, 0, 0))
                .endDate(LocalDateTime.of(2025, 9, 12, 17, 0, 0))
                .location("Corda 8")
                .eventDescription("An event showcasing the latest innovations in various industries.")
                .build();

        assertNotNull(eventRequest.getEventName(), "Event name should not be null");
        assertNotNull(eventRequest.getStartDate(), "Start date should not be null");
        assertNotNull(eventRequest.getEndDate(), "End date should not be null");
        assertEquals("Corda 8", eventRequest.getLocation(), "Location should be Corda 8");
        assertNotNull(eventRequest.getEventDescription(), "Event description should not be null");
    }
}

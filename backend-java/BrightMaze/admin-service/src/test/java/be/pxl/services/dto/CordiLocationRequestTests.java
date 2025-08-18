package be.pxl.services.dto;

import be.pxl.services.domain.dto.CordiLocationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CordiLocationRequestTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testDefaultConstructor() {
        CordiLocationRequest request = new CordiLocationRequest();
        assertNull(request.getCurrentBuilding(), "Current building should be null by default");
    }

    @Test
    void testAllArgsConstructor() {
        CordiLocationRequest request = new CordiLocationRequest("corda 1");
        assertEquals("corda 1", request.getCurrentBuilding(), "Current building should be set via constructor");
    }

    @Test
    void testBuilder() {
        CordiLocationRequest request = CordiLocationRequest.builder()
                .currentBuilding("corda arena")
                .build();
        assertEquals("corda arena", request.getCurrentBuilding(), "Current building should be set via builder");
    }

    @Test
    void testSettersAndGetters() {
        CordiLocationRequest request = new CordiLocationRequest();
        request.setCurrentBuilding("corda bar");
        assertEquals("corda bar", request.getCurrentBuilding(), "Getter should return value set by setter");
    }

    @Test
    void testJsonSerialization() throws Exception {
        CordiLocationRequest request = CordiLocationRequest.builder()
                .currentBuilding("corda 1")
                .build();
        String json = objectMapper.writeValueAsString(request);
        assertEquals("{\"current_building\":\"corda 1\"}", json, "JSON serialization should use correct property name");
    }

    @Test
    void testJsonDeserialization() throws Exception {
        String json = "{\"current_building\":\"corda arena\"}";
        CordiLocationRequest request = objectMapper.readValue(json, CordiLocationRequest.class);
        assertEquals("corda arena", request.getCurrentBuilding(), "JSON deserialization should set correct value");
    }

    @Test
    void testToString() {
        CordiLocationRequest request = new CordiLocationRequest("corda 1");
        String toString = request.toString();
        assertTrue(toString.contains("currentBuilding=corda 1"), "toString should include currentBuilding");
    }

    @Test
    void testEqualsAndHashCode() {
        CordiLocationRequest request1 = new CordiLocationRequest("corda 1");
        CordiLocationRequest request2 = new CordiLocationRequest("corda 1");
        CordiLocationRequest request3 = new CordiLocationRequest("corda 2");

        assertEquals(request1, request2, "Objects with same currentBuilding should be equal");
        assertNotEquals(request1, request3, "Objects with different currentBuilding should not be equal");
        assertEquals(request1.hashCode(), request2.hashCode(), "Hash codes should be equal for equal objects");
    }
}
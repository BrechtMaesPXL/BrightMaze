package be.pxl.services.services;

import be.pxl.services.client.PythonChatbotClient;
import be.pxl.services.domain.dto.CordiLocationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CordiLocationServiceTest {

    private PythonChatbotClient pythonChatbotClient;
    private CordiLocationService cordiLocationService;

    @BeforeEach
    void setUp() {
        pythonChatbotClient = Mockito.mock(PythonChatbotClient.class);
        cordiLocationService = new CordiLocationService(pythonChatbotClient);
    }

    @Test
    void sendLocationToFastApi_shouldReturnMessage() {
        CordiLocationRequest request = new CordiLocationRequest();
        request.setCurrentBuilding("BuildingA");

        Map<String, Object> fakeResponse = Map.of("message", "Location received");
        when(pythonChatbotClient.handleCurrentLocation(Map.of("current_building", "BuildingA"))).thenReturn(fakeResponse);

        String result = cordiLocationService.sendLocationToFastApi(request);

        assertThat(result).isEqualTo("Location received");
        verify(pythonChatbotClient, times(1)).handleCurrentLocation(Map.of("current_building", "BuildingA"));
    }

    @Test
    void getCurrentLocationFromFastApi_shouldReturnCurrentBuilding() {
        Map<String, Object> fakeResponse = Map.of("current_building", "BuildingB");
        when(pythonChatbotClient.getCurrentLocation()).thenReturn(fakeResponse);

        String result = cordiLocationService.getCurrentLocationFromFastApi();

        assertThat(result).isEqualTo("BuildingB");
        verify(pythonChatbotClient, times(1)).getCurrentLocation();
    }
}

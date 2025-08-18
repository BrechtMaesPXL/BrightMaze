package be.pxl.services.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.pxl.services.client.PythonChatbotClient;
import be.pxl.services.domain.dto.RouteSettingsRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteFunctionalityServiceTest {

    @Mock
    private PythonChatbotClient pythonChatbotClient;

    private RouteFunctionalityService routeFunctionalityService;

    @BeforeEach
    void setUp() {
        routeFunctionalityService = new RouteFunctionalityService(pythonChatbotClient);
    }

    @Test
    void setRouteEnabled_ShouldEnableRoute_WhenRequestIsTrue() {
        // Arrange
        RouteSettingsRequest request = new RouteSettingsRequest();
        request.setEnabled(true);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Route enabled successfully");

        when(pythonChatbotClient.handleRouteSettings(Map.of("enabled", true)))
                .thenReturn(response);

        // Act
        String result = routeFunctionalityService.setRouteEnabled(request);

        // Assert
        assertEquals("Route enabled successfully", result);
        verify(pythonChatbotClient).handleRouteSettings(Map.of("enabled", true));
    }

    @Test
    void setRouteEnabled_ShouldDisableRoute_WhenRequestIsFalse() {
        // Arrange
        RouteSettingsRequest request = new RouteSettingsRequest();
        request.setEnabled(false);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Route disabled successfully");

        when(pythonChatbotClient.handleRouteSettings(Map.of("enabled", false)))
                .thenReturn(response);

        // Act
        String result = routeFunctionalityService.setRouteEnabled(request);

        // Assert
        assertEquals("Route disabled successfully", result);
        verify(pythonChatbotClient).handleRouteSettings(Map.of("enabled", false));
    }

    @Test
    void setRouteEnabled_ShouldThrowNullPointerException_WhenResponseIsNull() {
        // Arrange
        RouteSettingsRequest request = new RouteSettingsRequest();
        request.setEnabled(true);

        when(pythonChatbotClient.handleRouteSettings(any()))
                .thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> 
            routeFunctionalityService.setRouteEnabled(request)
        );
    }

    @Test
    void setRouteEnabled_ShouldThrowNullPointerException_WhenMessageIsNull() {
        // Arrange
        RouteSettingsRequest request = new RouteSettingsRequest();
        request.setEnabled(true);

        Map<String, Object> response = new HashMap<>();
        // No message key present

        when(pythonChatbotClient.handleRouteSettings(any()))
                .thenReturn(response);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> 
            routeFunctionalityService.setRouteEnabled(request)
        );
    }

    @Test
    void getRouteEnabled_ShouldReturnTrue_WhenRouteIsEnabled() {
        // Arrange
        Map<String, Object> response = new HashMap<>();
        response.put("enabled", "true");

        when(pythonChatbotClient.getRouteSettings())
                .thenReturn(response);

        // Act
        String result = routeFunctionalityService.getRouteEnabled();

        // Assert
        assertEquals("true", result);
        verify(pythonChatbotClient).getRouteSettings();
    }

    @Test
    void getRouteEnabled_ShouldReturnFalse_WhenRouteIsDisabled() {
        // Arrange
        Map<String, Object> response = new HashMap<>();
        response.put("enabled", "false");

        when(pythonChatbotClient.getRouteSettings())
                .thenReturn(response);

        // Act
        String result = routeFunctionalityService.getRouteEnabled();

        // Assert
        assertEquals("false", result);
        verify(pythonChatbotClient).getRouteSettings();
    }

    @Test
    void getRouteEnabled_ShouldThrowNullPointerException_WhenResponseIsNull() {
        // Arrange
        when(pythonChatbotClient.getRouteSettings())
                .thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> 
            routeFunctionalityService.getRouteEnabled()
        );
    }

    @Test
    void getRouteEnabled_ShouldThrowNullPointerException_WhenEnabledIsNull() {
        // Arrange
        Map<String, Object> response = new HashMap<>();
        // No enabled key present

        when(pythonChatbotClient.getRouteSettings())
                .thenReturn(response);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> 
            routeFunctionalityService.getRouteEnabled()
        );
    }
}

package be.pxl.services.services;

import be.pxl.services.client.PythonChatbotClient;
import be.pxl.services.domain.dto.RouteSettingsRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RouteFunctionalityService implements IRouteFunctionalityService {

    private final PythonChatbotClient pythonChatbotClient;

    public RouteFunctionalityService(PythonChatbotClient pythonChatbotClient) {
        this.pythonChatbotClient = pythonChatbotClient;
    }

    @Override
    public String setRouteEnabled(RouteSettingsRequest request) {
        Map<String, Object> response = pythonChatbotClient.handleRouteSettings(
            Map.of("enabled", request.isEnabled())
        );
        return response.get("message").toString();
    }

    @Override
    public String getRouteEnabled() {
        Map<String, Object> response = pythonChatbotClient.getRouteSettings();
        return response.get("enabled").toString();
    }
}

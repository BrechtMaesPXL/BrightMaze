package be.pxl.services.services;

import be.pxl.services.client.PythonChatbotClient;
import be.pxl.services.domain.dto.CordiLocationRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CordiLocationService implements ICordiLocationService {

    private final PythonChatbotClient pythonChatbotClient;

    public CordiLocationService(PythonChatbotClient pythonChatbotClient) {
        this.pythonChatbotClient = pythonChatbotClient;
    }

    @Override
    public String sendLocationToFastApi(CordiLocationRequest request) {
        Map<String, Object> response = pythonChatbotClient.handleCurrentLocation(
            Map.of("current_building", request.getCurrentBuilding())
        );
        return response.get("message").toString();
    }

    @Override
    public String getCurrentLocationFromFastApi() {
        Map<String, Object> response = pythonChatbotClient.getCurrentLocation();
        return response.get("current_building").toString();
    }
}

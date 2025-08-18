package be.pxl.services.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@FeignClient(name = "python-chatbot", url = "${cordi.api.url}")
public interface PythonChatbotClient {
    @PostMapping("/current-location")
    Map<String, Object> handleCurrentLocation(@RequestBody Map<String, Object> request);
    @GetMapping("/current-location")
    Map<String, Object> getCurrentLocation();

    @PostMapping("/route-settings")
    Map<String, Object> handleRouteSettings(@RequestBody Map<String, Object> request);
    @GetMapping("/route-settings")
    Map<String, Object> getRouteSettings();
}
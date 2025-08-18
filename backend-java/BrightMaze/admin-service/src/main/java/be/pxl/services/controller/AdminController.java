package be.pxl.services.controller;


import be.pxl.services.services.ICordiLocationService;
import be.pxl.services.services.IRouteFunctionalityService;
import be.pxl.services.domain.dto.CordiLocationRequest;
import be.pxl.services.domain.dto.RouteSettingsRequest;
import be.pxl.services.domain.dto.VoiceSettingsRequest;
import be.pxl.services.services.ICordiLocationService;
import be.pxl.services.services.IRouteFunctionalityService;
import be.pxl.services.services.IVoiceFunctionalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class AdminController {
    
    private final ICordiLocationService locationService;
    private final IRouteFunctionalityService routeService;
    private final IVoiceFunctionalityService voiceFunctionalityService;

    public AdminController(ICordiLocationService locationService, IRouteFunctionalityService routeService, IVoiceFunctionalityService voiceFunctionalityService) {
        this.locationService = locationService;
        this.routeService = routeService;
        this.voiceFunctionalityService = voiceFunctionalityService;
    }

    @PostMapping("/current-location")
    public ResponseEntity<String> forwardLocation(@RequestBody CordiLocationRequest request) {
        try {
            String result = locationService.sendLocationToFastApi(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(("Fout bij verzenden naar FastAPI: " + e.getMessage()));
        }
    }

    @GetMapping("/current-location")
    public ResponseEntity<String> getCurrentLocation() {
        try {
            String result = locationService.getCurrentLocationFromFastApi();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(("Fout bij ophalen locatie uit FastAPI: " + e.getMessage()));
        }
    }

    @PostMapping("/route-settings")
    public ResponseEntity<String> setRouteEnabled(@RequestBody RouteSettingsRequest request) {
        try {
            String result = routeService.setRouteEnabled(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error setting route enabled: " + e.getMessage());
        }
    }

    @GetMapping("/route-settings")
    public ResponseEntity<String> getRouteEnabled() {
        try {
            String result = routeService.getRouteEnabled();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting route enabled status: " + e.getMessage());
        }
    }
    @PostMapping("/voice-settings")
    public ResponseEntity<?> setVoiceEnabled(@RequestBody VoiceSettingsRequest request) {
        try {
            voiceFunctionalityService.setvoiceEnabled(request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error setting voice enabled: " + e.getMessage());
        }
    }

    @GetMapping("/voice-settings")
    public ResponseEntity<?> getVoiceEnabled() {
        try {
            boolean result = voiceFunctionalityService.getVoiceEnabled();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting route enabled status: " + e.getMessage());
        }
    }
}

package be.pxl.services.controller;

import be.pxl.services.domain.dto.CordiLocationRequest;
import be.pxl.services.domain.dto.RouteSettingsRequest;
import be.pxl.services.services.ICordiLocationService;
import be.pxl.services.services.IRouteFunctionalityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private ICordiLocationService locationService;

    @Mock
    private IRouteFunctionalityService routeService;

    @InjectMocks
    private AdminController adminController;

    private CordiLocationRequest request;
    private RouteSettingsRequest routeSettingsRequest;

    @BeforeEach
    void setUp() {
        request = new CordiLocationRequest();
        request.setCurrentBuilding("corda 1");

        routeSettingsRequest = new RouteSettingsRequest();
        routeSettingsRequest.setEnabled(true);
    }

    @Test
     void testForwardLocation_Success() {
        String expectedResult = "Location sent successfully";
        when(locationService.sendLocationToFastApi(any(CordiLocationRequest.class))).thenReturn(expectedResult);

        ResponseEntity<?> response = adminController.forwardLocation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
        verify(locationService, times(1)).sendLocationToFastApi(request);
    }

    @Test
     void testForwardLocation_Failure() {
        String errorMessage = "Connection error";
        when(locationService.sendLocationToFastApi(any(CordiLocationRequest.class)))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = adminController.forwardLocation(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Fout bij verzenden naar FastAPI: " + errorMessage, response.getBody());
        verify(locationService, times(1)).sendLocationToFastApi(request);
    }

    @Test
     void testForwardLocation_SpecificLocation_CordaArena() {
        request.setCurrentBuilding("corda arena");
        String expectedResult = "Location sent successfully";
        when(locationService.sendLocationToFastApi(any(CordiLocationRequest.class))).thenReturn(expectedResult);

        ResponseEntity<?> response = adminController.forwardLocation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
        verify(locationService, times(1)).sendLocationToFastApi(request);
    }

    @Test
     void testGetCurrentLocation_Success() {
        String expectedResult = "{\"location\": \"corda 1\"}";
        when(locationService.getCurrentLocationFromFastApi()).thenReturn(expectedResult);

        ResponseEntity<?> response = adminController.getCurrentLocation();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
        verify(locationService, times(1)).getCurrentLocationFromFastApi();
    }

    @Test
     void testGetCurrentLocation_Failure() {
        String errorMessage = "API unavailable";
        when(locationService.getCurrentLocationFromFastApi())
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = adminController.getCurrentLocation();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Fout bij ophalen locatie uit FastAPI: " + errorMessage, response.getBody());
        verify(locationService, times(1)).getCurrentLocationFromFastApi();
    }

    @Test
     void testSetRouteEnabled_Success() {
        String expectedResult = "Route enabled successfully";
        when(routeService.setRouteEnabled(any(RouteSettingsRequest.class))).thenReturn(expectedResult);

        ResponseEntity<?> response = adminController.setRouteEnabled(routeSettingsRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
        verify(routeService, times(1)).setRouteEnabled(routeSettingsRequest);
    }

    @Test
     void testSetRouteEnabled_Failure() {
        String errorMessage = "Failed to connect to service";
        when(routeService.setRouteEnabled(any(RouteSettingsRequest.class)))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = adminController.setRouteEnabled(routeSettingsRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error setting route enabled: " + errorMessage, response.getBody());
        verify(routeService, times(1)).setRouteEnabled(routeSettingsRequest);
    }

    @Test
     void testSetRouteEnabled_DisableRoute() {
        routeSettingsRequest.setEnabled(false);
        String expectedResult = "Route disabled successfully";
        when(routeService.setRouteEnabled(any(RouteSettingsRequest.class))).thenReturn(expectedResult);

        ResponseEntity<?> response = adminController.setRouteEnabled(routeSettingsRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
        verify(routeService, times(1)).setRouteEnabled(routeSettingsRequest);
    }

    @Test
     void testGetRouteEnabled_Success_RouteIsEnabled() {
        String expectedResult = "true";
        when(routeService.getRouteEnabled()).thenReturn(expectedResult);

        ResponseEntity<?> response = adminController.getRouteEnabled();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
        verify(routeService, times(1)).getRouteEnabled();
    }

    @Test
     void testGetRouteEnabled_Success_RouteIsDisabled() {
        String expectedResult = "false";
        when(routeService.getRouteEnabled()).thenReturn(expectedResult);

        ResponseEntity<?> response = adminController.getRouteEnabled();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
        verify(routeService, times(1)).getRouteEnabled();
    }

    @Test
     void testGetRouteEnabled_Failure() {
        String errorMessage = "Service unavailable";
        when(routeService.getRouteEnabled())
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = adminController.getRouteEnabled();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error getting route enabled status: " + errorMessage, response.getBody());
        verify(routeService, times(1)).getRouteEnabled();
    }
}
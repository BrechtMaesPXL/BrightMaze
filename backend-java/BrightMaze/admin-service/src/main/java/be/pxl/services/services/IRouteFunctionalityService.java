package be.pxl.services.services;

import be.pxl.services.domain.dto.RouteSettingsRequest;

public interface IRouteFunctionalityService {
    String setRouteEnabled(RouteSettingsRequest request);
    String getRouteEnabled();
}

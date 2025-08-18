package be.pxl.services.services;

import be.pxl.services.domain.dto.CordiLocationRequest;

public interface ICordiLocationService {
    String sendLocationToFastApi(CordiLocationRequest request);
    String getCurrentLocationFromFastApi();
}

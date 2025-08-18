package be.pxl.services.services;

import be.pxl.services.domain.dto.RouteSettingsRequest;
import be.pxl.services.domain.dto.VoiceSettingsRequest;
import org.springframework.stereotype.Service;

@Service
public interface IVoiceFunctionalityService {
    void setvoiceEnabled(VoiceSettingsRequest request);
    boolean getVoiceEnabled();
}

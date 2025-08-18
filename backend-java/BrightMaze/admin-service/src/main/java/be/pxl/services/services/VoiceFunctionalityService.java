package be.pxl.services.services;

import be.pxl.services.domain.dto.VoiceSettingsRequest;
import be.pxl.services.domain.Settings;
import be.pxl.services.repository.SettingsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoiceFunctionalityService implements IVoiceFunctionalityService{
    private final SettingsRepository repo;

    public VoiceFunctionalityService(SettingsRepository repo) {
        this.repo = repo;
    }

    @Override
    public void setvoiceEnabled(VoiceSettingsRequest request) {
        Settings settings = repo.findById("voice")
                .orElseGet(() -> {
                    Settings nieuw = new Settings();
                    nieuw.setId("voice");
                    return nieuw;
                });

        settings.setVoiceSetting(request.isEnabled());
        repo.save(settings);
    }

    @Override
    public boolean getVoiceEnabled() {
        return repo.findById("voice")
                .map(Settings::getVoiceSetting)
                .orElse(true); // default true
    }
}

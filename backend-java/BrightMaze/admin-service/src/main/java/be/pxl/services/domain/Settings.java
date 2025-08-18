package be.pxl.services.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settings {

    @Id
    private String id;

    private boolean voiceSetting;

    public boolean getVoiceSetting() {
        return voiceSetting;
    }

    public void setVoiceSetting(boolean voiceSetting) {
        this.voiceSetting = voiceSetting;
    }

    public void setId(String id) {
        this.id = id;
    }
}

package be.pxl.services.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoRequest {

    @NotEmpty(message = "VideoName cannot be empty")
    private String VideoName;
    @NotEmpty(message = "VideoDescription cannot be empty")
    private String VideoDescription;
}


package be.pxl.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CordiLocationRequest {

    @JsonProperty("current_building")
    private String currentBuilding;

}

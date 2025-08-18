package be.pxl.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotEmpty(message = "Event name cannot be empty")
    private String eventName;

    @NotNull(message = "Start date cannot be null")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "End date cannot be null")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime endDate;

    @NotEmpty(message = "Location cannot be empty")
    private String location;

    @NotEmpty(message = "Event description cannot be empty")
    private String eventDescription;

    public Map<String, Object> toMap() {
        return Map.of(
                "eventName", eventName,
                "startDate", startDate,
                "endDate", endDate,
                "location", location,
                "eventDescription", eventDescription
        );
    }

}

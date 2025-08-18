package be.pxl.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private String id;

    private String eventName;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String location;

    private String eventDescription;
}

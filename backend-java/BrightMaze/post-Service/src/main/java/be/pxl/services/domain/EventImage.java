package be.pxl.services.domain;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "eventsimages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class EventImage {
    @Id
    private String id;
    private String imagePath;
    
}

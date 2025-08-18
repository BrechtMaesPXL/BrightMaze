package be.pxl.services.domain;


import be.pxl.services.enums.Status;
import be.pxl.services.enums.Tags;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document(collection = "videos")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String videoPath;
    private String videoName;
    private String videoDescription;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<Tags> tags = List.of();

    @Enumerated(EnumType.STRING)
    private Status status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate = null;

    private String lastError = null;





}

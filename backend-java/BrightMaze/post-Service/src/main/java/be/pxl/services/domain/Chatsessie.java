package be.pxl.services.domain;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;
@Document(collection = "chatSessies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chatsessie {
    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString(); // Unieke chat-ID
    private History history;
}


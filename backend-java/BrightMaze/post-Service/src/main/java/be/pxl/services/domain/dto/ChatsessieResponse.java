package be.pxl.services.domain.dto;

import be.pxl.services.domain.History;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatsessieResponse {
    private String id;
    private History history;
}

package be.pxl.services.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatsessieRequest {
    @NotEmpty(message = "Id cannot be empty")
    private String id;

    @NotNull(message = "message cannot be empty")
    private String message;

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}

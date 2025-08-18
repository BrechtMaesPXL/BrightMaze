package be.pxl.services.domain;

import be.pxl.services.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryItem {
    private Role role;
    private String content;
    private String type;

    public HistoryItem(Role role, String content) {
        this.role = role;
        this.content = content;
        type = null;
    }
}

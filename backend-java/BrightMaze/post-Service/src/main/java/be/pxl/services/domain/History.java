package be.pxl.services.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {
    @Id
    private String id;
    private List<HistoryItem> historyItems;
    public void addItem(HistoryItem item){
        this.historyItems.add(item);
    }
}
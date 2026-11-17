package br.com.fiap.dailyreminder.modules.activities.domain;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
@Data @NoArgsConstructor @Embeddable
public class ChecklistItem {
    @Column(name = "id", nullable = false) private UUID id;
    @Column(name = "text", nullable = false, length = 255) private String text;
    @Column(nullable = false) private boolean completed;
    public ChecklistItem(UUID id, String text, boolean completed) { this.id = id; this.text = text; this.completed = completed; }
}

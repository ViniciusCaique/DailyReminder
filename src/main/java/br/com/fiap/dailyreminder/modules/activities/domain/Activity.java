package br.com.fiap.dailyreminder.modules.activities.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_activity")
public class Activity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private Integer duration;
    @Column(length = 10000) private String description;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private ActivityStatus status = ActivityStatus.INBOX;
    private LocalDate dataDia;
    private String name;
    @CreationTimestamp private Instant created_at;
    @Column(name = "lembrete_id") private UUID noteId;
    @Column(nullable = false) private UUID userId;
    public boolean isCompleted() { return status == ActivityStatus.DONE; }
    public void setCompleted(boolean completed) { status = completed ? ActivityStatus.DONE : ActivityStatus.TODO; }
    private Instant completedAt;
    @Version private long version;
    private UUID projectId;
    @ElementCollection @CollectionTable(name = "tb_activity_labels", joinColumns = @JoinColumn(name = "activity_id"))
    @Column(name = "label_id", nullable = false)
    private java.util.Set<UUID> labelIds = new java.util.LinkedHashSet<>();
    @ElementCollection @CollectionTable(name = "tb_activity_checklist", joinColumns = @JoinColumn(name = "activity_id"))
    @OrderColumn(name = "item_order")
    private java.util.List<ChecklistItem> checklist = new java.util.ArrayList<>();
}

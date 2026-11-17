package br.com.fiap.dailyreminder.modules.notes.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_notes")
public class Note implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String message;
    private int duration;
    private UUID userId;
    private Instant scheduledAt;
    private boolean completed;
    @Enumerated(EnumType.STRING) private Recurrence recurrence = Recurrence.NONE;
    private String timeZone = "UTC";
    private String repeatDays = "";
    private Instant lastCompletedAt;
}

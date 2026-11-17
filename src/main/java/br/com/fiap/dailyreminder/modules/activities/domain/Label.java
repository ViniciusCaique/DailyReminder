package br.com.fiap.dailyreminder.modules.activities.domain;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
@Data @NoArgsConstructor @Entity @Table(name = "tb_activity_label")
public class Label {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID userId;
    @Column(nullable = false, length = 80) private String name;
}

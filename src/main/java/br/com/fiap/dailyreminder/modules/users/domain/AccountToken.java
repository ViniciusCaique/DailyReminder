package br.com.fiap.dailyreminder.modules.users.domain;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;
@Data @Entity @Table(name = "tb_account_tokens")
public class AccountToken {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID userId;
    @Column(length = 64, unique = true) private String tokenHash;
    private String purpose;
    private Instant expiresAt;
    private boolean used;
}

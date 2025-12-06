package org.cswteams.ms3.entity;

// IMPORT DI LOMBOK
import lombok.Data;

// IMPORT JPA
import javax.persistence.*;

// IMPORT JAVA TIME
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private LocalDateTime blacklistedAt;
}

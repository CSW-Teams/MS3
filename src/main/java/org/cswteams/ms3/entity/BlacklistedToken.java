package org.cswteams.ms3.entity;

// IMPORT DI LOMBOK

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Explicit mapping of the 'token' column to avoid ambiguity.
    @Column(nullable = false, name = "token", unique = true, length = 512)
    private String token;

    /*
    Explicit mapping of 'name = "blacklisted_at"' because Hibernate's automatic camelCase -> snake_case
    conversion might fail or vary depending on the configuration (e.g., looking for 'blacklistedat').
    Specifying it forces the use of the correct column created by the SQL script.
     */
    @Column(nullable = false, name = "blacklisted_at")
    private LocalDateTime blacklistedAt;

    @Column(nullable = false, name = "expires_at")
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ms3_system_user_id", nullable = false)
    private SystemUser systemUser;
}

package upc.edu.muusmart.campaignmanagement.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import upc.edu.muusmart.campaignmanagement.domain.model.enums.StableStatus;

/**
 * Entity representing a Stable (corral or barn) in which animals are kept. A stable
 * belongs to a single user (identified by username) and can be referenced by
 * campaigns. Stables capture minimal metadata such as name and description
 * along with creation and update timestamps.
 */
@Entity
@Table(name = "stables",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_stable_owner_name", columnNames = {"ownerUsername", "name"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Username of the user who owns this stable.
     */
    @Column(nullable = false)
    private String ownerUsername;

    // NUEVOS CAMPOS
    @Column(nullable = false)
    private String location = "Peru"; // valor por defecto

    @Column(nullable = false)
    private Integer capacity; // requerido en creación

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StableStatus status = StableStatus.OPERATIVE; // default

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
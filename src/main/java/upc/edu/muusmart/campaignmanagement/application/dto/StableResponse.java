package upc.edu.muusmart.campaignmanagement.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import upc.edu.muusmart.campaignmanagement.domain.model.enums.StableStatus;

/**
 * Response payload representing a stable. Includes the stable's owner as a
 * username and timestamps. Exposes only the necessary information to
 * clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StableResponse {
    private Long id;
    private String name;
    private String description;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // NUEVOS CAMPOS
    private String location;
    private Integer capacity;
    private StableStatus status;
}
package upc.edu.muusmart.campaignmanagement.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import upc.edu.muusmart.campaignmanagement.domain.model.enums.StableStatus;

/**
 * Request payload for creating a new stable. The name is required; the
 * description may be omitted.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStableRequest {
    @NotBlank(message = "Stable name is required")
    private String name;
    private String description;

    // NUEVOS CAMPOS
    private String location; // si viene null, la entidad usa Peru

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be > 0")
    private Integer capacity;

    private StableStatus status; // opcional, default OPERATIVE
}
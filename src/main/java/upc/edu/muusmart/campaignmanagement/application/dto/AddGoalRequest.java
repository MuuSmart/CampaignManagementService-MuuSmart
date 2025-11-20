package upc.edu.muusmart.campaignmanagement.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddGoalRequest {

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Metric is required")
    private String metric;

    @NotNull(message = "Target value is required")
    private Integer targetValue;

    @NotNull(message = "Current value is required")
    private Integer currentValue;

}

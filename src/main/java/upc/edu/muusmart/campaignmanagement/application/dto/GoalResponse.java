package upc.edu.muusmart.campaignmanagement.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalResponse {

    private Long id;
    private String description;
    private String metric;
    private Integer targetValue;
    private Integer currentValue;

}

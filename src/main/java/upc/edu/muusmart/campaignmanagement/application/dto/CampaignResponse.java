package upc.edu.muusmart.campaignmanagement.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    /**
     * The username of the campaign owner. IAM tokens contain the username
     * (subject) rather than a numeric identifier, so the front‑end and
     * other services should use this field to identify the owner.
     */
    private String username;
    private Long stableId;
    private List<GoalResponse> goals;
    private List<ChannelResponse> channels;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

package upc.edu.muusmart.campaignmanagement.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddChannelRequest {

    @NotBlank(message = "Channel type is required")
    private String type;

    private String details;

}

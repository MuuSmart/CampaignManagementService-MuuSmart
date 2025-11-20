package upc.edu.muusmart.campaignmanagement.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelResponse {

    private Long id;
    private String type;
    private String details;

}

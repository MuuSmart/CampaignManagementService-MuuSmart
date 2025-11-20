package upc.edu.muusmart.campaignmanagement.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "goals",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_goal_campaign_description", columnNames = {"campaign_id", "description"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String metric;

    @Column(nullable = false)
    private Integer targetValue;

    @Column(nullable = false)
    private Integer currentValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    public void updateValues(String description, String metric, Integer targetValue, Integer currentValue) {
        this.description = description;
        this.metric = metric;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
    }

}

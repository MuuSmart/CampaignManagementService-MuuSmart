package upc.edu.muusmart.campaignmanagement.domain.repository;

import jakarta.validation.constraints.NotBlank;
import upc.edu.muusmart.campaignmanagement.domain.model.aggregates.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByOwnerUsername(String ownerUsername);
    List<Campaign> findByStableId(Long stableId);

    /**
     * Finds a campaign by its unique name.
     *
     * @param name the campaign name, must not be blank
     * @return an optional containing the campaign if it exists
     */
    Optional<Campaign> findByName(@NotBlank(message = "Campaign name is required") String name);

    /**
     * Finds a campaign by name and owner username, to enforce per-user uniqueness.
     */
    Optional<Campaign> findByNameAndOwnerUsername(String name, String ownerUsername);
}

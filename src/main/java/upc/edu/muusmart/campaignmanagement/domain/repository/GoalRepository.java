package upc.edu.muusmart.campaignmanagement.domain.repository;

import upc.edu.muusmart.campaignmanagement.domain.model.aggregates.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByCampaignId(Long campaignId);
}

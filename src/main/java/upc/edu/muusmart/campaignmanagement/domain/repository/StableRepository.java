package upc.edu.muusmart.campaignmanagement.domain.repository;

import upc.edu.muusmart.campaignmanagement.domain.model.aggregates.Stable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StableRepository extends JpaRepository<Stable, Long> {
    /**
     * Finds all stables owned by a specific user.
     *
     * @param ownerUsername the username of the stable owner
     * @return list of stables owned by the given user
     */
    List<Stable> findByOwnerUsername(String ownerUsername);

    /**
     * Finds a stable by name and owner. Used to prevent duplicate stable names
     * for the same user.
     *
     * @param name          the stable name
     * @param ownerUsername the username of the owner
     * @return an optional containing the stable if found
     */
    Optional<Stable> findByNameAndOwnerUsername(String name, String ownerUsername);
}
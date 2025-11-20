package upc.edu.muusmart.campaignmanagement.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upc.edu.muusmart.campaignmanagement.application.dto.CreateStableRequest;
import upc.edu.muusmart.campaignmanagement.application.dto.StableResponse;
import upc.edu.muusmart.campaignmanagement.domain.model.aggregates.Stable;
import upc.edu.muusmart.campaignmanagement.domain.model.enums.StableStatus;
import upc.edu.muusmart.campaignmanagement.domain.repository.StableRepository;
import upc.edu.muusmart.campaignmanagement.shared.exceptions.DuplicateResourceException;
import upc.edu.muusmart.campaignmanagement.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for managing stables. Provides methods to create and
 * retrieve stables with appropriate authorization checks. Users can only
 * manage their own stables unless they have the ADMIN role (checked in
 * controllers).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StableService {

    private final StableRepository stableRepository;

    /**
     * Creates a new stable for the given user. Ensures that the user does not
     * already have a stable with the same name.
     *
     * @param request  the new stable details
     * @param username the owner of the stable
     * @return the created stable as a response DTO
     */
    public StableResponse createStable(CreateStableRequest request, String username) {
        stableRepository.findByNameAndOwnerUsername(request.getName(), username)
                .ifPresent(s -> {
                    throw new DuplicateResourceException("Stable with the same name already exists for this user");
                });
        Stable stable = Stable.builder()
                .name(request.getName())
                .description(request.getDescription())
                .ownerUsername(username)
                .location(request.getLocation() == null || request.getLocation().isBlank() ? "Peru" : request.getLocation())
                .capacity(request.getCapacity())
                .status(request.getStatus() == null ? StableStatus.OPERATIVE : request.getStatus())
                .build();
        Stable saved = stableRepository.save(stable);
        return mapToResponse(saved);
    }

    /**
     * Retrieves all stables accessible to the caller. Administrators get all
     * stables; normal users get only their own.
     *
     * @param username the username of the caller
     * @param isAdmin  whether the caller has the ADMIN role
     * @return a list of stables the caller can see
     */
    public List<StableResponse> getAllStables(String username, boolean isAdmin) {
        List<Stable> stables;
        if (isAdmin) {
            stables = stableRepository.findAll();
        } else {
            stables = stableRepository.findByOwnerUsername(username);
        }
        return stables.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Retrieves a single stable by ID if the caller is authorized. Normal
     * users may only access their own stables.
     *
     * @param stableId the stable identifier
     * @param username the username of the caller
     * @param isAdmin  whether the caller has the ADMIN role
     * @return the stable response
     */
    public StableResponse getStableById(Long stableId, String username, boolean isAdmin) {
        Stable stable = stableRepository.findById(stableId)
                .orElseThrow(() -> new ResourceNotFoundException("Stable not found with id: " + stableId));
        if (!isAdmin && !stable.getOwnerUsername().equals(username)) {
            throw new SecurityException("Access denied to stable with id: " + stableId);
        }
        return mapToResponse(stable);
    }

    private StableResponse mapToResponse(Stable stable) {
        return StableResponse.builder()
                .id(stable.getId())
                .name(stable.getName())
                .description(stable.getDescription())
                .ownerUsername(stable.getOwnerUsername())
                .createdAt(stable.getCreatedAt())
                .updatedAt(stable.getUpdatedAt())
                .location(stable.getLocation())
                .capacity(stable.getCapacity())
                .status(stable.getStatus())
                .build();
    }
}
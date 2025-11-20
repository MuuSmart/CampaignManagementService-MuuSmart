package upc.edu.muusmart.campaignmanagement.application.service;

import upc.edu.muusmart.campaignmanagement.application.dto.*;
import upc.edu.muusmart.campaignmanagement.domain.model.aggregates.Campaign;
import upc.edu.muusmart.campaignmanagement.domain.model.aggregates.Channel;
import upc.edu.muusmart.campaignmanagement.domain.model.aggregates.Goal;
import upc.edu.muusmart.campaignmanagement.domain.repository.CampaignRepository;
import upc.edu.muusmart.campaignmanagement.domain.repository.ChannelRepository;
import upc.edu.muusmart.campaignmanagement.domain.repository.GoalRepository;
import upc.edu.muusmart.campaignmanagement.domain.repository.StableRepository;
import lombok.RequiredArgsConstructor;
// No longer import Authentication or GrantedAuthority here; authorization is handled in the controller.
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upc.edu.muusmart.campaignmanagement.shared.exceptions.DuplicateResourceException;
import upc.edu.muusmart.campaignmanagement.shared.exceptions.InvalidValueException;
import upc.edu.muusmart.campaignmanagement.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final GoalRepository goalRepository;
    private final ChannelRepository channelRepository;
    private final StableRepository stableRepository;

    private static final Set<String> ALLOWED_STATUSES = Set.of("PLANNED", "ACTIVE", "COMPLETED");
    private static final Set<String> ALLOWED_GOAL_METRICS = Set.of("CLICKS", "VIEWS", "CONVERSIONS");

    /**
     * Creates a new campaign for the given user. The campaign name must be unique and the
     * referenced stable must exist. Non‑admin users may only create campaigns for stables
     * they own.
     */
    public CampaignResponse createCampaign(CreateCampaignRequest request, String username, boolean isAdmin) {
        // Ensure valid status
        if (!ALLOWED_STATUSES.contains(request.getStatus())) {
            throw new InvalidValueException("Invalid status. Allowed: " + ALLOWED_STATUSES);
        }

        // Ensure unique campaign name per user
        campaignRepository.findByNameAndOwnerUsername(request.getName(), username)
                .ifPresent(c -> { throw new DuplicateResourceException("Campaign name already exists for this user"); });

        // Validate the referenced stable exists
        var stableOpt = stableRepository.findById(request.getStableId());
        if (stableOpt.isEmpty()) {
            throw new ResourceNotFoundException("Stable not found with id: " + request.getStableId());
        }
        var stable = stableOpt.get();
        // Non‑admin users may only create campaigns for their own stables
        if (!isAdmin && !stable.getOwnerUsername().equals(username)) {
            throw new SecurityException("You are not authorized to use this stable: " + request.getStableId());
        }

        Campaign campaign = Campaign.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .ownerUsername(username)
                .stableId(request.getStableId())
                .build();

        Campaign savedCampaign = campaignRepository.save(campaign);
        return mapToCampaignResponse(savedCampaign);
    }

    /**
     * Retrieves a campaign by its ID, enforcing that a non-admin caller
     * can access only their own campaigns.
     *
     * @param id       the campaign identifier
     * @param username the username of the authenticated user
     * @param isAdmin  whether the caller has the ADMIN role
     * @return the campaign response if access is permitted
     * @throws SecurityException if the caller is not authorized to access the campaign
     */
    public CampaignResponse getCampaignById(Long id, String username, boolean isAdmin) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        // Normal users may only access their own campaigns
        if (!isAdmin && !campaign.getOwnerUsername().equals(username)) {
            throw new SecurityException("Access denied to campaign with id: " + id);
        }
        return mapToCampaignResponse(campaign);
    }

    /**
     * Retrieves campaigns visible to the authenticated user. Administrators
     * receive all campaigns; normal users receive only their own.
     *
     * @param username the username of the authenticated user
     * @param isAdmin  whether the caller has the ADMIN role
     * @return list of campaigns accessible to the caller
     */
    public List<CampaignResponse> getAllCampaignsByUsername(String username, boolean isAdmin) {
        if (isAdmin) {
            return campaignRepository.findAll()
                    .stream()
                    .map(this::mapToCampaignResponse)
                    .collect(Collectors.toList());
        }
        return campaignRepository.findByOwnerUsername(username)
                .stream()
                .map(this::mapToCampaignResponse)
                .collect(Collectors.toList());
    }

    public List<CampaignResponse> getCampaignsByStableId(Long stableId) {
        return campaignRepository.findByStableId(stableId)
                .stream()
                .map(this::mapToCampaignResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a campaign if the caller is authorized. Administrators can delete
     * any campaign, whereas normal users can delete only their own.
     *
     * @param id       the campaign identifier
     * @param username the username of the authenticated user
     * @param isAdmin  whether the caller has the ADMIN role
     * @throws SecurityException if the caller is not authorized to delete the campaign
     */
    public void deleteCampaign(Long id, String username, boolean isAdmin) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        if (!isAdmin && !campaign.getOwnerUsername().equals(username)) {
            throw new SecurityException("Access denied to delete campaign with id: " + id);
        }
        campaignRepository.delete(campaign);
    }

    /**
     * Updates the status of a campaign if the caller is authorized.
     * Administrators may update any campaign; normal users may only update their own.
     *
     * @param id       the campaign identifier
     * @param request  the request containing the new status
     * @param username the username of the authenticated user
     * @param isAdmin  whether the caller has the ADMIN role
     * @return the updated campaign response
     */
    public CampaignResponse updateCampaignStatus(Long id, UpdateCampaignStatusRequest request, String username, boolean isAdmin) {
        if (!ALLOWED_STATUSES.contains(request.getStatus())) {
            throw new InvalidValueException("Invalid status. Allowed: " + ALLOWED_STATUSES);
        }
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        if (!isAdmin && !campaign.getOwnerUsername().equals(username)) {
            throw new SecurityException("Access denied to update campaign with id: " + id);
        }
        campaign.updateStatus(request.getStatus());
        Campaign updatedCampaign = campaignRepository.save(campaign);
        return mapToCampaignResponse(updatedCampaign);
    }

    /**
     * Adds a goal to the specified campaign if the caller is authorized. Administrators
     * may add goals to any campaign; normal users may only add to their own.
     *
     * @param campaignId the identifier of the campaign
     * @param request    the request containing goal details
     * @param username   the username of the authenticated user
     * @param isAdmin    whether the caller has the ADMIN role
     * @return the updated campaign response
     */
    public CampaignResponse addGoalToCampaign(Long campaignId, AddGoalRequest request, String username, boolean isAdmin) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));
        if (!isAdmin && !campaign.getOwnerUsername().equals(username)) {
            throw new SecurityException("Access denied to modify campaign with id: " + campaignId);
        }
        if (!ALLOWED_GOAL_METRICS.contains(request.getMetric())) {
            throw new InvalidValueException("Invalid metric. Allowed: " + ALLOWED_GOAL_METRICS);
        }
        // Evitar duplicados de metas por descripcion dentro de la campaña
        if (campaign.getGoals() != null) {
            boolean goalDup = campaign.getGoals().stream()
                    .anyMatch(g -> g.getDescription().equalsIgnoreCase(request.getDescription()));
            if (goalDup) {
                throw new DuplicateResourceException("Goal with the same description already exists in this campaign");
            }
        }
        Goal goal = Goal.builder()
                .description(request.getDescription())
                .metric(request.getMetric())
                .targetValue(request.getTargetValue())
                .currentValue(request.getCurrentValue())
                .build();
        campaign.addGoal(goal);
        Campaign updatedCampaign = campaignRepository.save(campaign);
        return mapToCampaignResponse(updatedCampaign);
    }

    /**
     * Adds a channel to the specified campaign if the caller is authorized. Administrators
     * may add channels to any campaign; normal users may only add to their own.
     *
     * @param campaignId the identifier of the campaign
     * @param request    the request containing channel details
     * @param username   the username of the authenticated user
     * @param isAdmin    whether the caller has the ADMIN role
     * @return the updated campaign response
     */
    public CampaignResponse addChannelToCampaign(Long campaignId, AddChannelRequest request, String username, boolean isAdmin) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));
        if (!isAdmin && !campaign.getOwnerUsername().equals(username)) {
            throw new SecurityException("Access denied to modify campaign with id: " + campaignId);
        }
        // Evitar duplicados de canales por tipo dentro de la campaña
        if (campaign.getChannels() != null) {
            boolean channelDup = campaign.getChannels().stream()
                    .anyMatch(c -> c.getType().equalsIgnoreCase(request.getType()));
            if (channelDup) {
                throw new DuplicateResourceException("Channel with the same type already exists in this campaign");
            }
        }
        Channel channel = Channel.builder()
                .type(request.getType())
                .details(request.getDetails())
                .build();
        campaign.addChannel(channel);
        Campaign updatedCampaign = campaignRepository.save(campaign);
        return mapToCampaignResponse(updatedCampaign);
    }

    /**
     * Retrieves goals associated with the specified campaign if the caller is authorized.
     * Administrators may read any campaign; normal users may read only their own.
     *
     * @param campaignId the campaign identifier
     * @param username   the username of the authenticated user
     * @param isAdmin    whether the caller has the ADMIN role
     * @return list of goal responses
     * @throws SecurityException if the caller is not authorized to view the campaign
     */
    public List<GoalResponse> getGoalsByCampaignId(Long campaignId, String username, boolean isAdmin) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));
        if (!isAdmin && !campaign.getOwnerUsername().equals(username)) {
            throw new SecurityException("Access denied to view campaign with id: " + campaignId);
        }
        return goalRepository.findByCampaignId(campaignId)
                .stream()
                .map(this::mapToGoalResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves channels associated with the specified campaign if the caller is authorized.
     * Administrators may read any campaign; normal users may read only their own.
     *
     * @param campaignId the campaign identifier
     * @param username   the username of the authenticated user
     * @param isAdmin    whether the caller has the ADMIN role
     * @return list of channel responses
     * @throws SecurityException if the caller is not authorized to view the campaign
     */
    public List<ChannelResponse> getChannelsByCampaignId(Long campaignId, String username, boolean isAdmin) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));
        if (!isAdmin && !campaign.getOwnerUsername().equals(username)) {
            throw new SecurityException("Access denied to view campaign with id: " + campaignId);
        }
        return channelRepository.findByCampaignId(campaignId)
                .stream()
                .map(this::mapToChannelResponse)
                .collect(Collectors.toList());
    }

    private CampaignResponse mapToCampaignResponse(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .description(campaign.getDescription())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .status(campaign.getStatus())
                .username(campaign.getOwnerUsername())
                .stableId(campaign.getStableId())
                .goals(campaign.getGoals() != null ? campaign.getGoals().stream()
                        .map(this::mapToGoalResponse)
                        .collect(Collectors.toList()) : new java.util.ArrayList<>())
                .channels(campaign.getChannels() != null ? campaign.getChannels().stream()
                        .map(this::mapToChannelResponse)
                        .collect(Collectors.toList()) : new java.util.ArrayList<>())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .build();
    }

    private GoalResponse mapToGoalResponse(Goal goal) {
        return GoalResponse.builder()
                .id(goal.getId())
                .description(goal.getDescription())
                .metric(goal.getMetric())
                .targetValue(goal.getTargetValue())
                .currentValue(goal.getCurrentValue())
                .build();
    }

    private ChannelResponse mapToChannelResponse(Channel channel) {
        return ChannelResponse.builder()
                .id(channel.getId())
                .type(channel.getType())
                .details(channel.getDetails())
                .build();
    }

}

package upc.edu.muusmart.campaignmanagement.interfaces.rest;

import upc.edu.muusmart.campaignmanagement.application.dto.*;
import upc.edu.muusmart.campaignmanagement.application.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("campaigns")
@RequiredArgsConstructor
@Tag(name = "Campaigns", description = "Campaign Management API")
@SecurityRequirement(name = "Bearer Authentication")
public class CampaignController {

    private final CampaignService campaignService;

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new campaign")
    public ResponseEntity<CampaignResponse> createCampaign(
            @Valid @RequestBody CreateCampaignRequest request,
            Authentication authentication) {
        String username = extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        CampaignResponse response = campaignService.createCampaign(request, username, isAdmin);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "Get all campaigns")
    public ResponseEntity<List<CampaignResponse>> getAllCampaigns(Authentication authentication) {
        String username = extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<CampaignResponse> campaigns = campaignService.getAllCampaignsByUsername(username, isAdmin);
        return ResponseEntity.ok(campaigns);
    }

    /**
     * Helper method to extract the username from the authentication principal.
     *
     * <p>The JWT authentication filter always sets the principal to the username
     * string. This method simply converts the principal to a string so the
     * service layer can associate created entities with the authenticated
     * username rather than a numeric identifier.</p>
     *
     * @param authentication the authentication object from the security context
     * @return the username of the authenticated user
     */
    private String extractUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        return principal != null ? principal.toString() : null;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get campaign by ID")
    public ResponseEntity<CampaignResponse> getCampaignById(@PathVariable Long id, Authentication authentication) {
        String username = extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        CampaignResponse response = campaignService.getCampaignById(id, username, isAdmin);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a campaign")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id, Authentication authentication) {
        String username = extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        campaignService.deleteCampaign(id, username, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @PatchMapping("/{id}/update-status")
    @Operation(summary = "Update campaign status")
    public ResponseEntity<CampaignResponse> updateCampaignStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCampaignStatusRequest request,
            Authentication authentication) {
        String username = extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        CampaignResponse response = campaignService.updateCampaignStatus(id, request, username, isAdmin);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @PatchMapping("/{id}/add-goal")
    @Operation(summary = "Add a goal to a campaign")
    public ResponseEntity<CampaignResponse> addGoalToCampaign(
            @PathVariable Long id,
            @Valid @RequestBody AddGoalRequest request,
            Authentication authentication) {
        String username = extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        CampaignResponse response = campaignService.addGoalToCampaign(id, request, username, isAdmin);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @PatchMapping("/{id}/add-channel")
    @Operation(summary = "Add a channel to a campaign")
    public ResponseEntity<CampaignResponse> addChannelToCampaign(
            @PathVariable Long id,
            @Valid @RequestBody AddChannelRequest request,
            Authentication authentication) {
        String username = extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        CampaignResponse response = campaignService.addChannelToCampaign(id, request, username, isAdmin);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/{id}/goals")
    @Operation(summary = "Get all goals for a campaign")
    public ResponseEntity<List<GoalResponse>> getGoalsByCampaign(@PathVariable Long id, Authentication authentication) {
        String username = extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<GoalResponse> goals = campaignService.getGoalsByCampaignId(id, username, isAdmin);
        return ResponseEntity.ok(goals);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/{id}/channels")
    @Operation(summary = "Get all channels for a campaign")
    public ResponseEntity<List<ChannelResponse>> getChannelsByCampaign(@PathVariable Long id, Authentication authentication) {
        String username = extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<ChannelResponse> channels = campaignService.getChannelsByCampaignId(id, username, isAdmin);
        return ResponseEntity.ok(channels);
    }
}

package upc.edu.muusmart.campaignmanagement.interfaces.rest;

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
import upc.edu.muusmart.campaignmanagement.application.dto.CreateStableRequest;
import upc.edu.muusmart.campaignmanagement.application.dto.StableResponse;
import upc.edu.muusmart.campaignmanagement.application.service.StableService;

import java.util.List;

/**
 * REST controller for managing stables. Provides endpoints for creating and
 * retrieving stables. Authentication is required for all operations, and
 * authorization checks are enforced via annotations and service methods.
 */
@RestController
@RequestMapping("stables")
@RequiredArgsConstructor
@Tag(name = "Stables", description = "Stable Management API")
@SecurityRequirement(name = "Bearer Authentication")
public class StableController {

    private final StableService stableService;

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new stable")
    public ResponseEntity<StableResponse> createStable(
            @Valid @RequestBody CreateStableRequest request,
            Authentication authentication) {
        String username = extractUsername(authentication);
        StableResponse response = stableService.createStable(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "Get all stables accessible to the caller")
    public ResponseEntity<List<StableResponse>> getAllStables(Authentication authentication) {
        String username = extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<StableResponse> stables = stableService.getAllStables(username, isAdmin);
        return ResponseEntity.ok(stables);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get stable by ID")
    public ResponseEntity<StableResponse> getStableById(
            @PathVariable Long id,
            Authentication authentication) {
        String username = extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        StableResponse response = stableService.getStableById(id, username, isAdmin);
        return ResponseEntity.ok(response);
    }

    /**
     * Extracts the username from the authentication principal. Mirrors the helper
     * in {@link CampaignController} but redefined here to avoid code sharing
     * between controllers.
     *
     * @param authentication the authentication object from the security context
     * @return the username of the authenticated user
     */
    private String extractUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        return principal != null ? principal.toString() : null;
    }
}
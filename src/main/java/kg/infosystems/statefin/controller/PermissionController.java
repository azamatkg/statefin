package kg.infosystems.statefin.controller;

import kg.infosystems.statefin.dto.request.PermissionCreateRequest;
import kg.infosystems.statefin.dto.request.PermissionUpdateRequest;
import kg.infosystems.statefin.dto.response.ApiResponse;
import kg.infosystems.statefin.dto.response.PagedResponse;
import kg.infosystems.statefin.dto.response.PermissionResponse;
import kg.infosystems.statefin.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<PermissionResponse>>> getAllPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "id") String sortBy) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PagedResponse<PermissionResponse> permissions = permissionService.getAllPermissions(pageable);
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionById(@PathVariable Long id) {
        PermissionResponse permissionResponse = permissionService.getPermissionById(id);
        return ResponseEntity.ok(ApiResponse.success(permissionResponse));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(@Valid @RequestBody PermissionCreateRequest request) {
        PermissionResponse permissionResponse = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Permission created successfully", permissionResponse));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public ResponseEntity<ApiResponse<PermissionResponse>> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionUpdateRequest request) {
        PermissionResponse permissionResponse = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(ApiResponse.success("Permission updated successfully", permissionResponse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(ApiResponse.success("Permission deleted successfully", null));
    }

    @GetMapping("/resources")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<List<String>>> getAllResources() {
        List<String> resources = permissionService.getAllResources();
        return ResponseEntity.ok(ApiResponse.success(resources));
    }

    @GetMapping("/actions")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<List<String>>> getAllActions() {
        List<String> actions = permissionService.getAllActions();
        return ResponseEntity.ok(ApiResponse.success(actions));
    }

}
package kg.infosystems.statefin.controller;

import kg.infosystems.statefin.dto.request.RoleCreateRequest;
import kg.infosystems.statefin.dto.request.RoleUpdateRequest;
import kg.infosystems.statefin.dto.response.ApiResponse;
import kg.infosystems.statefin.dto.response.PagedResponse;
import kg.infosystems.statefin.dto.response.PermissionResponse;
import kg.infosystems.statefin.dto.response.RoleResponse;
import kg.infosystems.statefin.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<RoleResponse>>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "id") String sortBy) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PagedResponse<RoleResponse> roles = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse roleResponse = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success(roleResponse));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleCreateRequest request) {
        RoleResponse roleResponse = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Role created successfully", roleResponse));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        RoleResponse roleResponse = roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", roleResponse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully", null));
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponse<Set<PermissionResponse>>> getRolePermissions(@PathVariable Long id) {
        Set<PermissionResponse> permissions = roleService.getRolePermissions(id);
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<ApiResponse<RoleResponse>> assignPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        RoleResponse roleResponse = roleService.assignPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok(ApiResponse.success("Permission assigned successfully", roleResponse));
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<ApiResponse<RoleResponse>> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        RoleResponse roleResponse = roleService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok(ApiResponse.success("Permission removed successfully", roleResponse));
    }

}
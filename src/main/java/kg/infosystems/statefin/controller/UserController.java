package kg.infosystems.statefin.controller;

import kg.infosystems.statefin.dto.request.UserUpdateRequest;
import kg.infosystems.statefin.dto.response.ApiResponse;
import kg.infosystems.statefin.dto.response.PagedResponse;
import kg.infosystems.statefin.dto.response.UserResponse;
import kg.infosystems.statefin.security.UserPrincipal;
import kg.infosystems.statefin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        UserResponse userResponse = userService.getUserByUsername(currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "id") String sortBy) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PagedResponse<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest updateRequest) {
        UserResponse userResponse = userService.updateUser(id, updateRequest);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", userResponse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        UserResponse userResponse = userService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.success("Role assigned successfully", userResponse));
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<ApiResponse<UserResponse>> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        UserResponse userResponse = userService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.success("Role removed successfully", userResponse));
    }

}
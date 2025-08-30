package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.UserRegistrationRequest;
import kg.infosystems.statefin.dto.request.UserUpdateRequest;
import kg.infosystems.statefin.dto.response.PagedResponse;
import kg.infosystems.statefin.dto.response.PermissionResponse;
import kg.infosystems.statefin.dto.response.RoleResponse;
import kg.infosystems.statefin.dto.response.UserResponse;
import kg.infosystems.statefin.entity.Role;
import kg.infosystems.statefin.entity.User;
import kg.infosystems.statefin.exception.ResourceNotFoundException;
import kg.infosystems.statefin.exception.UserAlreadyExistsException;
import kg.infosystems.statefin.repository.RoleRepository;
import kg.infosystems.statefin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .active(true)
                .build();
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(java.time.LocalDateTime.now());
        }
        if (user.getUpdatedAt() == null) {
            user.setUpdatedAt(java.time.LocalDateTime.now());
        }
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newUserRole = Role.builder()
                            .name("USER")
                            .description("Standard user with basic permissions")
                            .active(true)
                            .build();
                    if (newUserRole.getCreatedAt() == null) {
                        newUserRole.setCreatedAt(java.time.LocalDateTime.now());
                    }
                    if (newUserRole.getUpdatedAt() == null) {
                        newUserRole.setUpdatedAt(java.time.LocalDateTime.now());
                    }
                    return roleRepository.save(newUserRole);
                });
        user.addRole(userRole);
        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return mapToUserResponse(user);
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findByActiveTrue(pageable);
        
        return new PagedResponse<>(
                userPage.getContent().stream()
                        .map(this::mapToUserResponse)
                        .collect(Collectors.toList()),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast(),
                userPage.hasNext(),
                userPage.hasPrevious()
        );
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email is already registered");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        user.addRole(role);
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public UserResponse removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        user.removeRole(role);
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        Set<RoleResponse> roleResponses = user.getRoles().stream()
                .map(role -> {
                    Set<PermissionResponse> permissionResponses = role.getPermissions().stream()
                            .map(permission -> new PermissionResponse(
                                    permission.getId(),
                                    permission.getName(),
                                    permission.getDescription(),
                                    permission.getResource(),
                                    permission.getAction(),
                                    permission.getActive(),
                                    permission.getCreatedAt(),
                                    permission.getUpdatedAt()
                            ))
                            .collect(Collectors.toSet());

                    return new RoleResponse(
                            role.getId(),
                            role.getName(),
                            role.getDescription(),
                            role.getActive(),
                            role.getCreatedAt(),
                            role.getUpdatedAt(),
                            permissionResponses
                    );
                })
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                roleResponses
        );
    }

}
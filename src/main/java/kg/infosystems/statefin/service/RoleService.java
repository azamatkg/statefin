package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.RoleCreateRequest;
import kg.infosystems.statefin.dto.request.RoleUpdateRequest;
import kg.infosystems.statefin.dto.response.PagedResponse;
import kg.infosystems.statefin.dto.response.PermissionResponse;
import kg.infosystems.statefin.dto.response.RoleResponse;
import kg.infosystems.statefin.entity.auth.Permission;
import kg.infosystems.statefin.entity.auth.Role;
import kg.infosystems.statefin.exception.ResourceAlreadyExistsException;
import kg.infosystems.statefin.exception.ResourceNotFoundException;
import kg.infosystems.statefin.repository.PermissionRepository;
import kg.infosystems.statefin.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Role with name '" + request.getName() + "' already exists");
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .build();

        Role savedRole = roleRepository.save(role);
        return mapToRoleResponse(savedRole);
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return mapToRoleResponse(role);
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleByName(String name) {
        Role role = roleRepository.findByNameWithPermissions(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
        return mapToRoleResponse(role);
    }

    @Transactional(readOnly = true)
    public PagedResponse<RoleResponse> getAllRoles(Pageable pageable) {
        Page<Role> rolePage = roleRepository.findByActiveTrue(pageable);
        
        return new PagedResponse<>(
                rolePage.getContent().stream()
                        .map(this::mapToRoleResponse)
                        .collect(Collectors.toList()),
                rolePage.getNumber(),
                rolePage.getSize(),
                rolePage.getTotalElements(),
                rolePage.getTotalPages(),
                rolePage.isFirst(),
                rolePage.isLast(),
                rolePage.hasNext(),
                rolePage.hasPrevious()
        );
    }

    @Transactional
    public RoleResponse updateRole(Long id, RoleUpdateRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }

        if (request.getActive() != null) {
            role.setActive(request.getActive());
        }

        Role updatedRole = roleRepository.save(role);
        return mapToRoleResponse(updatedRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        role.setActive(false);
        roleRepository.save(role);
    }

    @Transactional
    public RoleResponse assignPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));

        role.addPermission(permission);
        Role updatedRole = roleRepository.save(role);
        return mapToRoleResponse(updatedRole);
    }

    @Transactional
    public RoleResponse removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));

        role.removePermission(permission);
        Role updatedRole = roleRepository.save(role);
        return mapToRoleResponse(updatedRole);
    }

    @Transactional(readOnly = true)
    public Set<PermissionResponse> getRolePermissions(Long roleId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        return role.getPermissions().stream()
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
    }

    private RoleResponse mapToRoleResponse(Role role) {
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
    }

}
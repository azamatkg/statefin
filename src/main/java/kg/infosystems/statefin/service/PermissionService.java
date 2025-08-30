package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.PermissionCreateRequest;
import kg.infosystems.statefin.dto.request.PermissionUpdateRequest;
import kg.infosystems.statefin.dto.response.PagedResponse;
import kg.infosystems.statefin.dto.response.PermissionResponse;
import kg.infosystems.statefin.entity.Permission;
import kg.infosystems.statefin.exception.ResourceAlreadyExistsException;
import kg.infosystems.statefin.exception.ResourceNotFoundException;
import kg.infosystems.statefin.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional
    public PermissionResponse createPermission(PermissionCreateRequest request) {
        if (permissionRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Permission with name '" + request.getName() + "' already exists");
        }
        
        if (permissionRepository.existsByResourceAndAction(request.getResource(), request.getAction())) {
            throw new ResourceAlreadyExistsException("Permission with resource '" + request.getResource() + 
                    "' and action '" + request.getAction() + "' already exists");
        }

        Permission permission = Permission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .resource(request.getResource())
                .action(request.getAction())
                .active(true)
                .build();

        Permission savedPermission = permissionRepository.save(permission);
        return mapToPermissionResponse(savedPermission);
    }

    @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
        return mapToPermissionResponse(permission);
    }

    @Transactional(readOnly = true)
    public PermissionResponse getPermissionByName(String name) {
        Permission permission = permissionRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with name: " + name));
        return mapToPermissionResponse(permission);
    }

    @Transactional(readOnly = true)
    public PagedResponse<PermissionResponse> getAllPermissions(Pageable pageable) {
        Page<Permission> permissionPage = permissionRepository.findByActiveTrue(pageable);
        
        return new PagedResponse<>(
                permissionPage.getContent().stream()
                        .map(this::mapToPermissionResponse)
                        .collect(Collectors.toList()),
                permissionPage.getNumber(),
                permissionPage.getSize(),
                permissionPage.getTotalElements(),
                permissionPage.getTotalPages(),
                permissionPage.isFirst(),
                permissionPage.isLast(),
                permissionPage.hasNext(),
                permissionPage.hasPrevious()
        );
    }

    @Transactional
    public PermissionResponse updatePermission(Long id, PermissionUpdateRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));

        if (request.getDescription() != null) {
            permission.setDescription(request.getDescription());
        }

        if (request.getActive() != null) {
            permission.setActive(request.getActive());
        }

        Permission updatedPermission = permissionRepository.save(permission);
        return mapToPermissionResponse(updatedPermission);
    }

    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));

        permission.setActive(false);
        permissionRepository.save(permission);
    }

    @Transactional(readOnly = true)
    public List<String> getAllResources() {
        return permissionRepository.findAllResources();
    }

    @Transactional(readOnly = true)
    public List<String> getAllActions() {
        return permissionRepository.findAllActions();
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByResource(String resource) {
        List<Permission> permissions = permissionRepository.findByResourceAndActiveTrue(resource);
        return permissions.stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    private PermissionResponse mapToPermissionResponse(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getName(),
                permission.getDescription(),
                permission.getResource(),
                permission.getAction(),
                permission.getActive(),
                permission.getCreatedAt(),
                permission.getUpdatedAt()
        );
    }

}
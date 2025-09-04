package kg.infosystems.statefin.bootstrap;

import kg.infosystems.statefin.entity.auth.Permission;
import kg.infosystems.statefin.entity.auth.Role;
import kg.infosystems.statefin.entity.auth.User;
import kg.infosystems.statefin.repository.PermissionRepository;
import kg.infosystems.statefin.repository.RoleRepository;
import kg.infosystems.statefin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.initial-data.load-on-startup:false}")
    private boolean loadInitialData;

    @Value("${app.initial-data.admin.username:admin}")
    private String adminUsername;

    @Value("${app.initial-data.admin.email:admin@statefin.com}")
    private String adminEmail;

    @Value("${app.initial-data.admin.password:Admin123!}")
    private String adminPassword;

    @Value("${app.initial-data.user.username:user}")
    private String userUsername;

    @Value("${app.initial-data.user.email:user@demo.com}")
    private String userEmail;

    @Value("${app.initial-data.user.password:User123!}")
    private String userPassword;

    @Value("${app.initial-data.manager.username:manager}")
    private String managerUsername;

    @Value("${app.initial-data.manager.email:manager@demo.com}")
    private String managerEmail;

    @Value("${app.initial-data.manager.password:Manager123!}")
    private String managerPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!loadInitialData) {
            return;
        }
        try {
            createPermissions();
            createRoles();
            createUsers();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load initial data", e);
        }
    }

    private void createPermissions() {
        List<PermissionData> permissions = Arrays.asList(
                new PermissionData("USER_READ", "Read user information", "USER", "READ"),
                new PermissionData("USER_WRITE", "Create and update users", "USER", "WRITE"),
                new PermissionData("USER_DELETE", "Delete users", "USER", "DELETE"),
                new PermissionData("ROLE_READ", "Read role information", "ROLE", "READ"),
                new PermissionData("ROLE_WRITE", "Create and update roles", "ROLE", "WRITE"),
                new PermissionData("ROLE_DELETE", "Delete roles", "ROLE", "DELETE"),
                new PermissionData("ROLE_MANAGE", "Manage role permissions", "ROLE", "MANAGE"),
                new PermissionData("PERMISSION_READ", "Read permission information", "PERMISSION", "READ"),
                new PermissionData("PERMISSION_WRITE", "Create and update permissions", "PERMISSION", "WRITE"),
                new PermissionData("PERMISSION_DELETE", "Delete permissions", "PERMISSION", "DELETE"),
                new PermissionData("DECISION_READ", "Read decision information", "DECISION", "READ"),
                new PermissionData("DECISION_WRITE", "Create and update decisions", "DECISION", "WRITE"),
                new PermissionData("DECISION_DELETE", "Delete decisions", "DECISION", "DELETE"),
                new PermissionData("DECISION_TYPE_READ", "Read decision type information", "DECISION_TYPE", "READ"),
                new PermissionData("DECISION_TYPE_WRITE", "Create and update decision types", "DECISION_TYPE", "WRITE"),
                new PermissionData("DECISION_TYPE_DELETE", "Delete decision types", "DECISION_TYPE", "DELETE"),
                new PermissionData("DECISION_MAKING_BODY_READ", "Read decision making body information", "DECISION_MAKING_BODY", "READ"),
                new PermissionData("DECISION_MAKING_BODY_WRITE", "Create and update decision making bodies", "DECISION_MAKING_BODY", "WRITE"),
                new PermissionData("DECISION_MAKING_BODY_DELETE", "Delete decision making bodies", "DECISION_MAKING_BODY", "DELETE")
        );
        for (PermissionData permData : permissions) {
            if (!permissionRepository.existsByName(permData.name)) {
                Permission permission = Permission.builder()
                        .name(permData.name)
                        .description(permData.description)
                        .resource(permData.resource)
                        .action(permData.action)
                        .active(true)
                        .build();
                permissionRepository.save(permission);
            }
        }
    }

    private void createRoles() {
        if (!roleRepository.existsByName("USER")) {
            Role userRole = Role.builder()
                    .name("USER")
                    .description("Standard user with basic permissions")
                    .active(true)
                    .build();
            roleRepository.save(userRole);
        }
        if (!roleRepository.existsByName("MANAGER")) {
            Role managerRole = Role.builder()
                    .name("MANAGER")
                    .description("Manager with user management permissions")
                    .active(true)
                    .build();
            List<String> managerPermissions = Arrays.asList("USER_READ", "USER_WRITE");
            assignPermissionsToRole(managerRole, managerPermissions);
            roleRepository.save(managerRole);
        }
        if (!roleRepository.existsByName("ADMIN")) {
            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .description("Administrator with full permissions")
                    .active(true)
                    .build();
            List<String> adminPermissions = Arrays.asList(
                    "USER_READ", "USER_WRITE", "USER_DELETE",
                    "ROLE_READ", "ROLE_WRITE", "ROLE_DELETE", "ROLE_MANAGE",
                    "PERMISSION_READ", "PERMISSION_WRITE", "PERMISSION_DELETE",
                    "DECISION_READ", "DECISION_WRITE", "DECISION_DELETE",
                    "DECISION_TYPE_READ", "DECISION_TYPE_WRITE", "DECISION_TYPE_DELETE",
                    "DECISION_MAKING_BODY_READ", "DECISION_MAKING_BODY_WRITE", "DECISION_MAKING_BODY_DELETE"
            );
            assignPermissionsToRole(adminRole, adminPermissions);
            roleRepository.save(adminRole);
        }
    }

    private void assignPermissionsToRole(Role role, List<String> permissionNames) {
        for (String permissionName : permissionNames) {
            permissionRepository.findByName(permissionName).ifPresent(role::addPermission);
        }
    }

    private void createUsers() {
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .firstName("System")
                    .lastName("Administrator")
                    .active(true)
                    .build();
            roleRepository.findByName("ADMIN").ifPresent(admin::addRole);
            userRepository.save(admin);
        }
        if (!userRepository.existsByUsername(managerUsername)) {
            User manager = User.builder()
                    .username(managerUsername)
                    .email(managerEmail)
                    .password(passwordEncoder.encode(managerPassword))
                    .firstName("Demo")
                    .lastName("Manager")
                    .active(true)
                    .build();
            roleRepository.findByName("MANAGER").ifPresent(manager::addRole);
            userRepository.save(manager);
        }
        if (!userRepository.existsByUsername(userUsername)) {
            User user = User.builder()
                    .username(userUsername)
                    .email(userEmail)
                    .password(passwordEncoder.encode(userPassword))
                    .firstName("Demo")
                    .lastName("User")
                    .active(true)
                    .build();
            roleRepository.findByName("USER").ifPresent(user::addRole);
            userRepository.save(user);
        }
    }

    private static class PermissionData {
        final String name;
        final String description;
        final String resource;
        final String action;

        PermissionData(String name, String description, String resource, String action) {
            this.name = name;
            this.description = description;
            this.resource = resource;
            this.action = action;
        }
    }

}
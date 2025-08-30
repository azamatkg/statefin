package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.UserRegistrationRequest;
import kg.infosystems.statefin.dto.request.UserUpdateRequest;
import kg.infosystems.statefin.dto.response.UserResponse;
import kg.infosystems.statefin.entity.Role;
import kg.infosystems.statefin.entity.User;
import kg.infosystems.statefin.exception.ResourceNotFoundException;
import kg.infosystems.statefin.exception.UserAlreadyExistsException;
import kg.infosystems.statefin.repository.RoleRepository;
import kg.infosystems.statefin.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationRequest registrationRequest;
    private User user;
    private Role userRole;

    @BeforeEach
    void setUp() {
        registrationRequest = new UserRegistrationRequest(
                "testuser", "test@example.com", "Password123!", "Test", "User");

        userRole = Role.builder()
                .id(1L)
                .name("USER")
                .description("Standard user role")
                .active(true)
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .build();
        user.addRole(userRole);
    }

    @Test
    void createUser_Success() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponse result = userService.createUser(registrationRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_UsernameAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.createUser(registrationRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username is already taken");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.createUser(registrationRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email is already registered");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Given
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));

        // When
        UserResponse result = userService.getUserById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void getUserById_NotFound() {
        // Given
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 1");
    }

    @Test
    void updateUser_Success() {
        // Given
        UserUpdateRequest updateRequest = new UserUpdateRequest("newemail@example.com", "NewFirst", "NewLast", true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponse result = userService.updateUser(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).save(any(User.class));
    }

}
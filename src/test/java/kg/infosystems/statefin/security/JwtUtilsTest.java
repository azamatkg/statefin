package kg.infosystems.statefin.security;

import kg.infosystems.statefin.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private Authentication authentication;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecretKeyThatIsLongEnoughForHS256Algorithm");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000L);
        ReflectionTestUtils.setField(jwtUtils, "refreshExpirationMs", 604800000L);

        userPrincipal = new UserPrincipal(
                1L,
                "testuser",
                "test@example.com",
                "password",
                true,
                List.of(new SimpleGrantedAuthority("USER_READ"))
        );

        authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }

    @Test
    void generateJwtToken_Success() {
        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void validateJwtToken_ValidToken() {
        // Given
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateJwtToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void getUserNameFromJwtToken_Success() {
        // Given
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Then
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void getUserIdFromJwtToken_Success() {
        // Given
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        Long userId = jwtUtils.getUserIdFromJwtToken(token);

        // Then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void getAuthoritiesFromJwtToken_Success() {
        // Given
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        List<String> authorities = jwtUtils.getAuthoritiesFromJwtToken(token);

        // Then
        assertThat(authorities).isNotNull();
        assertThat(authorities).contains("USER_READ");
    }

    @Test
    void generateRefreshToken_Success() {
        // When
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertThat(jwtUtils.validateJwtToken(refreshToken)).isTrue();
    }

}
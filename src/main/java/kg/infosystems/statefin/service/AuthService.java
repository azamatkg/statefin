package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.LoginRequest;
import kg.infosystems.statefin.dto.request.RefreshTokenRequest;
import kg.infosystems.statefin.dto.response.LoginResponse;
import kg.infosystems.statefin.dto.response.UserResponse;
import kg.infosystems.statefin.exception.InvalidTokenException;
import kg.infosystems.statefin.security.CustomUserDetailsService;
import kg.infosystems.statefin.security.UserPrincipal;
import kg.infosystems.statefin.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            String accessToken = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication);
            UserResponse userResponse = userService.getUserByUsername(authentication.getName());
            return new LoginResponse(
                    accessToken,
                    refreshToken,
                    jwtUtils.getJwtExpirationMs() / 1000,
                    userResponse
            );
        } catch (AuthenticationException e) {
            throw new AuthenticationException("Invalid username or password") {};
        }
    }

    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }
        if (jwtUtils.isTokenExpired(refreshToken)) {
            throw new InvalidTokenException("Refresh token has expired");
        }
        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
        UserResponse userResponse = userService.getUserByUsername(username);
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities());
        String newAccessToken = jwtUtils.generateJwtToken(authentication);
        String newRefreshToken = jwtUtils.generateRefreshToken(authentication);
        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                jwtUtils.getJwtExpirationMs() / 1000,
                userResponse
        );
    }

}
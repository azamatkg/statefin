package kg.infosystems.statefin.security.jwt;

import kg.infosystems.statefin.security.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        List<String> authorities = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Instant now = Instant.now();
        Instant expiryDate = now.plus(jwtExpirationMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .claim("userId", userPrincipal.getId())
                .claim("email", userPrincipal.getEmail())
                .claim("authorities", authorities)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Instant now = Instant.now();
        Instant expiryDate = now.plus(refreshExpirationMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .claim("userId", userPrincipal.getId())
                .claim("tokenType", "refresh")
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }

    public Long getUserIdFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return Long.valueOf(claims.get("userId").toString());
    }

    @SuppressWarnings("unchecked")
    public List<String> getAuthoritiesFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return (List<String>) claims.get("authorities");
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getExpiration();
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

}
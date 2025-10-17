package co.edu.hotel.empleadoservice.services.jwt;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET_KEY = "5KxYFlfdBJjL5iaPyLsM4Cd2SdAyNAXLb30euPzGPbfvUWvAhJ";

    public String generateToken(String email, String roleName) {
        return Jwts.builder()
                .setSubject(email)
                .addClaims(Map.of("role", roleName))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 3600 * 1000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token, String email) {
        return email.equals(extractUsername(token)) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return getAllClaims(token).getExpiration().before(new Date());
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}

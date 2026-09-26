package com.dtnexus.crm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import com.dtnexus.crm.model.User;

@Service
public class JwtService {

    private final String secret;

    public JwtService(@Value("${security.jwt.secret:}") String secret) {
        this.secret = secret;
    }

    public Claims parse(String token) throws JwtException {
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 32 characters");
        }
        Jws<Claims> signedClaims = Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token);
        return signedClaims.getPayload();
    }

    public String issue(User user) {
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 32 characters");
        }
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("tenant", user.getTenant())
                .claim("roles", Arrays.asList(user.getRoles().split(",")))
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}

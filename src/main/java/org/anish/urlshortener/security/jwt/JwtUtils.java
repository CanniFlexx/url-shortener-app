package org.anish.urlshortener.security.jwt;

import org.anish.urlshortener.service.UserDetailsImpl;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // Authorization -> Bearer <TOKEN>
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public String generateToken(UserDetailsImpl userDetails){
        String username = userDetails.getUsername();
        String roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(","));
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles) // Embeds roles as custom claims
                .issuedAt(new Date()) // Token issue date
                .expiration(new Date((new Date().getTime() + jwtExpirationMs))) // Expiration
                .signWith(key()) // Signs token with secret key
                .compact(); // Builds final JWT
    }


    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key())
                    .build().parseSignedClaims(authToken);
            return true; // Valid token
        } catch (JwtException e) {
            throw new RuntimeException(e); // Token-related issues
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e); // Empty or null token
        } catch (Exception e) {
            throw new RuntimeException(e); // Other unexpected errors
        }
    }

}
/*

In short, this class:

Extracts JWT from HTTP headers.
Generates JWT with username and roles.
Validates the JWT and checks expiration.
Extracts the username from the token.
Uses a secret key to sign and verify JWTs.

*/

//Refer to underlying concepts of: (for interview purposes) that have been used in this class.
//Streams API
//Functional programming (lambdas, method references)
//Mapping and transformation
//Collecting and reducing
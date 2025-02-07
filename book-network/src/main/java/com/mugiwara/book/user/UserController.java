package com.mugiwara.book.user;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/info")
public class UserController {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @GetMapping
    public ResponseEntity<Map<String, String>> getUserInfo(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        String username;

        try {
            username = extractUsernameFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid JWT token"));
        }

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", username);
        return ResponseEntity.ok(userInfo);
    }

    private String extractUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        // Debug print for claims verification
        System.out.println("Claims: " + claims);

        // Fetching preferred username if available, else fallback to subject
        String username = (String) claims.get("fullName");

        if (username == null) {
            username = (String) claims.get("name"); // Fallback for full name
        }

        return username != null ? username : claims.getSubject(); // Default to sub (email)
    }
}

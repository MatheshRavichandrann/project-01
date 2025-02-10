package com.mugiwara.book.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;

public class JWTVerificationExample {

    private static final String SECRET_KEY = "3D77C395F14D4230A75FE34BA2BC6A66E6984516C039D21CBDA6JEEVASOROHINIM6BC6D22CDAB44";

    public static void main(String[] args) {
        String jwtToken = "eyJhbGciOiJIUzM4NCJ9.eyJmdWxsTmFtZSI6IlRvbnkgQ2hvcHBlciIsInN1YiI6ImFsdC5nMi0zaXF0MTJsQHlvcG1haWwuY29tIiwiaWF0IjoxNzM4OTM4MDEzLCJleHAiOjE3MzkwMjQ0MTMsImF1dGhvcml0aWVzIjpbIlVTRVIiXX0.VOplLPzTqSnMYYhmDJLSS-MBYoQG--BJafCbM1TnQOA1ZCTVk0ZEmi6O3iEwNKep";

        try {
            Claims claims = decodeJWT(jwtToken);
            System.out.println("Token is valid!");
            System.out.println("Claims: " + claims);
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage());
        }
    }

    private static Claims decodeJWT(String jwt) {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}

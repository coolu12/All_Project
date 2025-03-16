package com.warehouse.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Map;
/**
 * Utility class for generating and parsing JSON Web Tokens (JWT).
 *
 * This class provides methods for generating JWT tokens with a specified expiration time and parsing JWT tokens
 * to retrieve the payload contents.
 * It uses a secret key for signing the tokens and includes methods for both generating and parsing JWT tokens.
 *
 * @author www.itheima.com
 * @modify Lu Cheng
 */
public class JwtUtils {

    private static String signKey = "dyson";
    private static Long expire = 28800000L;//8 hour
    /**
     * Generates a JWT token with the specified payload.
     *
     * @param claims The payload contents to be included in the JWT token.
     * @return       The generated JWT token.
     */
    public static String generateJwt(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
        return jwt;
    }
    /**
     * Parses a JWT token to retrieve the payload contents.
     *
     * @param jwt The JWT token to be parsed.
     * @return    The payload contents stored in the JWT token.
     */
    public static Claims parseJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }
}


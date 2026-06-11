package org.example.lease.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.lease.common.exception.LeaseException;
import org.example.lease.common.result.ResultCodeEnum;

import javax.crypto.SecretKey;
import java.util.Date;

public class JWTUtil {
    private static SecretKey key = Keys.hmacShaKeyFor("secretfyxfyxfyxfyxfyxfyxfyxfyxfyx".getBytes());
    public static String createJWT(long userId, String username) {
        String jwt = Jwts.builder()
                .setExpiration(new Date( System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .setSubject("Login")
                .claim("userId", userId)
                .claim("username", username)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }
    public static Claims parseJWT(String jwt) {
        if(jwt == null)
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_AUTH.getCode(), ResultCodeEnum.APP_LOGIN_AUTH.getMessage());

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);
            Claims body = claimsJws.getBody();
            return body;
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED.getCode(), ResultCodeEnum.TOKEN_EXPIRED.getMessage());
        }catch (JwtException e){
            e.printStackTrace();
            throw new LeaseException(ResultCodeEnum.TOKEN_INVALID.getCode(), ResultCodeEnum.TOKEN_INVALID.getMessage());
        }
    }
}

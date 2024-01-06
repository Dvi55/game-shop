package com.gameshop.www.eCommerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.gameshop.www.eCommerce.model.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    private static final String USER_EMAIL = "emai";
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expire.time}")
    private long expireTime;
    private Algorithm algorithm;

    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC512(algorithmKey);
    }

    public String generateJWT(LocalUser user) {
        return JWT.create()
                .withClaim(USER_EMAIL, user.getEmail())
                .withIssuer(issuer)
                .withExpiresAt(new Date(System.currentTimeMillis() + (expireTime * 1000)))
                .sign(algorithm);
    }

    public String getUserEmail(String token) {
        return JWT.decode(token).getClaim(USER_EMAIL).asString();
    }

    public boolean isTokenValid(String token) {
        try {

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

}

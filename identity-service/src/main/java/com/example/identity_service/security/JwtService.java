package com.example.identity_service.security;

import com.example.identity_service.role.Role;
import com.example.identity_service.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String TOKEN_TYPE = "JWT";

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    public JwtService(JwtProperties jwtProperties, ObjectMapper objectMapper) {
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
    }

    public String generateToken(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(jwtProperties.expirationMinutes() * 60L);
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .sorted(Comparator.naturalOrder())
                .toList();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", TOKEN_TYPE);

        String headerJson = toJson(header);
        String payloadJson = toJson(buildClaims(user, issuedAt, expiresAt, roles));

        String encodedHeader = base64Url(headerJson);
        String payload = base64Url(payloadJson);
        String signature = sign(encodedHeader + "." + payload);

        return encodedHeader + "." + payload + "." + signature;
    }

    private Map<String, Object> buildClaims(User user, Instant issuedAt, Instant expiresAt, List<String> roles) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", user.getEmail());
        claims.put("iat", issuedAt.getEpochSecond());
        claims.put("exp", expiresAt.getEpochSecond());
        claims.put("roles", roles);
        return claims;
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(jwtProperties.secret().getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] signature = mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign JWT.", exception);
        }
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String toJson(Map<String, Object> value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize JWT claims.", exception);
        }
    }
}

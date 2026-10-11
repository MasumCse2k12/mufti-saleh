package com.muftisaleh.security;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import com.muftisaleh.common.error.ApiException;
import com.muftisaleh.config.AppProperties;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    private static final String CLAIM_NAME = "name";
    private static final String AUDIENCE = "ADMIN";
    private static final int MIN_SECRET_BYTES = 32;

    private final AppProperties props;
    private final NimbusJwtEncoder encoder;
    private final NimbusJwtDecoder decoder;

    public JwtService(AppProperties props) {
        this.props = props;
        SecretKey key = secretKey(props.jwt().secret());
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        this.decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @PostConstruct
    void warnOnWeakSecret() {
        if (props.jwt().secret().getBytes().length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "app.jwt.secret must be at least " + MIN_SECRET_BYTES + " bytes for HS256");
        }
    }

    public String issueAdminAccess(UUID adminId, String displayName) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.jwt().issuer())
                .issuedAt(now)
                .expiresAt(now.plus(props.jwt().adminAccessTtl()))
                .subject(adminId.toString())
                .audience(List.of(AUDIENCE))
                .claim(CLAIM_NAME, displayName == null ? "" : displayName)
                .id(UUID.randomUUID().toString())
                .build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }

    public AuthPrincipal verify(String token) {
        Jwt jwt;
        try {
            jwt = decoder.decode(token);
        } catch (JwtException e) {
            throw ApiException.unauthorized("Your session has ended. Please sign in again.",
                    "আপনার সেশন শেষ হয়েছে। আবার লগইন করুন।");
        }
        if (!jwt.getAudience().contains(AUDIENCE)) {
            throw ApiException.unauthorized("Invalid token audience.", "অবৈধ টোকেন।");
        }
        return new AuthPrincipal(UUID.fromString(jwt.getSubject()), jwt.getClaimAsString(CLAIM_NAME));
    }

    public boolean isAdminToken(String token) {
        try {
            Jwt jwt = decoder.decode(token);
            return jwt.getAudience().contains(AUDIENCE);
        } catch (JwtException ignored) {
            return false;
        }
    }

    private static SecretKey secretKey(String secret) {
        return new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    }
}

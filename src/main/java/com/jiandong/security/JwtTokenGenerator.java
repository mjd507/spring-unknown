package com.jiandong.security;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator {

	private static final Logger log = LoggerFactory.getLogger(JwtTokenGenerator.class);

	public final JwtEncoder jwtEncoder;

	public JwtTokenGenerator(JwtEncoder jwtDecoder) {
		this.jwtEncoder = jwtDecoder;
	}

	public String generateToken(String userName, List<String> roles) {
		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(now)
				.expiresAt(now.plusSeconds(3600)) // 1 hour
				.subject(userName)
				.claim("role", roles)
				.build();

		String tokenValue = jwtEncoder
				.encode(JwtEncoderParameters.from(claims))
				.getTokenValue();
		log.info("generate token for user: {}, roles: {}", tokenValue, roles);
		return tokenValue;
	}

}

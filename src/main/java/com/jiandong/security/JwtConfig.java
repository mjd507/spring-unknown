package com.jiandong.security;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtConfig {

	private static final String JWT_PUBLIC_KEY_PATH = "classpath:jwt/public_key.pem";

	private static final String JWT_PRIVATE_KEY_PATH = "classpath:jwt/private_pkcs8_key.pem";

	private final ResourceLoader resourceLoader;

	public JwtConfig(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Bean
	public RSAPublicKey publicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		Resource resource = resourceLoader.getResource(JWT_PUBLIC_KEY_PATH);
		try (PemReader pemReader = new PemReader(new InputStreamReader(resource.getInputStream()))) {
			PemObject pemObject = pemReader.readPemObject();
			byte[] keyBytes = pemObject.getContent();

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		}
	}

	@Bean
	public RSAPrivateKey privateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		Resource resource = resourceLoader.getResource(JWT_PRIVATE_KEY_PATH);
		try (PemReader pemReader = new PemReader(new InputStreamReader(resource.getInputStream()))) {
			PemObject pemObject = pemReader.readPemObject();
			byte[] keyBytes = pemObject.getContent();

			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		}
	}

	@Bean
	public JwtEncoder jwtEncoder() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		RSAKey jwk = new RSAKey.Builder(publicKey()).privateKey(privateKey()).build();
		return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
	}

	@Bean
	public JwtDecoder jwtDecoder() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		return NimbusJwtDecoder.withPublicKey(publicKey()).build();
	}

}
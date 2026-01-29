package com.jiandong.support;

import java.util.List;

import com.jiandong.security.JwtTokenGenerator;

import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class JwtTokenHelper {

	private final JwtTokenGenerator jwtTokenGenerator;

	public JwtTokenHelper(JwtTokenGenerator jwtTokenGenerator) {
		this.jwtTokenGenerator = jwtTokenGenerator;
	}

	public String generateNormalUserToken() {
		return "Bearer " + jwtTokenGenerator.generateToken("noraml-user", List.of("USER"));
	}

	public String generateAdminUserToken() {
		return "Bearer " + jwtTokenGenerator.generateToken("admin-user", List.of("ADMIN"));
	}

}

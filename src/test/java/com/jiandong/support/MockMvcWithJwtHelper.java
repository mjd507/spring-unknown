package com.jiandong.support;

import java.util.List;

import com.jiandong.security.JwtTokenGenerator;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@TestComponent
public class MockMvcWithJwtHelper {

	private final JwtTokenGenerator jwtTokenGenerator;

	public MockMvcWithJwtHelper(JwtTokenGenerator jwtTokenGenerator) {
		this.jwtTokenGenerator = jwtTokenGenerator;
	}

	public MockMvcTester.MockMvcRequestBuilder normalUserGet(MockMvcTester mockMvcTester) {
		return normalUser(mockMvcTester, HttpMethod.GET);
	}

	public MockMvcTester.MockMvcRequestBuilder adminUserGet(MockMvcTester mockMvcTester) {
		return adminUser(mockMvcTester, HttpMethod.GET);
	}

	MockMvcTester.MockMvcRequestBuilder normalUser(MockMvcTester mockMvcTester, HttpMethod httpMethod) {
		return mockMvcTester
				.method(httpMethod)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenGenerator.generateToken("noraml-user", List.of("USER")));
	}

	MockMvcTester.MockMvcRequestBuilder adminUser(MockMvcTester mockMvcTester, HttpMethod httpMethod) {
		return mockMvcTester
				.method(httpMethod)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenGenerator.generateToken("admin-user", List.of("ADMIN")));
	}

}

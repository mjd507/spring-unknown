package com.jiandong.support;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

public class MockMvcDecorator {

	public static MockMvcTester.MockMvcRequestBuilder normalUserGet(MockMvcTester mockMvcTester) {
		return normalUser(mockMvcTester, HttpMethod.GET);
	}

	public static MockMvcTester.MockMvcRequestBuilder normalUserPost(MockMvcTester mockMvcTester) {
		return normalUser(mockMvcTester, HttpMethod.POST);
	}

	static MockMvcTester.MockMvcRequestBuilder normalUser(MockMvcTester mockMvcTester, HttpMethod httpMethod) {
		return mockMvcTester
				.method(httpMethod)
				.header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjp1c2VyX3B3ZA==");
	}

	static MockMvcTester.MockMvcRequestBuilder adminUser(MockMvcTester mockMvcTester, HttpMethod httpMethod) {
		return mockMvcTester
				.method(httpMethod)
				.header(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW5fcHdk==");
	}

}

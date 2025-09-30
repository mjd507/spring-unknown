package com.jiandong.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
		WebSecurityConfig.class,
		WebSecurityTest.SecurityTestController.class,
})
@ImportAutoConfiguration({
		DispatcherServletAutoConfiguration.class, MockMvcAutoConfiguration.class
})
public class WebSecurityTest {

	private static final Logger log = LoggerFactory.getLogger(WebSecurityTest.class);

	@Autowired MockMvc mockMvc;

	@Test
	void test_200_no_authentication() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/public/security1"))
				.andExpect(status().isOk())
				.andReturn();
		String response = mvcResult.getResponse().getContentAsString();
		Assertions.assertEquals("public-content-allowed", response);
	}

	@Test
	void test_401() throws Exception {
		mockMvc.perform(get("/private/security2"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void test_200_with_authentication() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/private/security2")
						.header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjp1c2VyX3B3ZA=="))
				.andExpect(status().isOk())
				.andReturn();
		String response = mvcResult.getResponse().getContentAsString();
		Assertions.assertEquals("allowed due to authentication success", response);
	}

	@RestController
	public static class SecurityTestController {

		@GetMapping("/public/security1")
		public String test() {
			return "public-content-allowed";
		}

		@GetMapping("/private/security2")
		public String test2() {
			return "allowed due to authentication success";
		}

	}

}

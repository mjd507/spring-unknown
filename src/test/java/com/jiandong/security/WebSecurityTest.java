package com.jiandong.security;

import com.jiandong.support.MockMvcDecorator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {
		WebSecurityConfig.class,
		WebSecurityTest.SecurityTestController.class,
})
@ImportAutoConfiguration({
		MockMvcAutoConfiguration.class
})
public class WebSecurityTest {

	private static final Logger log = LoggerFactory.getLogger(WebSecurityTest.class);

	@Autowired MockMvcTester mockMvcTester;

	@Test
	void test_200_no_authentication() throws Exception {
		MockMvcDecorator.normalUserGet(mockMvcTester)
				.uri("/public/security1")
				.assertThat()
				.hasStatusOk()
				.bodyText()
				.isEqualTo("public-content-allowed");
	}

	@Test
	void test_401() throws Exception {
		mockMvcTester.get()
				.uri("/private/security2")
				.assertThat()
				.hasStatus(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void test_200_with_authentication() throws Exception {
		MockMvcDecorator.normalUserGet(mockMvcTester)
				.uri("/private/security2")
				.assertThat()
				.hasStatusOk()
				.bodyText()
				.isEqualTo("allowed due to authentication success");
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

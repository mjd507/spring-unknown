package com.jiandong.exception;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {
		GlobalExceptionHandler.class,
		GlobalExceptionHandlerTest.ExceptionTestController.class
})
@ImportAutoConfiguration({
		MockMvcAutoConfiguration.class
})
class GlobalExceptionHandlerTest {

	@Autowired MockMvcTester mockMvcTester;

	@Test
	void testBusinessExceptionHandler() {
		mockMvcTester.get()
				.uri("/business")
				.assertThat()
				.hasStatus(HttpStatus.CONFLICT)
				.hasBodyTextEqualTo("this is a business exception");
	}

	@Test
	void testGenericExceptionHandler() {
		mockMvcTester.get()
				.uri("/generic")
				.assertThat()
				.hasStatus5xxServerError()
				.hasBodyTextEqualTo("this is a generic exception");
	}

	@RestController
	public static class ExceptionTestController {

		@GetMapping("/business")
		public String business() {
			throw new GlobalExceptionHandler.BusinessException("this is a business exception");
		}

		@GetMapping("/generic")
		public String generic() {
			throw new RuntimeException("this is a generic exception");
		}

	}

}

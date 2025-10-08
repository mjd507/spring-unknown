package com.jiandong.core.apiversioning;

import com.jiandong.security.WebSecurityConfig;
import com.jiandong.support.MockMvcDecorator;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.web.client.ApiVersionInserter;

@SpringBootTest(classes = {AccountController.class, AccountControllerTest.MockMvcApiVersionConfig.class, WebSecurityConfig.class})
@ImportAutoConfiguration({WebMvcAutoConfiguration.class})
@AutoConfigureMockMvc
class AccountControllerTest {

	@Autowired MockMvcTester mockMvcTester;

	@Test
	void getAccount_400_no_apiVersion() {
		MockMvcDecorator.normalUserGet(mockMvcTester)
				.uri("/accounts/99")
				.assertThat()
				.hasStatus(HttpStatus.BAD_REQUEST)
				.hasErrorMessage("API version is required.");
	}

	@Test
	void getAccount_1_0_version_withDefaultName() {
		MockMvcDecorator.normalUserGet(mockMvcTester)
				.uri("/accounts/99")
				.apiVersion(1.0)
				.assertThat()
				.hasStatusOk()
				.bodyJson().isEqualTo("{\"id\":\"99\",\"name\":\"default\"}");
	}

	@Test
	void getAccount_1_1_version_withAdvancedName() {
		MockMvcDecorator.normalUserGet(mockMvcTester)
				.uri("/accounts/99")
				.apiVersion(1.1)
				.assertThat()
				.hasStatusOk()
				.bodyJson().isEqualTo("{\"id\":\"99\",\"name\":\"advanced\"}");
	}

	@TestConfiguration
	public static class MockMvcApiVersionConfig implements MockMvcBuilderCustomizer {

		@Override
		public void customize(ConfigurableMockMvcBuilder<?> builder) {
			builder.apiVersionInserter(ApiVersionInserter.useHeader("X-API-Version"))
					.alwaysDo(MockMvcResultHandlers.print());
		}

	}

}


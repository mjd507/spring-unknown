package com.jiandong.core.http;

import com.jiandong.core.http.apicontract.stackoverflow.QuestionService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

@ImportHttpServices(
		group = HttpServiceConfig.GROUP_GITHUB,
		basePackages = {"com.jiandong.core.http.apicontract.github"}
)
@ImportHttpServices(group = HttpServiceConfig.GROUP_STACKOVERFLOW,
		types = {QuestionService.class}
)
@Configuration
public class HttpServiceConfig {

	public static final String GROUP_GITHUB = "github";

	public static final String GROUP_STACKOVERFLOW = "stackoverflow";

	@Bean
	RestClientHttpServiceGroupConfigurer groupConfigurer() {

		return groups -> {
			groups.filterByName(GROUP_GITHUB)
					.forEachClient((group, builder) -> builder
							.baseUrl("https://api.github.com")
							.defaultHeader("Accept", "application/vnd.github.v3+json"));

			groups.filterByName(GROUP_STACKOVERFLOW)
					.forEachClient((group, builder) -> builder
							.baseUrl("https://api.stackexchange.com?site=stackoverflow"));
		};

	}

}

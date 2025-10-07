package com.jiandong.core.http;

import java.util.Set;

import com.jiandong.core.http.apicontract.github.IssueService;
import com.jiandong.core.http.apicontract.github.MilestoneService;
import com.jiandong.core.http.apicontract.github.ReleaseService;
import com.jiandong.core.http.apicontract.github.RepositoryService;
import com.jiandong.core.http.apicontract.stackoverflow.QuestionService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.service.registry.HttpServiceProxyRegistry;

import static com.jiandong.core.http.HttpServiceConfig.GROUP_GITHUB;
import static com.jiandong.core.http.HttpServiceConfig.GROUP_STACKOVERFLOW;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {HttpServiceConfig.class})
class HttpServiceConfigTest {

	@Autowired HttpServiceProxyRegistry httpServiceProxyRegistry;

	@Test
	void testHttpServiceConfig() {
		Set<String> groupNames = httpServiceProxyRegistry.getGroupNames();
		assertThat(groupNames)
				.hasSize(2)
				.contains(GROUP_GITHUB)
				.contains(GROUP_STACKOVERFLOW);

		Set<Class<?>> githubClients = httpServiceProxyRegistry.getClientTypesInGroup(GROUP_GITHUB);
		assertThat(githubClients)
				.hasSize(4)
				.contains(RepositoryService.class)
				.contains(MilestoneService.class)
				.contains(IssueService.class)
				.contains(ReleaseService.class);

		Set<Class<?>> stackOverFlowClients = httpServiceProxyRegistry.getClientTypesInGroup(GROUP_STACKOVERFLOW);
		assertThat(stackOverFlowClients)
				.hasSize(1)
				.contains(QuestionService.class);

	}

}

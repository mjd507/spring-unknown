package com.jiandong.core.http;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import com.jiandong.core.http.apicontract.github.IssueService;
import com.jiandong.core.http.apicontract.github.MilestoneService;
import com.jiandong.core.http.apicontract.github.ReleaseService;
import com.jiandong.core.http.apicontract.github.RepositoryService;
import com.jiandong.core.http.apicontract.github.resp.Assignee;
import com.jiandong.core.http.apicontract.github.resp.Issue;
import com.jiandong.core.http.apicontract.github.resp.Milestone;
import com.jiandong.core.http.apicontract.github.resp.Release;
import com.jiandong.core.http.apicontract.github.resp.Repository;
import com.jiandong.core.http.apicontract.stackoverflow.Container;
import com.jiandong.core.http.apicontract.stackoverflow.Question;
import com.jiandong.core.http.apicontract.stackoverflow.QuestionService;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

class HttpInterfaceClientTest {

	private static final Logger log = LoggerFactory.getLogger(HttpInterfaceClientTest.class);

	private final JsonMapper jsonMapper = new JsonMapper();

	private final String org = "mjd507";

	private final String repo = "spring-unknown";

	private final Dispatcher dispatcher = new Dispatcher() {

		@Override
		public @NonNull MockResponse dispatch(RecordedRequest request) throws InterruptedException {

			return switch (request.getUrl().encodedPath()) {
				case "/repos/" + org + "/" + repo -> {
					var repository = new Repository("mock-spring-unknow", 1, 1, 2);
					yield new MockResponse.Builder()
							.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
							.body(jsonMapper.writeValueAsString(repository))
							.build();
				}
				case "/repos/" + org + "/" + repo + "/milestones" -> {
					Milestone milestone = new Milestone(11, "mock-milestone", 1, 1, ZonedDateTime.now().plusDays(2));
					yield new MockResponse.Builder()
							.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
							.body(jsonMapper.writeValueAsString(List.of(milestone)))
							.build();
				}
				case "/repos/" + org + "/" + repo + "/issues" -> {
					Issue issue = new Issue("1", 1, "mock-issue", new Assignee("mock-assignee"));
					yield new MockResponse.Builder()
							.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
							.body(jsonMapper.writeValueAsString(List.of(issue)))
							.build();
				}
				case "/repos/" + org + "/" + repo + "/releases" -> {
					Release release = new Release("mock-tag", ZonedDateTime.now().plusDays(20));
					yield new MockResponse.Builder()
							.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
							.body(jsonMapper.writeValueAsString(List.of(release)))
							.build();
				}
				case "/questions" -> {
					Container<@NonNull Question> container = new Container<>(List.of(new Question("mock-question")));
					yield new MockResponse.Builder()
							.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
							.body(jsonMapper.writeValueAsString(container))
							.build();
				}
				default -> new MockResponse.Builder()
						.code(404)
						.build();
			};
		}
	};

	private MockWebServer githubServer;

	private MockWebServer stackOverFlowServer;

	@BeforeEach
	void startServer() throws IOException {
		this.githubServer = new MockWebServer();
		this.githubServer.start();
		this.githubServer.setDispatcher(dispatcher);

		this.stackOverFlowServer = new MockWebServer();
		this.stackOverFlowServer.start();
		this.stackOverFlowServer.setDispatcher(dispatcher);
	}

	@AfterEach
	void shutdown() {
		if (this.githubServer != null) {
			this.githubServer.close();
		}
		if (this.stackOverFlowServer != null) {
			this.stackOverFlowServer.close();
		}
	}

	@Test
	void httpInterfaceClient() {
		// use the HttpServiceProxyFactory to generate the http interface client.
		// which uses the specified RestClient.
		// and the RestClient are bind to the mock server.
		RestClient githubRestClient = RestClient.builder()
				.baseUrl(this.githubServer.url("/").toString())
				.defaultHeader("Accept", "application/json")
				.configureMessageConverters(clientBuilder -> clientBuilder
						.registerDefaults()
						.withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper)))
				.build();
		HttpServiceProxyFactory githubHttpServiceFactory = HttpServiceProxyFactory
				.builderFor(RestClientAdapter.create(githubRestClient)).build();
		RepositoryService repositoryService = githubHttpServiceFactory.createClient(RepositoryService.class);
		IssueService issueService = githubHttpServiceFactory.createClient(IssueService.class);
		MilestoneService milestoneService = githubHttpServiceFactory.createClient(MilestoneService.class);
		ReleaseService releaseService = githubHttpServiceFactory.createClient(ReleaseService.class);

		RestClient stackOverFlowRestClient = RestClient.builder()
				.baseUrl(this.stackOverFlowServer.url("/").toString())
				.configureMessageConverters(clientBuilder -> clientBuilder
						.registerDefaults()
						.withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper)))
				.build();
		HttpServiceProxyFactory stackOverFlowServiceFactory = HttpServiceProxyFactory
				.builderFor(RestClientAdapter.create(stackOverFlowRestClient)).build();
		QuestionService questionService = stackOverFlowServiceFactory.createClient(QuestionService.class);

		HttpCaller httpCaller = new HttpCaller(repositoryService, issueService, milestoneService, releaseService, questionService);
		ReflectionTestUtils.setField(httpCaller, "org", org);
		ReflectionTestUtils.setField(httpCaller, "repo", repo);
		HttpCaller.Result result = httpCaller.call();
		Assertions.assertEquals("mock-spring-unknow", result.repository().name());
		Assertions.assertEquals("mock-issue", result.issues().get(0).title());
		Assertions.assertEquals("mock-milestone", result.milestones().get(0).title());
		Assertions.assertEquals("mock-tag", result.releases().get(0).tag_name());
		Assertions.assertEquals("mock-question", result.questions().items().get(0).title());
	}

}

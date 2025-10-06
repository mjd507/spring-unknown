package com.jiandong.core.http;

import java.util.List;

import com.jiandong.core.http.apicontract.github.IssueService;
import com.jiandong.core.http.apicontract.github.MilestoneService;
import com.jiandong.core.http.apicontract.github.ReleaseService;
import com.jiandong.core.http.apicontract.github.RepositoryService;
import com.jiandong.core.http.apicontract.github.resp.Issue;
import com.jiandong.core.http.apicontract.github.resp.Milestone;
import com.jiandong.core.http.apicontract.github.resp.Release;
import com.jiandong.core.http.apicontract.github.resp.Repository;
import com.jiandong.core.http.apicontract.stackoverflow.Container;
import com.jiandong.core.http.apicontract.stackoverflow.Question;
import com.jiandong.core.http.apicontract.stackoverflow.QuestionService;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A http interface client test component
 */
@Component
public class HttpCaller {

	private static final Logger log = LoggerFactory.getLogger(HttpCaller.class);

	private final RepositoryService repositoryService;

	private final IssueService issueService;

	private final MilestoneService milestoneService;

	private final ReleaseService releaseService;

	private final QuestionService questionService;

	@Value("${github.org:mjd507}")
	private String org;

	@Value("${github.repo:spring-unknown}")
	private String repo;

	public HttpCaller(RepositoryService repositoryService, IssueService issueService, MilestoneService milestoneService, ReleaseService releaseService, QuestionService questionService) {
		this.repositoryService = repositoryService;
		this.issueService = issueService;
		this.milestoneService = milestoneService;
		this.releaseService = releaseService;
		this.questionService = questionService;
	}

	public Result call() {
		Repository repository = repositoryService.getRepository(org, repo);
		List<Milestone> milestones = milestoneService.getMilestones(org, repo);
		List<Issue> issues = issueService.getOpenIssuesForMilestone(org, repo, milestones.get(0).number());
		List<Release> releases = releaseService.getRecentReleases(org, repo);
		Container<@NonNull Question> container = questionService.questions(repo, "votes");
		Result result = new Result(repository, milestones, issues, releases, container);
		log.info(result.toString());
		return result;
	}

	public record Result(Repository repository, List<Milestone> milestones, List<Issue> issues,
						 List<Release> releases, Container<@NonNull Question> questions) {

	}

}

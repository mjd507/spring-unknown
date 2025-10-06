package com.jiandong.core.http.apicontract.github;

import java.util.List;

import com.jiandong.core.http.apicontract.github.req.State;
import com.jiandong.core.http.apicontract.github.resp.Assignee;
import com.jiandong.core.http.apicontract.github.resp.Issue;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;

@HttpExchange("/repos/{org}/{repo}/issues")
public interface IssueService {

	@GetExchange
	List<Issue> getIssuesForMilestone(@PathVariable String org, @PathVariable String repo,
			@RequestParam int milestone, @RequestParam State state);

	default List<Issue> getOpenIssuesForMilestone(String org, String repo, int milestone) {
		List<Issue> issues = getIssuesForMilestone(org, repo, milestone, State.open);
		issues.sort(comparing(Issue::assignee, nullsLast(comparing(Assignee::login))));
		return issues;
	}

}
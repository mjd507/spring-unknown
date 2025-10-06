package com.jiandong.core.http.apicontract.github;

import java.util.List;

import com.jiandong.core.http.apicontract.github.resp.Milestone;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface MilestoneService {

	@GetExchange("/repos/{org}/{repo}/milestones")
	List<Milestone> getMilestones(@PathVariable String org, @PathVariable String repo);

}
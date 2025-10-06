package com.jiandong.core.http.apicontract.github;

import com.jiandong.core.http.apicontract.github.resp.Repository;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/repos/{org}")
public interface RepositoryService {

	@GetExchange("/{repo}")
	Repository getRepository(@PathVariable String org, @PathVariable String repo);

}
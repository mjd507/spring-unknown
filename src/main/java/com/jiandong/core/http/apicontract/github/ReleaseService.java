package com.jiandong.core.http.apicontract.github;

import java.util.List;

import com.jiandong.core.http.apicontract.github.resp.Release;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/repos/{org}/{repo}/releases")
public interface ReleaseService {

	@GetExchange
	List<Release> getReleases(@PathVariable String org, @PathVariable String repo);

	default List<Release> getRecentReleases(String org, String repo) {
		List<Release> releases = getReleases(org, repo);
		return releases.stream().filter(Release::isRecent).toList();
	}

}

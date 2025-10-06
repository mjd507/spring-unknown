package com.jiandong.core.http.apicontract.github.resp;

import java.text.DecimalFormat;

public record Repository(String name, int stargazers_count, int forks_count, int watchers_count) {

	public String toString() {
		return "Project " + name() + ": " +
				DecimalFormat.getInstance().format(stargazers_count) + " stars, " +
				DecimalFormat.getInstance().format(forks_count) + " forks, " +
				DecimalFormat.getInstance().format(watchers_count) + " watchers";
	}

}
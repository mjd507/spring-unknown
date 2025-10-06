package com.jiandong.core.http.apicontract.github.resp;

import org.jspecify.annotations.Nullable;

public record Issue(String id, int number, String title, @Nullable Assignee assignee) {

	public String toString() {
		return "#" + number + " \"" + title + "\", " +
				(assignee != null ? "assigned to " + assignee.login() : "not assigned");
	}

}
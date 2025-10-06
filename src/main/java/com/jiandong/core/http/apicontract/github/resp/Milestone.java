package com.jiandong.core.http.apicontract.github.resp;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.jspecify.annotations.Nullable;

public record Milestone(int number, String title, int open_issues, int closed_issues, @Nullable ZonedDateTime due_on)
		implements Comparable<Milestone> {

	public boolean hasDueDate() {
		return (this.due_on != null);
	}

	@Override
	public int compareTo(Milestone other) {
		return other.title.compareTo(title);
	}

	@Override
	public String toString() {
		return "Milestone " + title + ", " +
				"opened " + open_issues + ", closed " + closed_issues +
				(hasDueDate() ? ", due on " + due_on.format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
	}

}
package com.jiandong.core.http.apicontract.github.resp;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public record Release(String tag_name, ZonedDateTime created_at) {

	public boolean isRecent() {
		return created_at().plusMonths(2).isAfter(ZonedDateTime.now());
	}

	public String toString() {
		return "[" + created_at.format(DateTimeFormatter.ISO_LOCAL_DATE) + "] release " + tag_name;
	}

}
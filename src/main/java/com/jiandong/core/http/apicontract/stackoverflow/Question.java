package com.jiandong.core.http.apicontract.stackoverflow;

import org.springframework.web.util.HtmlUtils;

public record Question(String title) {

	public String toString() {
		return "\"" + HtmlUtils.htmlUnescape(title) + "\"";
	}

}
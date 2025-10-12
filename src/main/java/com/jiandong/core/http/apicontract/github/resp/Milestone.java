package com.jiandong.core.http.apicontract.github.resp;

import java.time.ZonedDateTime;

import org.jspecify.annotations.Nullable;

public record Milestone(int number, String title, int open_issues, int closed_issues, @Nullable ZonedDateTime due_on) {

}
package com.jiandong.core.http.apicontract.stackoverflow;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/questions")
public interface QuestionService {

	@GetExchange
	Container<Question> questions(@RequestParam String tagged, @RequestParam String sort);

}
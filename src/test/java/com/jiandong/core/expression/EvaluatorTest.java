package com.jiandong.core.expression;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EvaluatorTest {

	Evaluator evaluator = new Evaluator();

	@Test
	void testEvaluateAgainstStringLiteral() {
		String literalString = evaluator.evaluateAgainstStringLiteral("'Hello World'", String.class);
		Assertions.assertThat(literalString).isEqualTo("Hello World");

		String methodReturnValue = evaluator.evaluateAgainstStringLiteral("'Hello World'.concat('!')", String.class);
		Assertions.assertThat(methodReturnValue).isEqualTo("Hello World!");

		byte[] propertyValue = evaluator.evaluateAgainstStringLiteral("'Hello World'.bytes", byte[].class);
		Assertions.assertThat(propertyValue).isEqualTo("Hello World".getBytes());

		int propertyValue2 = evaluator.evaluateAgainstStringLiteral("'Hello World'.bytes.length", int.class);
		Assertions.assertThat(propertyValue2).isEqualTo("Hello World".length());

		String constructorCall = evaluator.evaluateAgainstStringLiteral("new String('hello world').toUpperCase()", String.class);
		Assertions.assertThat(constructorCall).isEqualTo("HELLO WORLD");

	}

	@Test
	void testEvaluateAgainstInventor() {
		Evaluator.Inventor inventor = new Evaluator.Inventor("Nikola Tesla", LocalDateTime.of(1856, 7, 9, 0, 0), "Serbian");
		String name = evaluator.evaluateAgainstInventor(inventor, "name", String.class);
		Assertions.assertThat(name).isEqualTo("Nikola Tesla");

		Boolean nameMatched = evaluator.evaluateAgainstInventor(inventor, "name == 'Nikola Tesla'", boolean.class);
		Assertions.assertThat(nameMatched).isTrue();
	}

}

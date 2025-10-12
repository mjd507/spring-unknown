package com.jiandong.core.expression;

import com.jiandong.support.SupportBean;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {EvaluatorComponent.class, SupportBean.class})
class EvaluatorComponentTest {

	@Autowired EvaluatorComponent evaluatorComponent;

	@Test
	void testBeanPropertyEvaluation() {
		Assertions.assertThat(evaluatorComponent.systemProperties)
				.isNotEmpty()
				.hasSizeGreaterThan(0)
				.containsKeys("java.home", "user.country");

		Assertions.assertThat(evaluatorComponent.number)
				.isEqualTo(123);
	}

}

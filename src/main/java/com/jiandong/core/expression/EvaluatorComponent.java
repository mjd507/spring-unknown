package com.jiandong.core.expression;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EvaluatorComponent {

	@Value("#{ systemProperties }") // systemProperties is a bean provided by framework.
	public Map<String, ?> systemProperties;

	@Value("#{ supportBean.randomNumber }") // evaluate value from another bean's property.
	public int number;

}

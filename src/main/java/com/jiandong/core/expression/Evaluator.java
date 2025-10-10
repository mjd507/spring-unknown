package com.jiandong.core.expression;

import java.time.LocalDateTime;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class Evaluator {

	SpelExpressionParser parser = new SpelExpressionParser();

	// ===  basic evaluation cases === //

	// 1. literal string.  "'Hello World'"  --- StringLiteral
	// 2. method call.  "'Hello World'.concat('!')" --- StringLiteral/MethodReference
	// 3. property/field call.
	// 		"'Hello World'.bytes"   => invokes 'getBytes()'   --- StringLiteral/PropertyOrFieldReference
	// 	    "'Hello World'.bytes.length" => invokes 'getBytes().length' --- StringLiteral/PropertyOrFieldReference/PropertyOrFieldReference
	// 4. constructor call. "new String('hello world').toUpperCase()"  --- ConstructorReference/MethodReference
	public <T> T evaluateAgainstStringLiteral(String str, Class<T> desiredResultType) {
		Expression expression = parser.parseExpression(str);
		return expression.getValue(desiredResultType);
	}

	// === the more common usage of SpEL is to provide an expression string
	// that is evaluated against a specific object instance (call root object). === //

	public <T> T evaluateAgainstInventor(Inventor inventor, String expressionString, Class<T> desiredResultType) {
		Expression expression = parser.parseExpression(expressionString);
		return expression.getValue(inventor, desiredResultType);
	}

	public record Inventor(String name, LocalDateTime birthday, String nationality) {

	}

}

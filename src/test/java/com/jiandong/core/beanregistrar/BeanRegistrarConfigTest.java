package com.jiandong.core.beanregistrar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {BeanRegistrarConfig.class})
@ActiveProfiles("baz")
class BeanRegistrarConfigTest {

	@Autowired BeanRegistrarConfig.Foo foo;

	@Autowired BeanRegistrarConfig.Bar bar;

	@Autowired BeanRegistrarConfig.Baz baz;

	@Autowired ApplicationContext applicationContext;

	@Test
	void test() {
		BeanRegistrarConfig.Foo foo2 = applicationContext.getBean(BeanRegistrarConfig.Foo.class);
		Assertions.assertThat(foo)
				.isNotNull()
				.isSameAs(foo2);
		BeanRegistrarConfig.Bar bar2 = applicationContext.getBean(BeanRegistrarConfig.Bar.class);
		Assertions.assertThat(bar)
				.isNotNull()
				.isNotSameAs(bar2);
		Assertions.assertThat(baz).isNotNull();
	}

}

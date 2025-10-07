package com.jiandong;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTest {

    String[] args() {
        return new String[] {
                "--spring.main.web-application-type=none"
        };
    }

    @Test
	void contextLoad() {

    }
}

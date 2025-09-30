package com.jiandong;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ApplicationTest {

    String[] args() {
        return new String[] {
                "--spring.main.web-application-type=none"
        };
    }

	@Disabled // will make other jms integration tests fail. msgs sent to queue may consume by this thread.
    @Test
    void testMain() {
        Application.main(args());
    }
}

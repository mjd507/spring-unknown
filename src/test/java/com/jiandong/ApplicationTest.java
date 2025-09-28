package com.jiandong;

import org.junit.jupiter.api.Test;

class ApplicationTest {

    String[] args() {
        return new String[] {
                "--spring.main.web-application-type=none"
        };
    }

    @Test
    void testMain() {
        Application.main(args());
    }
}

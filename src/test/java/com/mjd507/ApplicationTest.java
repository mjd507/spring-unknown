package com.mjd507;

import org.junit.jupiter.api.Test;

public class ApplicationTest {

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

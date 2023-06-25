
package com.mjd507.aop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestClass {

    @Log
    @GetMapping("/aop-test/{abc}")
    public String test(@PathVariable("abc") String p) {
        return p;
    }
}

package com.mjd507.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityTestController {

    @GetMapping("security1")
    public String test() {
        return "success";
    }

    @GetMapping("security2")
    public String test2() {
        return "success";
    }
}

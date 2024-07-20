package com.jiandong.outbox;

import ch.qos.logback.core.testUtil.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "register")
    void register() {
        userService.register(new UserService.User(RandomUtil.getPositiveInt(),"JD_"+ RandomUtil.getPositiveInt(), 1993));
    }

}

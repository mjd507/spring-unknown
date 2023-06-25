package com.mjd507.jms;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JmsTestController {

    private final MsgSender msgSender;

    @GetMapping("send")
    public void send() {
        msgSender.send();
    }
}
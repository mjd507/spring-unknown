package com.jiandong.jms;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MsgSender {
    private final JmsMessagingTemplate jmsMessagingTemplate;

    public void send() {
        jmsMessagingTemplate.convertAndSend("amq", new Email("info@example.com", "Hello"));
    }
}

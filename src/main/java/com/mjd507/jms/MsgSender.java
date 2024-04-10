package com.mjd507.jms;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import jakarta.jms.Queue;

@Component
@RequiredArgsConstructor
public class MsgSender {
    private final JmsMessagingTemplate jmsMessagingTemplate;

    public void send() {
        jmsMessagingTemplate.convertAndSend("amq", new Email("info@example.com", "Hello"));
    }
}

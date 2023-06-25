package com.mjd507.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MsgReceiver {

    @JmsListener(destination = "amq")
    public void receive(Email email) {
        log.info("Received < {} >", email);
    }
}

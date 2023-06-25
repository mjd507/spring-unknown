package com.mjd507.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class MsgController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat")
    @SendTo("/ws/topic/messages")
    public MsgModel send(MsgModel message) throws Exception {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        MsgModel msgModel = new MsgModel(message.getFrom(), message.getText() + "_" + time);
        System.out.println(msgModel);
        return msgModel;
    }

    @Scheduled(fixedRate = 10 * 60*1000)
    public void schedule() {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        MsgModel msgModel = new MsgModel("mjd", "time" + "_" + time);
        simpMessagingTemplate.convertAndSend("/topic/messages", msgModel);
    }
}

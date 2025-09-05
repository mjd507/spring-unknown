package com.jiandong.websocket;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Profile("websocket")
@Controller
public class WsMsgController {

	private final SimpMessagingTemplate simpMessagingTemplate;

	public WsMsgController(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@MessageMapping("/chat")
	@SendTo("/ws/topic/messages")
	public WsMsgModel send(WsMsgModel message) throws Exception {
		String time = new SimpleDateFormat("HH:mm").format(new Date());
		WsMsgModel wsMsgModel = new WsMsgModel(message.from(), message.text() + "_" + time);
		System.out.println(wsMsgModel);
		return wsMsgModel;
	}

	@Scheduled(fixedRate = 10 * 60 * 1000)
	public void schedule() {
		String time = new SimpleDateFormat("HH:mm").format(new Date());
		WsMsgModel wsMsgModel = new WsMsgModel("mjd", "time" + "_" + time);
		simpMessagingTemplate.convertAndSend("/topic/messages", wsMsgModel);
	}

}

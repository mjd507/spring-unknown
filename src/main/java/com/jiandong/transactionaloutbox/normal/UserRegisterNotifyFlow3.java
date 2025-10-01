package com.jiandong.transactionaloutbox.normal;

import com.jiandong.transactionaloutbox.UserRegister;
import com.jiandong.transactionaloutbox.UserRegisterReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegisterNotifyFlow3 {

	private static final Logger log = LoggerFactory.getLogger(UserRegisterNotifyFlow3.class);

	private static final String EVENT_TYPE = "USER_REGISTER_NOTIFY";

	private final UserRegisterDao userRegisterDao;

	private final OutboxEventDao outboxEventDao;

	private final TaskExecutor taskExecutor;

	private final NotifyService notifyService;

	public UserRegisterNotifyFlow3(UserRegisterDao userRegisterDao, OutboxEventDao outboxEventDao, TaskExecutor taskExecutor, NotifyService notifyService) {
		this.userRegisterDao = userRegisterDao;
		this.outboxEventDao = outboxEventDao;
		this.notifyService = notifyService;
		this.taskExecutor = taskExecutor;
	}

	@Transactional
	public void register(UserRegisterReq registerReq) {
		UserRegister user = userRegisterDao.insertUser(registerReq);

		String eventBody = user.id() + ":" + registerReq.email();
		OutboxEvent outboxEvent = outboxEventDao.addOutboxEvent(EVENT_TYPE, eventBody);
		taskExecutor.execute(() -> notifyService.sendNotification(outboxEvent));
	}

}

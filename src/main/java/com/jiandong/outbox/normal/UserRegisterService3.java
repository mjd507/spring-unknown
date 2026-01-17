package com.jiandong.outbox.normal;

import com.jiandong.outbox.UserRegister;
import com.jiandong.outbox.UserRegisterDao;
import com.jiandong.outbox.UserRegisterReq;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegisterService3 {

	private final UserRegisterDao userRegisterDao;

	private final OutboxEventDao outboxEventDao;

	private final TaskExecutor applicationTaskExecutor;

	private final NotifyService3 notifyService3;

	public UserRegisterService3(UserRegisterDao userRegisterDao, OutboxEventDao outboxEventDao, TaskExecutor applicationTaskExecutor, NotifyService3 notifyService3) {
		this.userRegisterDao = userRegisterDao;
		this.outboxEventDao = outboxEventDao;
		this.notifyService3 = notifyService3;
		this.applicationTaskExecutor = applicationTaskExecutor;
	}

	@Transactional
	public void register(UserRegisterReq registerReq) {
		UserRegister user = userRegisterDao.insertUser(registerReq);

		String eventBody = user.id() + ":" + registerReq.email();
		OutboxEvent outboxEvent = outboxEventDao.addOutboxEvent("USER_REGISTER", eventBody);
		applicationTaskExecutor.execute(() -> notifyService3.sendNotification(outboxEvent));
	}

}

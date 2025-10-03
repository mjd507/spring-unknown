package com.jiandong.transactionaloutbox.modulith;

import com.jiandong.transactionaloutbox.UserRegister;
import com.jiandong.transactionaloutbox.UserRegisterDao;
import com.jiandong.transactionaloutbox.UserRegisterReq;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegisterService2 {

	private final UserRegisterDao userRegisterDao;

	private final ApplicationEventPublisher applicationEventPublisher;

	public UserRegisterService2(UserRegisterDao userRegisterDao, ApplicationEventPublisher applicationEventPublisher) {
		this.userRegisterDao = userRegisterDao;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Transactional
	public void register(UserRegisterReq registerReq) {
		UserRegister userRegister = userRegisterDao.insertUser(registerReq);

		RegisteredEvent outboxEvent = new RegisteredEvent(userRegister.name(), userRegister.email());
		applicationEventPublisher.publishEvent(outboxEvent);
	}

}

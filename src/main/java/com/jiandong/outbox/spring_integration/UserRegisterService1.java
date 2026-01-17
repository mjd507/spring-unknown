package com.jiandong.outbox.spring_integration;

import com.jiandong.outbox.UserRegister;
import com.jiandong.outbox.UserRegisterDao;
import com.jiandong.outbox.UserRegisterReq;

import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegisterService1 {

	private final UserRegisterDao userRegisterDao;

	private final DirectChannel registeredChannel;

	public UserRegisterService1(UserRegisterDao userRegisterDao, DirectChannel registeredChannel) {
		this.userRegisterDao = userRegisterDao;
		this.registeredChannel = registeredChannel;
	}

	@Transactional
	public void register(UserRegisterReq registerReq) {
		UserRegister userRegister = userRegisterDao.insertUser(registerReq);
		registeredChannel.send(new GenericMessage<>(userRegister));
	}

}

package com.jiandong.transactionaloutbox.modulith;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	private final Logger log = LoggerFactory.getLogger(UserService.this.getClass());

	private final UserRepository userRepository;

	private final ApplicationEventPublisher applicationEventPublisher;

	public UserService(UserRepository userRepository, ApplicationEventPublisher applicationEventPublisher) {
		this.userRepository = userRepository;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Entity
	@Table(name = "user")
	public record User(
			@Id Integer id,
			String name,
			Integer birthYear) {

	}

	public record UserRegisteredEvent(int id) {

	}

	@Transactional
	void register(User user) {
		User registeredUser = userRepository.save(user);
		applicationEventPublisher.publishEvent(new UserRegisteredEvent(registeredUser.id()));
	}

	@ApplicationModuleListener
	void onUserRegistered(UserRegisteredEvent userRegisteredEvent) {
		log.info("user registered. id:{}", userRegisteredEvent.id());
	}

}

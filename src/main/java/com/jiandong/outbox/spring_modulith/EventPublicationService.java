package com.jiandong.outbox.spring_modulith;

import java.util.List;

import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.events.core.TargetEventPublication;
import org.springframework.stereotype.Service;

/**
 * service for retrieving outbox events saved from spring-modulith.
 * see outbox table schema: <a href="https://docs.spring.io/spring-modulith/reference/appendix.html#schemas">schemas</a>
 */
@Service
public class EventPublicationService {

	private final EventPublicationRepository eventPublicationRepository;

	public EventPublicationService(EventPublicationRepository eventPublicationRepository) {
		this.eventPublicationRepository = eventPublicationRepository;
	}

	public List<TargetEventPublication> listInCompleteEvents() {
		return eventPublicationRepository.findIncompletePublications();
	}

	public List<TargetEventPublication> listCompleteEvents() {
		return eventPublicationRepository.findCompletedPublications();
	}

}

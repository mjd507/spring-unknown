package com.jiandong.outbox.normal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

@Service
public class OutboxEventDao {

	private final JdbcClient jdbcClient;

	public OutboxEventDao(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public OutboxEvent addOutboxEvent(String eventType, String eventBody) {
		LocalDateTime now = LocalDateTime.now();
		GeneratedKeyHolder eventKeyHolder = new GeneratedKeyHolder();
		jdbcClient.sql("insert into outbox_event (event_type, event_body, event_date, complete_date) "
						+ "values (:eventType, :eventBody, :eventDate, :completeDate)")
				.param("eventType", eventType)
				.param("eventBody", eventBody)
				.param("eventDate", now)
				.param("completeDate", null)
				.update(eventKeyHolder);
		Number key = (Number) Objects.requireNonNull(eventKeyHolder.getKeys()).get("id");
		return new OutboxEvent(key.intValue(), eventType, eventBody, now, null);
	}

	public void completeOutboxEvent(OutboxEvent outboxEvent) {
		jdbcClient.sql("update outbox_event set complete_date = :completeDate where id = :id")
				.param("id", outboxEvent.id())
				.param("completeDate", LocalDateTime.now())
				.update();
	}

	public List<OutboxEvent> listNonCompletedEvents(LocalDateTime lastEventDate) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime eventDate = lastEventDate == null ? now : now.minusMinutes(3);
		return jdbcClient.sql("select * from outbox_event where complete_date is null and event_date <= :eventDate")
				.param("eventDate", eventDate)
				.query(OutboxEvent.class)
				.list();
	}

}

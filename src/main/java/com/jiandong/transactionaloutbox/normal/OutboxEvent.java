package com.jiandong.transactionaloutbox.normal;

import java.time.LocalDateTime;

public record OutboxEvent(Integer id, String eventType, String eventBody,
						  LocalDateTime eventDate, LocalDateTime completeDate) {

}
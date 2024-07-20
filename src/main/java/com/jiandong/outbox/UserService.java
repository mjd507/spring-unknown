package com.jiandong.outbox;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "user")
    public static class User {
        @Id
        Integer id;
        String name;
        Integer birthYear;
    }

    public record UserRegisteredEvent(int id) {
    }

    @Transactional
    void register(User user) {
        User registeredUser = userRepository.save(user);
        applicationEventPublisher.publishEvent(new UserRegisteredEvent(registeredUser.getId()));
    }

    @ApplicationModuleListener
    void onUserRegistered(UserRegisteredEvent userRegisteredEvent) {
        log.info("user registered. id:{}", userRegisteredEvent.id());
    }
}

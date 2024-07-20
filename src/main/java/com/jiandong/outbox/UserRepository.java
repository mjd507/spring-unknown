package com.jiandong.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserService.User, Long> {
}

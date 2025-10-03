package com.jiandong.transactionaloutbox;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

@Service
public class UserRegisterDao {

	private final JdbcClient jdbcClient;

	public UserRegisterDao(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public UserRegister insertUser(UserRegisterReq registerReq) {
		LocalDateTime now = LocalDateTime.now();
		GeneratedKeyHolder userRegisterKeyHolder = new GeneratedKeyHolder();
		jdbcClient
				.sql("insert into user_register (name, email, create_date) "
						+ "values(:name, :email, :createDate)")
				.param("name", registerReq.name())
				.param("email", registerReq.email())
				.param("createDate", now)
				.update(userRegisterKeyHolder);
		Number id = userRegisterKeyHolder.getKey();
		return new UserRegister(Objects.requireNonNull(id).intValue(), registerReq.name(), registerReq.email());
	}

	public UserRegister findUser(String name) {
		return jdbcClient.sql("select * from user_register where name = :name")
				.param("name", name)
				.query(UserRegister.class)
				.single();
	}

}

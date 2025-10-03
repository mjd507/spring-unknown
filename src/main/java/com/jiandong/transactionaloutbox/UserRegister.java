package com.jiandong.transactionaloutbox;

import java.io.Serializable;

public record UserRegister(Integer id, String name, String email)
		implements Serializable // for spring-integration jdbc message store serialise
{

}
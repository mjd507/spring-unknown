package com.jiandong.support;

import com.jiandong.security.JwtConfig;
import com.jiandong.security.JwtTokenGenerator;
import com.jiandong.security.WebSecurityConfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({WebSecurityConfig.class, JwtConfig.class, JwtTokenGenerator.class, MockMvcWithJwtHelper.class})
public class SecurityContext {

}

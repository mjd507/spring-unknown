package com.jiandong.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity // enable @PreAuthorize
//@EnableGlobalAuthentication
public class WebSecurityConfig {

	private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.headers(headers -> headers
						.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // h2-console render
				.authenticationManager(authenticationManager())
				.httpBasic(withDefaults()) // BasicAuthenticationFilter
//				.formLogin(withDefaults()) // UsernamePasswordAuthenticationFilter
//				.rememberMe(withDefaults()) // RememberMeAuthenticationFilter
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(PathPatternRequestMatcher.pathPattern("/public/**")).permitAll()
						.requestMatchers(PathPatternRequestMatcher.pathPattern("/h2*/**")).permitAll()
						.requestMatchers(PathPatternRequestMatcher.pathPattern("/actuator/**")).permitAll()
						.anyRequest().authenticated()
				);
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		ProviderManager providerManager = new ProviderManager(authenticationProvider);
		providerManager.setEraseCredentialsAfterAuthentication(true);
		return providerManager;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withUsername("user")
				.password(passwordEncoder().encode("user_pwd"))
				// base64(user:user_pwd): dXNlcjp1c2VyX3B3ZA==
				.roles("USER")
				.build();
		UserDetails admin = User.withUsername("admin")
				.password(passwordEncoder().encode("admin_pwd"))
				// base64(admin:admin_pwd): YWRtaW46YWRtaW5fcHdk
				.roles("ADMIN")
				.build();
		return new InMemoryUserDetailsManager(List.of(user, admin));
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}

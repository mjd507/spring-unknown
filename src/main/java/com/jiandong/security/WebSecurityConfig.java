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
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
				.authenticationManager(authenticationManager())
				.httpBasic(withDefaults()) // BasicAuthenticationFilter
//				.formLogin(withDefaults()) // UsernamePasswordAuthenticationFilter
//				.rememberMe(withDefaults()) // RememberMeAuthenticationFilter
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(PathPatternRequestMatcher.pathPattern("/public/**")).permitAll()
						.requestMatchers(PathPatternRequestMatcher.pathPattern("/h2*/**")).permitAll()
						.requestMatchers(PathPatternRequestMatcher.pathPattern("/ws/**")).permitAll()
						.anyRequest().authenticated()
				)
//                .x509(httpSecurityX509Configurer -> {
//                    httpSecurityX509Configurer.subjectPrincipalRegex("CN=(.*?)(?:,|$)");
//                    httpSecurityX509Configurer.userDetailsService(x509UserDetailService());
//                })
		;
		return http.build();
	}

	@Bean
	public UserDetailsService x509UserDetailService() {
		return username -> {
			System.out.println("certificate common name (CN) : " + username);
			if (username.equals("Bob")) {
				return new User("user", "pwd", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
			}
			throw new UsernameNotFoundException("User not found!");
		};
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
				// base64(user:user_pwd): dXNlcjokMmEkMTAkRGZ5Ylk5dmJiQ1hySnF2cmw2RUhFT0JzdW1WbVpTaUtsQ0J6REhoZUQzaWNySi5UNHJ1cU8=
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

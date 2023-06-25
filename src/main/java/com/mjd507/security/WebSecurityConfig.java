package com.mjd507.security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity // enable @PreAuthorize
//@EnableGlobalAuthentication
public class WebSecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (webSecurity) -> {
            EndpointRequest.EndpointRequestMatcher anyEndpoint = EndpointRequest.toAnyEndpoint();
            List<RequestMatcher> reqMatchers = new ArrayList<>() {{
                add(new AntPathRequestMatcher("/ws/**"));
                add(anyEndpoint);
            }};
            webSecurity.ignoring().requestMatchers(reqMatchers.toArray(RequestMatcher[]::new));
        };
    }

    @Bean
    public CustomAuthenticationProcessingFilter authenticationProcessingFilter() {
        CustomAuthenticationProcessingFilter authenticationProcessingFilter = new CustomAuthenticationProcessingFilter(authenticationManager());
        authenticationProcessingFilter.setAuthenticationSuccessHandler(new SuccessHandler());
        authenticationProcessingFilter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
        return authenticationProcessingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterAt(authenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(withDefaults())
                .x509(httpSecurityX509Configurer -> {
                    httpSecurityX509Configurer.subjectPrincipalRegex("CN=(.*?)(?:,|$)");
                    httpSecurityX509Configurer.userDetailsService(x509UserDetailService());
                })
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
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User
                .withUsername("user")
                .password(passwordEncoder().encode("pwd"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package com.edupress.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Test configuration providing a minimal UserDetailsService so that
 * AuthenticationManager bean creation in SecurityConfiguration succeeds
 * without wiring the real database-backed service.
 */
@Configuration
public class TestUserDetailsServiceConfiguration {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("test@example.com").password(passwordEncoder.encode("password")).roles("USER").build();

        return new InMemoryUserDetailsManager(user);
    }
}

package com.edupress.security;

import com.edupress.domain.AppUser;
import com.edupress.domain.enumeration.Role;
import com.edupress.repository.AppUserRepository;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
@Primary
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final AppUserRepository appUserRepository;

    public DomainUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);

        return appUserRepository
            .findOneByEmail(login.toLowerCase())
            .map(this::createSpringSecurityUser)
            .orElseThrow(() -> new UsernameNotFoundException("User with email " + login + " was not found in the database"));
    }

    private User createSpringSecurityUser(AppUser appUser) {
        var authority = appUser.getRole() == Role.ADMIN
            ? new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)
            : new SimpleGrantedAuthority(AuthoritiesConstants.USER);
        return new User(appUser.getEmail(), appUser.getPassword(), Collections.singletonList(authority));
    }
}

package com.edupress.security;

import com.edupress.domain.AppUser;
import com.edupress.domain.enumeration.Role;
import com.edupress.repository.AppUserRepository;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Creates an AppUser profile with STUDENT role on first successful authentication.
 */
@Component
public class SignupListener {

    private static final Logger LOG = LoggerFactory.getLogger(SignupListener.class);

    private final AppUserRepository appUserRepository;

    public SignupListener(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @EventListener
    public void onInteractiveAuthSuccess(InteractiveAuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            return; // Only handle JWT based logins
        }
        Map<String, Object> details = SecurityUtils.extractDetailsFromTokenAttributes(jwtAuth.getTokenAttributes());
        String email = Optional.ofNullable(details.get("email")).map(Object::toString).orElse(null);
        if (email == null) {
            LOG.debug("SignupListener: no email in token, skipping AppUser provisioning");
            return;
        }
        appUserRepository
            .findOneByEmail(email)
            .ifPresentOrElse(
                existing -> {},
                () -> {
                    AppUser user = new AppUser();
                    user.setEmail(email);
                    user.setFirstName(Optional.ofNullable(details.get("firstName")).map(Object::toString).orElse(null));
                    user.setLastName(Optional.ofNullable(details.get("lastName")).map(Object::toString).orElse(null));
                    user.setProfilePictureUrl(Optional.ofNullable(details.get("imageUrl")).map(Object::toString).orElse(null));
                    user.setIsActive(true);
                    user.setRole(Role.STUDENT);
                    appUserRepository.save(user);
                    LOG.info("Provisioned AppUser profile for {} with STUDENT role", email);
                }
            );
    }
}

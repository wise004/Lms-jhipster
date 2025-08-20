package com.edupress.config;

import com.edupress.domain.AppUser;
import com.edupress.domain.enumeration.Role;
import com.edupress.repository.AppUserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a default admin user in the dev profile for easy login.
 */
@Component
@Profile("dev")
public class DevDataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DevDataInitializer.class);

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataInitializer(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        final String email = "admin@edupress.local";
        final String defaultPassword = "admin";

        appUserRepository
            .findOneByEmail(email)
            .ifPresentOrElse(
                u -> LOG.info("Dev admin already exists: {}", email),
                () -> {
                    AppUser admin = new AppUser();
                    admin.setEmail(email);
                    admin.setFirstName("Dev");
                    admin.setLastName("Admin");
                    admin.setPassword(passwordEncoder.encode(defaultPassword));
                    admin.setRole(Role.ADMIN);
                    admin.setIsActive(true);
                    appUserRepository.save(admin);
                    LOG.warn("Seeded dev admin: {} with password '{}'. Change it in production!", email, defaultPassword);
                }
            );

        // Ensure all existing fake users have a password in dev so they can log in with their email and the default password
        appUserRepository
            .findAll()
            .forEach(u -> {
                try {
                    final String hash = u.getPassword();
                    final boolean missing = (hash == null || hash.isBlank());
                    final boolean notBcrypt =
                        hash != null && !hash.startsWith("$2a$") && !hash.startsWith("$2b$") && !hash.startsWith("$2y$");
                    if (missing || notBcrypt) {
                        u.setPassword(passwordEncoder.encode(defaultPassword));
                        appUserRepository.save(u);
                        LOG.info("Set default password for dev user {}", u.getEmail());
                    }
                } catch (Exception ex) {
                    LOG.warn("Could not set default password for user {}: {}", u.getEmail(), ex.getMessage());
                }
            });
    }
}

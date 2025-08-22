package com.edupress.config;

import com.edupress.domain.AppUser;
import com.edupress.domain.enumeration.Role;
import com.edupress.repository.AppUserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds an initial admin user in production if none exists yet.
 * Uses environment variables ADMIN_EMAIL / ADMIN_PASSWORD with safe defaults (must change default!).
 */
@Component
@Profile("prod")
public class ProductionAdminInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(ProductionAdminInitializer.class);

    @Value("${ADMIN_EMAIL:admin@edupress.io}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:ChangeMe123!}")
    private String adminPassword;

    @Value("${ADMIN_SEED_ENABLED:true}")
    private boolean seedEnabled;

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public ProductionAdminInitializer(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void seedAdmin() {
        if (!seedEnabled) {
            LOG.info("[ProdSeed] Seeding disabled via ADMIN_SEED_ENABLED=false");
            return;
        }
        try {
            String emailLower = adminEmail.toLowerCase();
            appUserRepository
                .findOneByEmail(emailLower)
                .ifPresentOrElse(
                    u -> LOG.info("[ProdSeed] Admin user already exists: {}", emailLower),
                    () -> {
                        AppUser admin = new AppUser();
                        admin.setEmail(emailLower);
                        admin.setFirstName("Prod");
                        admin.setLastName("Admin");
                        admin.setPassword(passwordEncoder.encode(adminPassword));
                        admin.setRole(Role.ADMIN);
                        admin.setIsActive(true);
                        appUserRepository.save(admin);
                        LOG.warn("[ProdSeed] Created initial admin {} with default password. CHANGE IT IMMEDIATELY.", emailLower);
                    }
                );
        } catch (Exception e) {
            LOG.error(
                "[ProdSeed] Admin seeding failed but application will continue. Set ADMIN_SEED_ENABLED=false to silence. Cause: {}",
                e.getMessage(),
                e
            );
        }
    }
}

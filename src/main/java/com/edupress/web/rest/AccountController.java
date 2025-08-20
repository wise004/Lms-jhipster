package com.edupress.web.rest;

import com.edupress.domain.AppUser;
import com.edupress.domain.enumeration.Role;
import com.edupress.repository.AppUserRepository;
import com.edupress.web.rest.vm.ManagedUserVM;
import jakarta.validation.Valid;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user registration.
 */
@RestController
@RequestMapping("/api")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            return ResponseEntity.badRequest().body("Password must be at least 4 characters long");
        }

        Optional<AppUser> existingUser = appUserRepository.findOneByEmail(managedUserVM.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Email is already in use!");
        }

        existingUser = appUserRepository.findOneByEmail(managedUserVM.getLogin());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Login name already registered!");
        }

        AppUser newUser = createUser(managedUserVM);
        appUserRepository.save(newUser);

        log.debug("Created Information for User: {}", newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful!");
    }

    private static boolean checkPasswordLength(String password) {
        return StringUtils.hasText(password) && password.length() >= 4 && password.length() <= 100;
    }

    private AppUser createUser(ManagedUserVM userVM) {
        AppUser user = new AppUser();
        user.setEmail(userVM.getEmail());
        user.setFirstName(userVM.getFirstName());
        user.setLastName(userVM.getLastName());
        user.setPassword(passwordEncoder.encode(userVM.getPassword()));
        user.setRole(Role.STUDENT); // Default role for new registrations
        user.setIsActive(true); // Set user as active
        return user;
    }
}

package com.edupress.service;

import com.edupress.domain.AppUser;
import com.edupress.repository.AppUserRepository;
import com.edupress.service.dto.AppUserDTO;
import com.edupress.service.mapper.AppUserMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.edupress.domain.AppUser}.
 */
@Service
@Transactional
public class AppUserService {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserService.class);

    private final AppUserRepository appUserRepository;

    private final AppUserMapper appUserMapper;

    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository, AppUserMapper appUserMapper, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Save a appUser.
     *
     * @param appUserDTO the entity to save.
     * @return the persisted entity.
     */
    public AppUserDTO save(AppUserDTO appUserDTO) {
        LOG.debug("Request to save AppUser : {}", appUserDTO);
        AppUser appUser = appUserMapper.toEntity(appUserDTO);
        // Ensure password is present and valid length; DTO doesn't expose password
        if (appUser.getPassword() == null || appUser.getPassword().length() != 60) {
            appUser.setPassword(passwordEncoder.encode("changeme"));
        }
        appUser = appUserRepository.save(appUser);
        return appUserMapper.toDto(appUser);
    }

    /**
     * Update a appUser.
     *
     * @param appUserDTO the entity to save.
     * @return the persisted entity.
     */
    public AppUserDTO update(AppUserDTO appUserDTO) {
        LOG.debug("Request to update AppUser : {}", appUserDTO);
        AppUser appUser = appUserMapper.toEntity(appUserDTO);
        if (appUser.getPassword() == null || appUser.getPassword().length() != 60) {
            appUser.setPassword(passwordEncoder.encode("changeme"));
        }
        appUser = appUserRepository.save(appUser);
        return appUserMapper.toDto(appUser);
    }

    /**
     * Partially update a appUser.
     *
     * @param appUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AppUserDTO> partialUpdate(AppUserDTO appUserDTO) {
        LOG.debug("Request to partially update AppUser : {}", appUserDTO);

        return appUserRepository
            .findById(appUserDTO.getId())
            .map(existingAppUser -> {
                appUserMapper.partialUpdate(existingAppUser, appUserDTO);

                return existingAppUser;
            })
            .map(appUserRepository::save)
            .map(appUserMapper::toDto);
    }

    /**
     * Get one appUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AppUserDTO> findOne(Long id) {
        LOG.debug("Request to get AppUser : {}", id);
        return appUserRepository.findById(id).map(appUserMapper::toDto);
    }

    /**
     * Delete the appUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AppUser : {}", id);
        appUserRepository.deleteById(id);
    }
}

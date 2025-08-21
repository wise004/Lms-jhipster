package com.edupress.web.rest;

import static com.edupress.domain.AppUserAsserts.*;
import static com.edupress.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edupress.IntegrationTest;
import com.edupress.domain.AppUser;
import com.edupress.domain.enumeration.Role;
import com.edupress.repository.AppUserRepository;
import com.edupress.service.dto.AppUserDTO;
import com.edupress.service.mapper.AppUserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AppUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AppUserResourceIT {

    // 60-char dummy hash to satisfy AppUser.password @Size(60)
    private static final String DUMMY_PASSWORD_HASH = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    private static final Role DEFAULT_ROLE = Role.STUDENT;
    private static final Role UPDATED_ROLE = Role.INSTRUCTOR;

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final String DEFAULT_PROFILE_PICTURE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PROFILE_PICTURE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/app-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppUserMockMvc;

    private AppUser appUser;

    private AppUser insertedAppUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createEntity() {
        return new AppUser()
            .role(DEFAULT_ROLE)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .bio(DEFAULT_BIO)
            .profilePictureUrl(DEFAULT_PROFILE_PICTURE_URL)
            .isActive(DEFAULT_IS_ACTIVE)
            .password(DUMMY_PASSWORD_HASH);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createUpdatedEntity() {
        return new AppUser()
            .role(UPDATED_ROLE)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .bio(UPDATED_BIO)
            .profilePictureUrl(UPDATED_PROFILE_PICTURE_URL)
            .isActive(UPDATED_IS_ACTIVE)
            .password(DUMMY_PASSWORD_HASH);
    }

    @BeforeEach
    void initTest() {
        appUser = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAppUser != null) {
            appUserRepository.delete(insertedAppUser);
            insertedAppUser = null;
        }
    }

    @Test
    @Transactional
    void createAppUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);
        var returnedAppUserDTO = om.readValue(
            restAppUserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AppUserDTO.class
        );

        // Validate the AppUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppUser = appUserMapper.toEntity(returnedAppUserDTO);
        assertAppUserUpdatableFieldsEquals(returnedAppUser, getPersistedAppUser(returnedAppUser));

        insertedAppUser = returnedAppUser;
    }

    @Test
    @Transactional
    void createAppUserWithExistingId() throws Exception {
        // Create the AppUser with an existing ID
        appUser.setId(1L);
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appUser.setRole(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appUser.setEmail(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appUser.setIsActive(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAppUsers() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].profilePictureUrl").value(hasItem(DEFAULT_PROFILE_PICTURE_URL)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getAppUser() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get the appUser
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL_ID, appUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appUser.getId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.bio").value(DEFAULT_BIO))
            .andExpect(jsonPath("$.profilePictureUrl").value(DEFAULT_PROFILE_PICTURE_URL))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getAppUsersByIdFiltering() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        Long id = appUser.getId();

        defaultAppUserFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAppUserFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAppUserFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAppUsersByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where role equals to
        defaultAppUserFiltering("role.equals=" + DEFAULT_ROLE, "role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllAppUsersByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where role in
        defaultAppUserFiltering("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE, "role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllAppUsersByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where role is not null
        defaultAppUserFiltering("role.specified=true", "role.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where firstName equals to
        defaultAppUserFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where firstName in
        defaultAppUserFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where firstName is not null
        defaultAppUserFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where firstName contains
        defaultAppUserFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where firstName does not contain
        defaultAppUserFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where lastName equals to
        defaultAppUserFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where lastName in
        defaultAppUserFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where lastName is not null
        defaultAppUserFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByLastNameContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where lastName contains
        defaultAppUserFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where lastName does not contain
        defaultAppUserFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where email equals to
        defaultAppUserFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllAppUsersByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where email in
        defaultAppUserFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllAppUsersByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where email is not null
        defaultAppUserFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where email contains
        defaultAppUserFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllAppUsersByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where email does not contain
        defaultAppUserFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phone equals to
        defaultAppUserFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phone in
        defaultAppUserFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phone is not null
        defaultAppUserFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phone contains
        defaultAppUserFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phone does not contain
        defaultAppUserFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllAppUsersByProfilePictureUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where profilePictureUrl equals to
        defaultAppUserFiltering(
            "profilePictureUrl.equals=" + DEFAULT_PROFILE_PICTURE_URL,
            "profilePictureUrl.equals=" + UPDATED_PROFILE_PICTURE_URL
        );
    }

    @Test
    @Transactional
    void getAllAppUsersByProfilePictureUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where profilePictureUrl in
        defaultAppUserFiltering(
            "profilePictureUrl.in=" + DEFAULT_PROFILE_PICTURE_URL + "," + UPDATED_PROFILE_PICTURE_URL,
            "profilePictureUrl.in=" + UPDATED_PROFILE_PICTURE_URL
        );
    }

    @Test
    @Transactional
    void getAllAppUsersByProfilePictureUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where profilePictureUrl is not null
        defaultAppUserFiltering("profilePictureUrl.specified=true", "profilePictureUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByProfilePictureUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where profilePictureUrl contains
        defaultAppUserFiltering(
            "profilePictureUrl.contains=" + DEFAULT_PROFILE_PICTURE_URL,
            "profilePictureUrl.contains=" + UPDATED_PROFILE_PICTURE_URL
        );
    }

    @Test
    @Transactional
    void getAllAppUsersByProfilePictureUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where profilePictureUrl does not contain
        defaultAppUserFiltering(
            "profilePictureUrl.doesNotContain=" + UPDATED_PROFILE_PICTURE_URL,
            "profilePictureUrl.doesNotContain=" + DEFAULT_PROFILE_PICTURE_URL
        );
    }

    @Test
    @Transactional
    void getAllAppUsersByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where isActive equals to
        defaultAppUserFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllAppUsersByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where isActive in
        defaultAppUserFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllAppUsersByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where isActive is not null
        defaultAppUserFiltering("isActive.specified=true", "isActive.specified=false");
    }

    private void defaultAppUserFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAppUserShouldBeFound(shouldBeFound);
        defaultAppUserShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppUserShouldBeFound(String filter) throws Exception {
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].profilePictureUrl").value(hasItem(DEFAULT_PROFILE_PICTURE_URL)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));

        // Check, that the count call also returns 1
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAppUserShouldNotBeFound(String filter) throws Exception {
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAppUser() throws Exception {
        // Get the appUser
        restAppUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAppUser() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appUser
        AppUser updatedAppUser = appUserRepository.findById(appUser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAppUser are not directly saved in db
        em.detach(updatedAppUser);
        updatedAppUser
            .role(UPDATED_ROLE)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .bio(UPDATED_BIO)
            .profilePictureUrl(UPDATED_PROFILE_PICTURE_URL)
            .isActive(UPDATED_IS_ACTIVE);
        AppUserDTO appUserDTO = appUserMapper.toDto(updatedAppUser);

        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appUserDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppUserToMatchAllProperties(updatedAppUser);
    }

    @Test
    @Transactional
    void putNonExistingAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appUserDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser.role(UPDATED_ROLE).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL).bio(UPDATED_BIO);

        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppUser))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppUserUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAppUser, appUser), getPersistedAppUser(appUser));
    }

    @Test
    @Transactional
    void fullUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser
            .role(UPDATED_ROLE)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .bio(UPDATED_BIO)
            .profilePictureUrl(UPDATED_PROFILE_PICTURE_URL)
            .isActive(UPDATED_IS_ACTIVE);

        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppUser))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppUserUpdatableFieldsEquals(partialUpdatedAppUser, getPersistedAppUser(partialUpdatedAppUser));
    }

    @Test
    @Transactional
    void patchNonExistingAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appUserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(appUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppUser() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the appUser
        restAppUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, appUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return appUserRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected AppUser getPersistedAppUser(AppUser appUser) {
        return appUserRepository.findById(appUser.getId()).orElseThrow();
    }

    protected void assertPersistedAppUserToMatchAllProperties(AppUser expectedAppUser) {
        assertAppUserAllPropertiesEquals(expectedAppUser, getPersistedAppUser(expectedAppUser));
    }

    protected void assertPersistedAppUserToMatchUpdatableProperties(AppUser expectedAppUser) {
        assertAppUserAllUpdatablePropertiesEquals(expectedAppUser, getPersistedAppUser(expectedAppUser));
    }
}

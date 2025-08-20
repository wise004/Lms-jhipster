package com.edupress.web.rest;

import static com.edupress.domain.AssignmentSubmissionAsserts.*;
import static com.edupress.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edupress.IntegrationTest;
import com.edupress.domain.AppUser;
import com.edupress.domain.Assignment;
import com.edupress.domain.AssignmentSubmission;
import com.edupress.domain.enumeration.SubmissionStatus;
import com.edupress.repository.AssignmentSubmissionRepository;
import com.edupress.service.AssignmentSubmissionService;
import com.edupress.service.dto.AssignmentSubmissionDTO;
import com.edupress.service.mapper.AssignmentSubmissionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AssignmentSubmissionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AssignmentSubmissionResourceIT {

    private static final Instant DEFAULT_SUBMITTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBMITTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SUBMISSION_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_SUBMISSION_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_URLS = "AAAAAAAAAA";
    private static final String UPDATED_FILE_URLS = "BBBBBBBBBB";

    private static final Integer DEFAULT_GRADE = 1;
    private static final Integer UPDATED_GRADE = 2;
    private static final Integer SMALLER_GRADE = 1 - 1;

    private static final String DEFAULT_FEEDBACK = "AAAAAAAAAA";
    private static final String UPDATED_FEEDBACK = "BBBBBBBBBB";

    private static final SubmissionStatus DEFAULT_STATUS = SubmissionStatus.SUBMITTED;
    private static final SubmissionStatus UPDATED_STATUS = SubmissionStatus.GRADED;

    private static final String ENTITY_API_URL = "/api/assignment-submissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

    @Mock
    private AssignmentSubmissionRepository assignmentSubmissionRepositoryMock;

    @Autowired
    private AssignmentSubmissionMapper assignmentSubmissionMapper;

    @Mock
    private AssignmentSubmissionService assignmentSubmissionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssignmentSubmissionMockMvc;

    private AssignmentSubmission assignmentSubmission;

    private AssignmentSubmission insertedAssignmentSubmission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssignmentSubmission createEntity() {
        return new AssignmentSubmission()
            .submittedAt(DEFAULT_SUBMITTED_AT)
            .submissionText(DEFAULT_SUBMISSION_TEXT)
            .fileUrls(DEFAULT_FILE_URLS)
            .grade(DEFAULT_GRADE)
            .feedback(DEFAULT_FEEDBACK)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssignmentSubmission createUpdatedEntity() {
        return new AssignmentSubmission()
            .submittedAt(UPDATED_SUBMITTED_AT)
            .submissionText(UPDATED_SUBMISSION_TEXT)
            .fileUrls(UPDATED_FILE_URLS)
            .grade(UPDATED_GRADE)
            .feedback(UPDATED_FEEDBACK)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        assignmentSubmission = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAssignmentSubmission != null) {
            assignmentSubmissionRepository.delete(insertedAssignmentSubmission);
            insertedAssignmentSubmission = null;
        }
    }

    @Test
    @Transactional
    void createAssignmentSubmission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);
        var returnedAssignmentSubmissionDTO = om.readValue(
            restAssignmentSubmissionMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentSubmissionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssignmentSubmissionDTO.class
        );

        // Validate the AssignmentSubmission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAssignmentSubmission = assignmentSubmissionMapper.toEntity(returnedAssignmentSubmissionDTO);
        assertAssignmentSubmissionUpdatableFieldsEquals(
            returnedAssignmentSubmission,
            getPersistedAssignmentSubmission(returnedAssignmentSubmission)
        );

        insertedAssignmentSubmission = returnedAssignmentSubmission;
    }

    @Test
    @Transactional
    void createAssignmentSubmissionWithExistingId() throws Exception {
        // Create the AssignmentSubmission with an existing ID
        assignmentSubmission.setId(1L);
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssignmentSubmissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentSubmissionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissions() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignmentSubmission.getId().intValue())))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].submissionText").value(hasItem(DEFAULT_SUBMISSION_TEXT)))
            .andExpect(jsonPath("$.[*].fileUrls").value(hasItem(DEFAULT_FILE_URLS)))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE)))
            .andExpect(jsonPath("$.[*].feedback").value(hasItem(DEFAULT_FEEDBACK)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssignmentSubmissionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(assignmentSubmissionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssignmentSubmissionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(assignmentSubmissionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssignmentSubmissionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(assignmentSubmissionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssignmentSubmissionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(assignmentSubmissionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAssignmentSubmission() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get the assignmentSubmission
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL_ID, assignmentSubmission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assignmentSubmission.getId().intValue()))
            .andExpect(jsonPath("$.submittedAt").value(DEFAULT_SUBMITTED_AT.toString()))
            .andExpect(jsonPath("$.submissionText").value(DEFAULT_SUBMISSION_TEXT))
            .andExpect(jsonPath("$.fileUrls").value(DEFAULT_FILE_URLS))
            .andExpect(jsonPath("$.grade").value(DEFAULT_GRADE))
            .andExpect(jsonPath("$.feedback").value(DEFAULT_FEEDBACK))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getAssignmentSubmissionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        Long id = assignmentSubmission.getId();

        defaultAssignmentSubmissionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAssignmentSubmissionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAssignmentSubmissionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsBySubmittedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where submittedAt equals to
        defaultAssignmentSubmissionFiltering("submittedAt.equals=" + DEFAULT_SUBMITTED_AT, "submittedAt.equals=" + UPDATED_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsBySubmittedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where submittedAt in
        defaultAssignmentSubmissionFiltering(
            "submittedAt.in=" + DEFAULT_SUBMITTED_AT + "," + UPDATED_SUBMITTED_AT,
            "submittedAt.in=" + UPDATED_SUBMITTED_AT
        );
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsBySubmittedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where submittedAt is not null
        defaultAssignmentSubmissionFiltering("submittedAt.specified=true", "submittedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByGradeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where grade equals to
        defaultAssignmentSubmissionFiltering("grade.equals=" + DEFAULT_GRADE, "grade.equals=" + UPDATED_GRADE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByGradeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where grade in
        defaultAssignmentSubmissionFiltering("grade.in=" + DEFAULT_GRADE + "," + UPDATED_GRADE, "grade.in=" + UPDATED_GRADE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByGradeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where grade is not null
        defaultAssignmentSubmissionFiltering("grade.specified=true", "grade.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByGradeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where grade is greater than or equal to
        defaultAssignmentSubmissionFiltering("grade.greaterThanOrEqual=" + DEFAULT_GRADE, "grade.greaterThanOrEqual=" + UPDATED_GRADE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByGradeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where grade is less than or equal to
        defaultAssignmentSubmissionFiltering("grade.lessThanOrEqual=" + DEFAULT_GRADE, "grade.lessThanOrEqual=" + SMALLER_GRADE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByGradeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where grade is less than
        defaultAssignmentSubmissionFiltering("grade.lessThan=" + UPDATED_GRADE, "grade.lessThan=" + DEFAULT_GRADE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByGradeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where grade is greater than
        defaultAssignmentSubmissionFiltering("grade.greaterThan=" + SMALLER_GRADE, "grade.greaterThan=" + DEFAULT_GRADE);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where status equals to
        defaultAssignmentSubmissionFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where status in
        defaultAssignmentSubmissionFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        // Get all the assignmentSubmissionList where status is not null
        defaultAssignmentSubmissionFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByAssignmentIsEqualToSomething() throws Exception {
        Assignment assignment;
        if (TestUtil.findAll(em, Assignment.class).isEmpty()) {
            assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);
            assignment = AssignmentResourceIT.createEntity();
        } else {
            assignment = TestUtil.findAll(em, Assignment.class).get(0);
        }
        em.persist(assignment);
        em.flush();
        assignmentSubmission.setAssignment(assignment);
        assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);
        Long assignmentId = assignment.getId();
        // Get all the assignmentSubmissionList where assignment equals to assignmentId
        defaultAssignmentSubmissionShouldBeFound("assignmentId.equals=" + assignmentId);

        // Get all the assignmentSubmissionList where assignment equals to (assignmentId + 1)
        defaultAssignmentSubmissionShouldNotBeFound("assignmentId.equals=" + (assignmentId + 1));
    }

    @Test
    @Transactional
    void getAllAssignmentSubmissionsByStudentIsEqualToSomething() throws Exception {
        AppUser student;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);
            student = AppUserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(student);
        em.flush();
        assignmentSubmission.setStudent(student);
        assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);
        Long studentId = student.getId();
        // Get all the assignmentSubmissionList where student equals to studentId
        defaultAssignmentSubmissionShouldBeFound("studentId.equals=" + studentId);

        // Get all the assignmentSubmissionList where student equals to (studentId + 1)
        defaultAssignmentSubmissionShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    private void defaultAssignmentSubmissionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAssignmentSubmissionShouldBeFound(shouldBeFound);
        defaultAssignmentSubmissionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAssignmentSubmissionShouldBeFound(String filter) throws Exception {
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignmentSubmission.getId().intValue())))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].submissionText").value(hasItem(DEFAULT_SUBMISSION_TEXT)))
            .andExpect(jsonPath("$.[*].fileUrls").value(hasItem(DEFAULT_FILE_URLS)))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE)))
            .andExpect(jsonPath("$.[*].feedback").value(hasItem(DEFAULT_FEEDBACK)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAssignmentSubmissionShouldNotBeFound(String filter) throws Exception {
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAssignmentSubmissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAssignmentSubmission() throws Exception {
        // Get the assignmentSubmission
        restAssignmentSubmissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssignmentSubmission() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignmentSubmission
        AssignmentSubmission updatedAssignmentSubmission = assignmentSubmissionRepository
            .findById(assignmentSubmission.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedAssignmentSubmission are not directly saved in db
        em.detach(updatedAssignmentSubmission);
        updatedAssignmentSubmission
            .submittedAt(UPDATED_SUBMITTED_AT)
            .submissionText(UPDATED_SUBMISSION_TEXT)
            .fileUrls(UPDATED_FILE_URLS)
            .grade(UPDATED_GRADE)
            .feedback(UPDATED_FEEDBACK)
            .status(UPDATED_STATUS);
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(updatedAssignmentSubmission);

        restAssignmentSubmissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignmentSubmissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isOk());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssignmentSubmissionToMatchAllProperties(updatedAssignmentSubmission);
    }

    @Test
    @Transactional
    void putNonExistingAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignmentSubmissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentSubmissionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssignmentSubmissionWithPatch() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignmentSubmission using partial update
        AssignmentSubmission partialUpdatedAssignmentSubmission = new AssignmentSubmission();
        partialUpdatedAssignmentSubmission.setId(assignmentSubmission.getId());

        partialUpdatedAssignmentSubmission
            .submittedAt(UPDATED_SUBMITTED_AT)
            .submissionText(UPDATED_SUBMISSION_TEXT)
            .grade(UPDATED_GRADE)
            .status(UPDATED_STATUS);

        restAssignmentSubmissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignmentSubmission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignmentSubmission))
            )
            .andExpect(status().isOk());

        // Validate the AssignmentSubmission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignmentSubmissionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssignmentSubmission, assignmentSubmission),
            getPersistedAssignmentSubmission(assignmentSubmission)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssignmentSubmissionWithPatch() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignmentSubmission using partial update
        AssignmentSubmission partialUpdatedAssignmentSubmission = new AssignmentSubmission();
        partialUpdatedAssignmentSubmission.setId(assignmentSubmission.getId());

        partialUpdatedAssignmentSubmission
            .submittedAt(UPDATED_SUBMITTED_AT)
            .submissionText(UPDATED_SUBMISSION_TEXT)
            .fileUrls(UPDATED_FILE_URLS)
            .grade(UPDATED_GRADE)
            .feedback(UPDATED_FEEDBACK)
            .status(UPDATED_STATUS);

        restAssignmentSubmissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignmentSubmission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignmentSubmission))
            )
            .andExpect(status().isOk());

        // Validate the AssignmentSubmission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignmentSubmissionUpdatableFieldsEquals(
            partialUpdatedAssignmentSubmission,
            getPersistedAssignmentSubmission(partialUpdatedAssignmentSubmission)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assignmentSubmissionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssignmentSubmission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignmentSubmission.setId(longCount.incrementAndGet());

        // Create the AssignmentSubmission
        AssignmentSubmissionDTO assignmentSubmissionDTO = assignmentSubmissionMapper.toDto(assignmentSubmission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentSubmissionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assignmentSubmissionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssignmentSubmission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssignmentSubmission() throws Exception {
        // Initialize the database
        insertedAssignmentSubmission = assignmentSubmissionRepository.saveAndFlush(assignmentSubmission);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assignmentSubmission
        restAssignmentSubmissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, assignmentSubmission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assignmentSubmissionRepository.count();
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

    protected AssignmentSubmission getPersistedAssignmentSubmission(AssignmentSubmission assignmentSubmission) {
        return assignmentSubmissionRepository.findById(assignmentSubmission.getId()).orElseThrow();
    }

    protected void assertPersistedAssignmentSubmissionToMatchAllProperties(AssignmentSubmission expectedAssignmentSubmission) {
        assertAssignmentSubmissionAllPropertiesEquals(
            expectedAssignmentSubmission,
            getPersistedAssignmentSubmission(expectedAssignmentSubmission)
        );
    }

    protected void assertPersistedAssignmentSubmissionToMatchUpdatableProperties(AssignmentSubmission expectedAssignmentSubmission) {
        assertAssignmentSubmissionAllUpdatablePropertiesEquals(
            expectedAssignmentSubmission,
            getPersistedAssignmentSubmission(expectedAssignmentSubmission)
        );
    }
}

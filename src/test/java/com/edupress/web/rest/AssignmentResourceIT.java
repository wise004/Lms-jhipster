package com.edupress.web.rest;

import static com.edupress.domain.AssignmentAsserts.*;
import static com.edupress.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edupress.IntegrationTest;
import com.edupress.domain.Assignment;
import com.edupress.domain.Course;
import com.edupress.domain.Lesson;
import com.edupress.repository.AssignmentRepository;
import com.edupress.security.AuthoritiesConstants;
import com.edupress.service.dto.AssignmentDTO;
import com.edupress.service.mapper.AssignmentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link AssignmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class AssignmentResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_INSTRUCTIONS = "AAAAAAAAAA";
    private static final String UPDATED_INSTRUCTIONS = "BBBBBBBBBB";

    private static final Instant DEFAULT_DUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DUE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_MAX_POINTS = 1;
    private static final Integer UPDATED_MAX_POINTS = 2;
    private static final Integer SMALLER_MAX_POINTS = 1 - 1;

    private static final String DEFAULT_SUBMISSION_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_SUBMISSION_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_ALLOWED_FILE_TYPES = "AAAAAAAAAA";
    private static final String UPDATED_ALLOWED_FILE_TYPES = "BBBBBBBBBB";

    private static final Integer DEFAULT_MAX_FILE_SIZE = 1;
    private static final Integer UPDATED_MAX_FILE_SIZE = 2;
    private static final Integer SMALLER_MAX_FILE_SIZE = 1 - 1;

    private static final Boolean DEFAULT_IS_PUBLISHED = false;
    private static final Boolean UPDATED_IS_PUBLISHED = true;

    private static final Boolean DEFAULT_ALLOW_LATE_SUBMISSION = false;
    private static final Boolean UPDATED_ALLOW_LATE_SUBMISSION = true;

    private static final Integer DEFAULT_LATE_SUBMISSION_PENALTY = 1;
    private static final Integer UPDATED_LATE_SUBMISSION_PENALTY = 2;
    private static final Integer SMALLER_LATE_SUBMISSION_PENALTY = 1 - 1;

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;
    private static final Integer SMALLER_SORT_ORDER = 1 - 1;

    private static final String ENTITY_API_URL = "/api/assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssignmentMockMvc;

    private Assignment assignment;

    private Assignment insertedAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assignment createEntity() {
        return new Assignment()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .instructions(DEFAULT_INSTRUCTIONS)
            .dueDate(DEFAULT_DUE_DATE)
            .maxPoints(DEFAULT_MAX_POINTS)
            .submissionType(DEFAULT_SUBMISSION_TYPE)
            .allowedFileTypes(DEFAULT_ALLOWED_FILE_TYPES)
            .maxFileSize(DEFAULT_MAX_FILE_SIZE)
            .isPublished(DEFAULT_IS_PUBLISHED)
            .allowLateSubmission(DEFAULT_ALLOW_LATE_SUBMISSION)
            .lateSubmissionPenalty(DEFAULT_LATE_SUBMISSION_PENALTY)
            .sortOrder(DEFAULT_SORT_ORDER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assignment createUpdatedEntity() {
        return new Assignment()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .instructions(UPDATED_INSTRUCTIONS)
            .dueDate(UPDATED_DUE_DATE)
            .maxPoints(UPDATED_MAX_POINTS)
            .submissionType(UPDATED_SUBMISSION_TYPE)
            .allowedFileTypes(UPDATED_ALLOWED_FILE_TYPES)
            .maxFileSize(UPDATED_MAX_FILE_SIZE)
            .isPublished(UPDATED_IS_PUBLISHED)
            .allowLateSubmission(UPDATED_ALLOW_LATE_SUBMISSION)
            .lateSubmissionPenalty(UPDATED_LATE_SUBMISSION_PENALTY)
            .sortOrder(UPDATED_SORT_ORDER);
    }

    @BeforeEach
    void initTest() {
        assignment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAssignment != null) {
            assignmentRepository.delete(insertedAssignment);
            insertedAssignment = null;
        }
    }

    @Test
    @Transactional
    void createAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);
        var returnedAssignmentDTO = om.readValue(
            restAssignmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssignmentDTO.class
        );

        // Validate the Assignment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAssignment = assignmentMapper.toEntity(returnedAssignmentDTO);
        assertAssignmentUpdatableFieldsEquals(returnedAssignment, getPersistedAssignment(returnedAssignment));

        insertedAssignment = returnedAssignment;
    }

    @Test
    @Transactional
    void createAssignmentWithExistingId() throws Exception {
        // Create the Assignment with an existing ID
        assignment.setId(1L);
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assignment.setTitle(null);

        // Create the Assignment, which fails.
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        restAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAssignments() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].instructions").value(hasItem(DEFAULT_INSTRUCTIONS)))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].maxPoints").value(hasItem(DEFAULT_MAX_POINTS)))
            .andExpect(jsonPath("$.[*].submissionType").value(hasItem(DEFAULT_SUBMISSION_TYPE)))
            .andExpect(jsonPath("$.[*].allowedFileTypes").value(hasItem(DEFAULT_ALLOWED_FILE_TYPES)))
            .andExpect(jsonPath("$.[*].maxFileSize").value(hasItem(DEFAULT_MAX_FILE_SIZE)))
            .andExpect(jsonPath("$.[*].isPublished").value(hasItem(DEFAULT_IS_PUBLISHED)))
            .andExpect(jsonPath("$.[*].allowLateSubmission").value(hasItem(DEFAULT_ALLOW_LATE_SUBMISSION)))
            .andExpect(jsonPath("$.[*].lateSubmissionPenalty").value(hasItem(DEFAULT_LATE_SUBMISSION_PENALTY)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)));
    }

    @Test
    @Transactional
    void getAssignment() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get the assignment
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL_ID, assignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assignment.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.instructions").value(DEFAULT_INSTRUCTIONS))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.maxPoints").value(DEFAULT_MAX_POINTS))
            .andExpect(jsonPath("$.submissionType").value(DEFAULT_SUBMISSION_TYPE))
            .andExpect(jsonPath("$.allowedFileTypes").value(DEFAULT_ALLOWED_FILE_TYPES))
            .andExpect(jsonPath("$.maxFileSize").value(DEFAULT_MAX_FILE_SIZE))
            .andExpect(jsonPath("$.isPublished").value(DEFAULT_IS_PUBLISHED))
            .andExpect(jsonPath("$.allowLateSubmission").value(DEFAULT_ALLOW_LATE_SUBMISSION))
            .andExpect(jsonPath("$.lateSubmissionPenalty").value(DEFAULT_LATE_SUBMISSION_PENALTY))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER));
    }

    @Test
    @Transactional
    void getAssignmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        Long id = assignment.getId();

        defaultAssignmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAssignmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAssignmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAssignmentsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where title equals to
        defaultAssignmentFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where title in
        defaultAssignmentFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where title is not null
        defaultAssignmentFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where title contains
        defaultAssignmentFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where title does not contain
        defaultAssignmentFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByDueDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where dueDate equals to
        defaultAssignmentFiltering("dueDate.equals=" + DEFAULT_DUE_DATE, "dueDate.equals=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByDueDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where dueDate in
        defaultAssignmentFiltering("dueDate.in=" + DEFAULT_DUE_DATE + "," + UPDATED_DUE_DATE, "dueDate.in=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByDueDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where dueDate is not null
        defaultAssignmentFiltering("dueDate.specified=true", "dueDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxPoints equals to
        defaultAssignmentFiltering("maxPoints.equals=" + DEFAULT_MAX_POINTS, "maxPoints.equals=" + UPDATED_MAX_POINTS);
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxPointsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxPoints in
        defaultAssignmentFiltering("maxPoints.in=" + DEFAULT_MAX_POINTS + "," + UPDATED_MAX_POINTS, "maxPoints.in=" + UPDATED_MAX_POINTS);
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxPoints is not null
        defaultAssignmentFiltering("maxPoints.specified=true", "maxPoints.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxPointsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxPoints is greater than or equal to
        defaultAssignmentFiltering(
            "maxPoints.greaterThanOrEqual=" + DEFAULT_MAX_POINTS,
            "maxPoints.greaterThanOrEqual=" + UPDATED_MAX_POINTS
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxPointsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxPoints is less than or equal to
        defaultAssignmentFiltering("maxPoints.lessThanOrEqual=" + DEFAULT_MAX_POINTS, "maxPoints.lessThanOrEqual=" + SMALLER_MAX_POINTS);
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxPointsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxPoints is less than
        defaultAssignmentFiltering("maxPoints.lessThan=" + UPDATED_MAX_POINTS, "maxPoints.lessThan=" + DEFAULT_MAX_POINTS);
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxPointsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxPoints is greater than
        defaultAssignmentFiltering("maxPoints.greaterThan=" + SMALLER_MAX_POINTS, "maxPoints.greaterThan=" + DEFAULT_MAX_POINTS);
    }

    @Test
    @Transactional
    void getAllAssignmentsBySubmissionTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where submissionType equals to
        defaultAssignmentFiltering("submissionType.equals=" + DEFAULT_SUBMISSION_TYPE, "submissionType.equals=" + UPDATED_SUBMISSION_TYPE);
    }

    @Test
    @Transactional
    void getAllAssignmentsBySubmissionTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where submissionType in
        defaultAssignmentFiltering(
            "submissionType.in=" + DEFAULT_SUBMISSION_TYPE + "," + UPDATED_SUBMISSION_TYPE,
            "submissionType.in=" + UPDATED_SUBMISSION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsBySubmissionTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where submissionType is not null
        defaultAssignmentFiltering("submissionType.specified=true", "submissionType.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsBySubmissionTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where submissionType contains
        defaultAssignmentFiltering(
            "submissionType.contains=" + DEFAULT_SUBMISSION_TYPE,
            "submissionType.contains=" + UPDATED_SUBMISSION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsBySubmissionTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where submissionType does not contain
        defaultAssignmentFiltering(
            "submissionType.doesNotContain=" + UPDATED_SUBMISSION_TYPE,
            "submissionType.doesNotContain=" + DEFAULT_SUBMISSION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxFileSizeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxFileSize equals to
        defaultAssignmentFiltering("maxFileSize.equals=" + DEFAULT_MAX_FILE_SIZE, "maxFileSize.equals=" + UPDATED_MAX_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxFileSizeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxFileSize in
        defaultAssignmentFiltering(
            "maxFileSize.in=" + DEFAULT_MAX_FILE_SIZE + "," + UPDATED_MAX_FILE_SIZE,
            "maxFileSize.in=" + UPDATED_MAX_FILE_SIZE
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxFileSizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxFileSize is not null
        defaultAssignmentFiltering("maxFileSize.specified=true", "maxFileSize.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxFileSizeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxFileSize is greater than or equal to
        defaultAssignmentFiltering(
            "maxFileSize.greaterThanOrEqual=" + DEFAULT_MAX_FILE_SIZE,
            "maxFileSize.greaterThanOrEqual=" + UPDATED_MAX_FILE_SIZE
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxFileSizeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxFileSize is less than or equal to
        defaultAssignmentFiltering(
            "maxFileSize.lessThanOrEqual=" + DEFAULT_MAX_FILE_SIZE,
            "maxFileSize.lessThanOrEqual=" + SMALLER_MAX_FILE_SIZE
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxFileSizeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxFileSize is less than
        defaultAssignmentFiltering("maxFileSize.lessThan=" + UPDATED_MAX_FILE_SIZE, "maxFileSize.lessThan=" + DEFAULT_MAX_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByMaxFileSizeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where maxFileSize is greater than
        defaultAssignmentFiltering("maxFileSize.greaterThan=" + SMALLER_MAX_FILE_SIZE, "maxFileSize.greaterThan=" + DEFAULT_MAX_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllAssignmentsByIsPublishedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where isPublished equals to
        defaultAssignmentFiltering("isPublished.equals=" + DEFAULT_IS_PUBLISHED, "isPublished.equals=" + UPDATED_IS_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllAssignmentsByIsPublishedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where isPublished in
        defaultAssignmentFiltering(
            "isPublished.in=" + DEFAULT_IS_PUBLISHED + "," + UPDATED_IS_PUBLISHED,
            "isPublished.in=" + UPDATED_IS_PUBLISHED
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByIsPublishedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where isPublished is not null
        defaultAssignmentFiltering("isPublished.specified=true", "isPublished.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByAllowLateSubmissionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where allowLateSubmission equals to
        defaultAssignmentFiltering(
            "allowLateSubmission.equals=" + DEFAULT_ALLOW_LATE_SUBMISSION,
            "allowLateSubmission.equals=" + UPDATED_ALLOW_LATE_SUBMISSION
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByAllowLateSubmissionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where allowLateSubmission in
        defaultAssignmentFiltering(
            "allowLateSubmission.in=" + DEFAULT_ALLOW_LATE_SUBMISSION + "," + UPDATED_ALLOW_LATE_SUBMISSION,
            "allowLateSubmission.in=" + UPDATED_ALLOW_LATE_SUBMISSION
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByAllowLateSubmissionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where allowLateSubmission is not null
        defaultAssignmentFiltering("allowLateSubmission.specified=true", "allowLateSubmission.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByLateSubmissionPenaltyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where lateSubmissionPenalty equals to
        defaultAssignmentFiltering(
            "lateSubmissionPenalty.equals=" + DEFAULT_LATE_SUBMISSION_PENALTY,
            "lateSubmissionPenalty.equals=" + UPDATED_LATE_SUBMISSION_PENALTY
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByLateSubmissionPenaltyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where lateSubmissionPenalty in
        defaultAssignmentFiltering(
            "lateSubmissionPenalty.in=" + DEFAULT_LATE_SUBMISSION_PENALTY + "," + UPDATED_LATE_SUBMISSION_PENALTY,
            "lateSubmissionPenalty.in=" + UPDATED_LATE_SUBMISSION_PENALTY
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByLateSubmissionPenaltyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where lateSubmissionPenalty is not null
        defaultAssignmentFiltering("lateSubmissionPenalty.specified=true", "lateSubmissionPenalty.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsByLateSubmissionPenaltyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where lateSubmissionPenalty is greater than or equal to
        defaultAssignmentFiltering(
            "lateSubmissionPenalty.greaterThanOrEqual=" + DEFAULT_LATE_SUBMISSION_PENALTY,
            "lateSubmissionPenalty.greaterThanOrEqual=" + UPDATED_LATE_SUBMISSION_PENALTY
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByLateSubmissionPenaltyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where lateSubmissionPenalty is less than or equal to
        defaultAssignmentFiltering(
            "lateSubmissionPenalty.lessThanOrEqual=" + DEFAULT_LATE_SUBMISSION_PENALTY,
            "lateSubmissionPenalty.lessThanOrEqual=" + SMALLER_LATE_SUBMISSION_PENALTY
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByLateSubmissionPenaltyIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where lateSubmissionPenalty is less than
        defaultAssignmentFiltering(
            "lateSubmissionPenalty.lessThan=" + UPDATED_LATE_SUBMISSION_PENALTY,
            "lateSubmissionPenalty.lessThan=" + DEFAULT_LATE_SUBMISSION_PENALTY
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsByLateSubmissionPenaltyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where lateSubmissionPenalty is greater than
        defaultAssignmentFiltering(
            "lateSubmissionPenalty.greaterThan=" + SMALLER_LATE_SUBMISSION_PENALTY,
            "lateSubmissionPenalty.greaterThan=" + DEFAULT_LATE_SUBMISSION_PENALTY
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsBySortOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where sortOrder equals to
        defaultAssignmentFiltering("sortOrder.equals=" + DEFAULT_SORT_ORDER, "sortOrder.equals=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllAssignmentsBySortOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where sortOrder in
        defaultAssignmentFiltering("sortOrder.in=" + DEFAULT_SORT_ORDER + "," + UPDATED_SORT_ORDER, "sortOrder.in=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllAssignmentsBySortOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where sortOrder is not null
        defaultAssignmentFiltering("sortOrder.specified=true", "sortOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllAssignmentsBySortOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where sortOrder is greater than or equal to
        defaultAssignmentFiltering(
            "sortOrder.greaterThanOrEqual=" + DEFAULT_SORT_ORDER,
            "sortOrder.greaterThanOrEqual=" + UPDATED_SORT_ORDER
        );
    }

    @Test
    @Transactional
    void getAllAssignmentsBySortOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where sortOrder is less than or equal to
        defaultAssignmentFiltering("sortOrder.lessThanOrEqual=" + DEFAULT_SORT_ORDER, "sortOrder.lessThanOrEqual=" + SMALLER_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllAssignmentsBySortOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where sortOrder is less than
        defaultAssignmentFiltering("sortOrder.lessThan=" + UPDATED_SORT_ORDER, "sortOrder.lessThan=" + DEFAULT_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllAssignmentsBySortOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where sortOrder is greater than
        defaultAssignmentFiltering("sortOrder.greaterThan=" + SMALLER_SORT_ORDER, "sortOrder.greaterThan=" + DEFAULT_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllAssignmentsByCourseIsEqualToSomething() throws Exception {
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            assignmentRepository.saveAndFlush(assignment);
            course = CourseResourceIT.createEntity();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        em.persist(course);
        em.flush();
        assignment.setCourse(course);
        assignmentRepository.saveAndFlush(assignment);
        Long courseId = course.getId();
        // Get all the assignmentList where course equals to courseId
        defaultAssignmentShouldBeFound("courseId.equals=" + courseId);

        // Get all the assignmentList where course equals to (courseId + 1)
        defaultAssignmentShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    @Test
    @Transactional
    void getAllAssignmentsByLessonIsEqualToSomething() throws Exception {
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            assignmentRepository.saveAndFlush(assignment);
            lesson = LessonResourceIT.createEntity();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).get(0);
        }
        em.persist(lesson);
        em.flush();
        assignment.setLesson(lesson);
        assignmentRepository.saveAndFlush(assignment);
        Long lessonId = lesson.getId();
        // Get all the assignmentList where lesson equals to lessonId
        defaultAssignmentShouldBeFound("lessonId.equals=" + lessonId);

        // Get all the assignmentList where lesson equals to (lessonId + 1)
        defaultAssignmentShouldNotBeFound("lessonId.equals=" + (lessonId + 1));
    }

    private void defaultAssignmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAssignmentShouldBeFound(shouldBeFound);
        defaultAssignmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAssignmentShouldBeFound(String filter) throws Exception {
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].instructions").value(hasItem(DEFAULT_INSTRUCTIONS)))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].maxPoints").value(hasItem(DEFAULT_MAX_POINTS)))
            .andExpect(jsonPath("$.[*].submissionType").value(hasItem(DEFAULT_SUBMISSION_TYPE)))
            .andExpect(jsonPath("$.[*].allowedFileTypes").value(hasItem(DEFAULT_ALLOWED_FILE_TYPES)))
            .andExpect(jsonPath("$.[*].maxFileSize").value(hasItem(DEFAULT_MAX_FILE_SIZE)))
            .andExpect(jsonPath("$.[*].isPublished").value(hasItem(DEFAULT_IS_PUBLISHED)))
            .andExpect(jsonPath("$.[*].allowLateSubmission").value(hasItem(DEFAULT_ALLOW_LATE_SUBMISSION)))
            .andExpect(jsonPath("$.[*].lateSubmissionPenalty").value(hasItem(DEFAULT_LATE_SUBMISSION_PENALTY)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)));

        // Check, that the count call also returns 1
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAssignmentShouldNotBeFound(String filter) throws Exception {
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAssignment() throws Exception {
        // Get the assignment
        restAssignmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssignment() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignment
        Assignment updatedAssignment = assignmentRepository.findById(assignment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssignment are not directly saved in db
        em.detach(updatedAssignment);
        updatedAssignment
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .instructions(UPDATED_INSTRUCTIONS)
            .dueDate(UPDATED_DUE_DATE)
            .maxPoints(UPDATED_MAX_POINTS)
            .submissionType(UPDATED_SUBMISSION_TYPE)
            .allowedFileTypes(UPDATED_ALLOWED_FILE_TYPES)
            .maxFileSize(UPDATED_MAX_FILE_SIZE)
            .isPublished(UPDATED_IS_PUBLISHED)
            .allowLateSubmission(UPDATED_ALLOW_LATE_SUBMISSION)
            .lateSubmissionPenalty(UPDATED_LATE_SUBMISSION_PENALTY)
            .sortOrder(UPDATED_SORT_ORDER);
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(updatedAssignment);

        restAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssignmentToMatchAllProperties(updatedAssignment);
    }

    @Test
    @Transactional
    void putNonExistingAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assignmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignment using partial update
        Assignment partialUpdatedAssignment = new Assignment();
        partialUpdatedAssignment.setId(assignment.getId());

        partialUpdatedAssignment
            .description(UPDATED_DESCRIPTION)
            .allowLateSubmission(UPDATED_ALLOW_LATE_SUBMISSION)
            .lateSubmissionPenalty(UPDATED_LATE_SUBMISSION_PENALTY);

        restAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignment))
            )
            .andExpect(status().isOk());

        // Validate the Assignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssignment, assignment),
            getPersistedAssignment(assignment)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assignment using partial update
        Assignment partialUpdatedAssignment = new Assignment();
        partialUpdatedAssignment.setId(assignment.getId());

        partialUpdatedAssignment
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .instructions(UPDATED_INSTRUCTIONS)
            .dueDate(UPDATED_DUE_DATE)
            .maxPoints(UPDATED_MAX_POINTS)
            .submissionType(UPDATED_SUBMISSION_TYPE)
            .allowedFileTypes(UPDATED_ALLOWED_FILE_TYPES)
            .maxFileSize(UPDATED_MAX_FILE_SIZE)
            .isPublished(UPDATED_IS_PUBLISHED)
            .allowLateSubmission(UPDATED_ALLOW_LATE_SUBMISSION)
            .lateSubmissionPenalty(UPDATED_LATE_SUBMISSION_PENALTY)
            .sortOrder(UPDATED_SORT_ORDER);

        restAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssignment))
            )
            .andExpect(status().isOk());

        // Validate the Assignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssignmentUpdatableFieldsEquals(partialUpdatedAssignment, getPersistedAssignment(partialUpdatedAssignment));
    }

    @Test
    @Transactional
    void patchNonExistingAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assignmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assignment.setId(longCount.incrementAndGet());

        // Create the Assignment
        AssignmentDTO assignmentDTO = assignmentMapper.toDto(assignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assignmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssignment() throws Exception {
        // Initialize the database
        insertedAssignment = assignmentRepository.saveAndFlush(assignment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assignment
        restAssignmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, assignment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assignmentRepository.count();
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

    protected Assignment getPersistedAssignment(Assignment assignment) {
        return assignmentRepository.findById(assignment.getId()).orElseThrow();
    }

    protected void assertPersistedAssignmentToMatchAllProperties(Assignment expectedAssignment) {
        assertAssignmentAllPropertiesEquals(expectedAssignment, getPersistedAssignment(expectedAssignment));
    }

    protected void assertPersistedAssignmentToMatchUpdatableProperties(Assignment expectedAssignment) {
        assertAssignmentAllUpdatablePropertiesEquals(expectedAssignment, getPersistedAssignment(expectedAssignment));
    }
}

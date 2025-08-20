package com.edupress.web.rest;

import static com.edupress.domain.QuizAttemptAsserts.*;
import static com.edupress.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edupress.IntegrationTest;
import com.edupress.domain.AppUser;
import com.edupress.domain.Quiz;
import com.edupress.domain.QuizAttempt;
import com.edupress.domain.enumeration.AttemptStatus;
import com.edupress.repository.QuizAttemptRepository;
import com.edupress.service.QuizAttemptService;
import com.edupress.service.dto.QuizAttemptDTO;
import com.edupress.service.mapper.QuizAttemptMapper;
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
 * Integration tests for the {@link QuizAttemptResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class QuizAttemptResourceIT {

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SUBMITTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBMITTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_SCORE = 1;
    private static final Integer UPDATED_SCORE = 2;
    private static final Integer SMALLER_SCORE = 1 - 1;

    private static final Boolean DEFAULT_PASSED = false;
    private static final Boolean UPDATED_PASSED = true;

    private static final String DEFAULT_ANSWERS = "AAAAAAAAAA";
    private static final String UPDATED_ANSWERS = "BBBBBBBBBB";

    private static final Integer DEFAULT_ATTEMPT_NUMBER = 1;
    private static final Integer UPDATED_ATTEMPT_NUMBER = 2;
    private static final Integer SMALLER_ATTEMPT_NUMBER = 1 - 1;

    private static final AttemptStatus DEFAULT_STATUS = AttemptStatus.STARTED;
    private static final AttemptStatus UPDATED_STATUS = AttemptStatus.SUBMITTED;

    private static final String ENTITY_API_URL = "/api/quiz-attempts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepositoryMock;

    @Autowired
    private QuizAttemptMapper quizAttemptMapper;

    @Mock
    private QuizAttemptService quizAttemptServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuizAttemptMockMvc;

    private QuizAttempt quizAttempt;

    private QuizAttempt insertedQuizAttempt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuizAttempt createEntity() {
        return new QuizAttempt()
            .startedAt(DEFAULT_STARTED_AT)
            .submittedAt(DEFAULT_SUBMITTED_AT)
            .score(DEFAULT_SCORE)
            .passed(DEFAULT_PASSED)
            .answers(DEFAULT_ANSWERS)
            .attemptNumber(DEFAULT_ATTEMPT_NUMBER)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuizAttempt createUpdatedEntity() {
        return new QuizAttempt()
            .startedAt(UPDATED_STARTED_AT)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .score(UPDATED_SCORE)
            .passed(UPDATED_PASSED)
            .answers(UPDATED_ANSWERS)
            .attemptNumber(UPDATED_ATTEMPT_NUMBER)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        quizAttempt = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedQuizAttempt != null) {
            quizAttemptRepository.delete(insertedQuizAttempt);
            insertedQuizAttempt = null;
        }
    }

    @Test
    @Transactional
    void createQuizAttempt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the QuizAttempt
        QuizAttemptDTO quizAttemptDTO = quizAttemptMapper.toDto(quizAttempt);
        var returnedQuizAttemptDTO = om.readValue(
            restQuizAttemptMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizAttemptDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuizAttemptDTO.class
        );

        // Validate the QuizAttempt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuizAttempt = quizAttemptMapper.toEntity(returnedQuizAttemptDTO);
        assertQuizAttemptUpdatableFieldsEquals(returnedQuizAttempt, getPersistedQuizAttempt(returnedQuizAttempt));

        insertedQuizAttempt = returnedQuizAttempt;
    }

    @Test
    @Transactional
    void createQuizAttemptWithExistingId() throws Exception {
        // Create the QuizAttempt with an existing ID
        quizAttempt.setId(1L);
        QuizAttemptDTO quizAttemptDTO = quizAttemptMapper.toDto(quizAttempt);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuizAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizAttemptDTO)))
            .andExpect(status().isBadRequest());

        // Validate the QuizAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllQuizAttempts() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList
        restQuizAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizAttempt.getId().intValue())))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].passed").value(hasItem(DEFAULT_PASSED)))
            .andExpect(jsonPath("$.[*].answers").value(hasItem(DEFAULT_ANSWERS)))
            .andExpect(jsonPath("$.[*].attemptNumber").value(hasItem(DEFAULT_ATTEMPT_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuizAttemptsWithEagerRelationshipsIsEnabled() throws Exception {
        when(quizAttemptServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuizAttemptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(quizAttemptServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuizAttemptsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(quizAttemptServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuizAttemptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(quizAttemptRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getQuizAttempt() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get the quizAttempt
        restQuizAttemptMockMvc
            .perform(get(ENTITY_API_URL_ID, quizAttempt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quizAttempt.getId().intValue()))
            .andExpect(jsonPath("$.startedAt").value(DEFAULT_STARTED_AT.toString()))
            .andExpect(jsonPath("$.submittedAt").value(DEFAULT_SUBMITTED_AT.toString()))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE))
            .andExpect(jsonPath("$.passed").value(DEFAULT_PASSED))
            .andExpect(jsonPath("$.answers").value(DEFAULT_ANSWERS))
            .andExpect(jsonPath("$.attemptNumber").value(DEFAULT_ATTEMPT_NUMBER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getQuizAttemptsByIdFiltering() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        Long id = quizAttempt.getId();

        defaultQuizAttemptFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultQuizAttemptFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultQuizAttemptFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByStartedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where startedAt equals to
        defaultQuizAttemptFiltering("startedAt.equals=" + DEFAULT_STARTED_AT, "startedAt.equals=" + UPDATED_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByStartedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where startedAt in
        defaultQuizAttemptFiltering("startedAt.in=" + DEFAULT_STARTED_AT + "," + UPDATED_STARTED_AT, "startedAt.in=" + UPDATED_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByStartedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where startedAt is not null
        defaultQuizAttemptFiltering("startedAt.specified=true", "startedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizAttemptsBySubmittedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where submittedAt equals to
        defaultQuizAttemptFiltering("submittedAt.equals=" + DEFAULT_SUBMITTED_AT, "submittedAt.equals=" + UPDATED_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsBySubmittedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where submittedAt in
        defaultQuizAttemptFiltering(
            "submittedAt.in=" + DEFAULT_SUBMITTED_AT + "," + UPDATED_SUBMITTED_AT,
            "submittedAt.in=" + UPDATED_SUBMITTED_AT
        );
    }

    @Test
    @Transactional
    void getAllQuizAttemptsBySubmittedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where submittedAt is not null
        defaultQuizAttemptFiltering("submittedAt.specified=true", "submittedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where score equals to
        defaultQuizAttemptFiltering("score.equals=" + DEFAULT_SCORE, "score.equals=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where score in
        defaultQuizAttemptFiltering("score.in=" + DEFAULT_SCORE + "," + UPDATED_SCORE, "score.in=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where score is not null
        defaultQuizAttemptFiltering("score.specified=true", "score.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where score is greater than or equal to
        defaultQuizAttemptFiltering("score.greaterThanOrEqual=" + DEFAULT_SCORE, "score.greaterThanOrEqual=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where score is less than or equal to
        defaultQuizAttemptFiltering("score.lessThanOrEqual=" + DEFAULT_SCORE, "score.lessThanOrEqual=" + SMALLER_SCORE);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where score is less than
        defaultQuizAttemptFiltering("score.lessThan=" + UPDATED_SCORE, "score.lessThan=" + DEFAULT_SCORE);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where score is greater than
        defaultQuizAttemptFiltering("score.greaterThan=" + SMALLER_SCORE, "score.greaterThan=" + DEFAULT_SCORE);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByPassedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where passed equals to
        defaultQuizAttemptFiltering("passed.equals=" + DEFAULT_PASSED, "passed.equals=" + UPDATED_PASSED);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByPassedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where passed in
        defaultQuizAttemptFiltering("passed.in=" + DEFAULT_PASSED + "," + UPDATED_PASSED, "passed.in=" + UPDATED_PASSED);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByPassedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where passed is not null
        defaultQuizAttemptFiltering("passed.specified=true", "passed.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByAttemptNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where attemptNumber equals to
        defaultQuizAttemptFiltering("attemptNumber.equals=" + DEFAULT_ATTEMPT_NUMBER, "attemptNumber.equals=" + UPDATED_ATTEMPT_NUMBER);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByAttemptNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where attemptNumber in
        defaultQuizAttemptFiltering(
            "attemptNumber.in=" + DEFAULT_ATTEMPT_NUMBER + "," + UPDATED_ATTEMPT_NUMBER,
            "attemptNumber.in=" + UPDATED_ATTEMPT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByAttemptNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where attemptNumber is not null
        defaultQuizAttemptFiltering("attemptNumber.specified=true", "attemptNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByAttemptNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where attemptNumber is greater than or equal to
        defaultQuizAttemptFiltering(
            "attemptNumber.greaterThanOrEqual=" + DEFAULT_ATTEMPT_NUMBER,
            "attemptNumber.greaterThanOrEqual=" + UPDATED_ATTEMPT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByAttemptNumberIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where attemptNumber is less than or equal to
        defaultQuizAttemptFiltering(
            "attemptNumber.lessThanOrEqual=" + DEFAULT_ATTEMPT_NUMBER,
            "attemptNumber.lessThanOrEqual=" + SMALLER_ATTEMPT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByAttemptNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where attemptNumber is less than
        defaultQuizAttemptFiltering("attemptNumber.lessThan=" + UPDATED_ATTEMPT_NUMBER, "attemptNumber.lessThan=" + DEFAULT_ATTEMPT_NUMBER);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByAttemptNumberIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where attemptNumber is greater than
        defaultQuizAttemptFiltering(
            "attemptNumber.greaterThan=" + SMALLER_ATTEMPT_NUMBER,
            "attemptNumber.greaterThan=" + DEFAULT_ATTEMPT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where status equals to
        defaultQuizAttemptFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where status in
        defaultQuizAttemptFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        // Get all the quizAttemptList where status is not null
        defaultQuizAttemptFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByQuizIsEqualToSomething() throws Exception {
        Quiz quiz;
        if (TestUtil.findAll(em, Quiz.class).isEmpty()) {
            quizAttemptRepository.saveAndFlush(quizAttempt);
            quiz = QuizResourceIT.createEntity();
        } else {
            quiz = TestUtil.findAll(em, Quiz.class).get(0);
        }
        em.persist(quiz);
        em.flush();
        quizAttempt.setQuiz(quiz);
        quizAttemptRepository.saveAndFlush(quizAttempt);
        Long quizId = quiz.getId();
        // Get all the quizAttemptList where quiz equals to quizId
        defaultQuizAttemptShouldBeFound("quizId.equals=" + quizId);

        // Get all the quizAttemptList where quiz equals to (quizId + 1)
        defaultQuizAttemptShouldNotBeFound("quizId.equals=" + (quizId + 1));
    }

    @Test
    @Transactional
    void getAllQuizAttemptsByStudentIsEqualToSomething() throws Exception {
        AppUser student;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            quizAttemptRepository.saveAndFlush(quizAttempt);
            student = AppUserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(student);
        em.flush();
        quizAttempt.setStudent(student);
        quizAttemptRepository.saveAndFlush(quizAttempt);
        Long studentId = student.getId();
        // Get all the quizAttemptList where student equals to studentId
        defaultQuizAttemptShouldBeFound("studentId.equals=" + studentId);

        // Get all the quizAttemptList where student equals to (studentId + 1)
        defaultQuizAttemptShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    private void defaultQuizAttemptFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultQuizAttemptShouldBeFound(shouldBeFound);
        defaultQuizAttemptShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultQuizAttemptShouldBeFound(String filter) throws Exception {
        restQuizAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizAttempt.getId().intValue())))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].passed").value(hasItem(DEFAULT_PASSED)))
            .andExpect(jsonPath("$.[*].answers").value(hasItem(DEFAULT_ANSWERS)))
            .andExpect(jsonPath("$.[*].attemptNumber").value(hasItem(DEFAULT_ATTEMPT_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restQuizAttemptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultQuizAttemptShouldNotBeFound(String filter) throws Exception {
        restQuizAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restQuizAttemptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingQuizAttempt() throws Exception {
        // Get the quizAttempt
        restQuizAttemptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuizAttempt() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quizAttempt
        QuizAttempt updatedQuizAttempt = quizAttemptRepository.findById(quizAttempt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuizAttempt are not directly saved in db
        em.detach(updatedQuizAttempt);
        updatedQuizAttempt
            .startedAt(UPDATED_STARTED_AT)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .score(UPDATED_SCORE)
            .passed(UPDATED_PASSED)
            .answers(UPDATED_ANSWERS)
            .attemptNumber(UPDATED_ATTEMPT_NUMBER)
            .status(UPDATED_STATUS);
        QuizAttemptDTO quizAttemptDTO = quizAttemptMapper.toDto(updatedQuizAttempt);

        restQuizAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quizAttemptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quizAttemptDTO))
            )
            .andExpect(status().isOk());

        // Validate the QuizAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuizAttemptToMatchAllProperties(updatedQuizAttempt);
    }

    @Test
    @Transactional
    void putNonExistingQuizAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizAttempt.setId(longCount.incrementAndGet());

        // Create the QuizAttempt
        QuizAttemptDTO quizAttemptDTO = quizAttemptMapper.toDto(quizAttempt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quizAttemptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quizAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuizAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizAttempt.setId(longCount.incrementAndGet());

        // Create the QuizAttempt
        QuizAttemptDTO quizAttemptDTO = quizAttemptMapper.toDto(quizAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quizAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuizAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizAttempt.setId(longCount.incrementAndGet());

        // Create the QuizAttempt
        QuizAttemptDTO quizAttemptDTO = quizAttemptMapper.toDto(quizAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizAttemptMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizAttemptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuizAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuizAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quizAttempt using partial update
        QuizAttempt partialUpdatedQuizAttempt = new QuizAttempt();
        partialUpdatedQuizAttempt.setId(quizAttempt.getId());

        partialUpdatedQuizAttempt
            .startedAt(UPDATED_STARTED_AT)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .score(UPDATED_SCORE)
            .passed(UPDATED_PASSED)
            .attemptNumber(UPDATED_ATTEMPT_NUMBER)
            .status(UPDATED_STATUS);

        restQuizAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuizAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuizAttempt))
            )
            .andExpect(status().isOk());

        // Validate the QuizAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuizAttemptUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedQuizAttempt, quizAttempt),
            getPersistedQuizAttempt(quizAttempt)
        );
    }

    @Test
    @Transactional
    void fullUpdateQuizAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quizAttempt using partial update
        QuizAttempt partialUpdatedQuizAttempt = new QuizAttempt();
        partialUpdatedQuizAttempt.setId(quizAttempt.getId());

        partialUpdatedQuizAttempt
            .startedAt(UPDATED_STARTED_AT)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .score(UPDATED_SCORE)
            .passed(UPDATED_PASSED)
            .answers(UPDATED_ANSWERS)
            .attemptNumber(UPDATED_ATTEMPT_NUMBER)
            .status(UPDATED_STATUS);

        restQuizAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuizAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuizAttempt))
            )
            .andExpect(status().isOk());

        // Validate the QuizAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuizAttemptUpdatableFieldsEquals(partialUpdatedQuizAttempt, getPersistedQuizAttempt(partialUpdatedQuizAttempt));
    }

    @Test
    @Transactional
    void patchNonExistingQuizAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizAttempt.setId(longCount.incrementAndGet());

        // Create the QuizAttempt
        QuizAttemptDTO quizAttemptDTO = quizAttemptMapper.toDto(quizAttempt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, quizAttemptDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quizAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuizAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizAttempt.setId(longCount.incrementAndGet());

        // Create the QuizAttempt
        QuizAttemptDTO quizAttemptDTO = quizAttemptMapper.toDto(quizAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quizAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuizAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizAttempt.setId(longCount.incrementAndGet());

        // Create the QuizAttempt
        QuizAttemptDTO quizAttemptDTO = quizAttemptMapper.toDto(quizAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizAttemptMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(quizAttemptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuizAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuizAttempt() throws Exception {
        // Initialize the database
        insertedQuizAttempt = quizAttemptRepository.saveAndFlush(quizAttempt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the quizAttempt
        restQuizAttemptMockMvc
            .perform(delete(ENTITY_API_URL_ID, quizAttempt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return quizAttemptRepository.count();
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

    protected QuizAttempt getPersistedQuizAttempt(QuizAttempt quizAttempt) {
        return quizAttemptRepository.findById(quizAttempt.getId()).orElseThrow();
    }

    protected void assertPersistedQuizAttemptToMatchAllProperties(QuizAttempt expectedQuizAttempt) {
        assertQuizAttemptAllPropertiesEquals(expectedQuizAttempt, getPersistedQuizAttempt(expectedQuizAttempt));
    }

    protected void assertPersistedQuizAttemptToMatchUpdatableProperties(QuizAttempt expectedQuizAttempt) {
        assertQuizAttemptAllUpdatablePropertiesEquals(expectedQuizAttempt, getPersistedQuizAttempt(expectedQuizAttempt));
    }
}

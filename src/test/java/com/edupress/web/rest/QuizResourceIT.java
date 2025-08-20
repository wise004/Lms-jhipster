package com.edupress.web.rest;

import static com.edupress.domain.QuizAsserts.*;
import static com.edupress.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edupress.IntegrationTest;
import com.edupress.domain.Course;
import com.edupress.domain.Lesson;
import com.edupress.domain.Quiz;
import com.edupress.repository.QuizRepository;
import com.edupress.security.AuthoritiesConstants;
import com.edupress.service.dto.QuizDTO;
import com.edupress.service.mapper.QuizMapper;
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
 * Integration tests for the {@link QuizResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class QuizResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_TIME_LIMIT = 1;
    private static final Integer UPDATED_TIME_LIMIT = 2;
    private static final Integer SMALLER_TIME_LIMIT = 1 - 1;

    private static final Integer DEFAULT_PASSING_SCORE = 1;
    private static final Integer UPDATED_PASSING_SCORE = 2;
    private static final Integer SMALLER_PASSING_SCORE = 1 - 1;

    private static final Integer DEFAULT_ATTEMPTS_ALLOWED = 1;
    private static final Integer UPDATED_ATTEMPTS_ALLOWED = 2;
    private static final Integer SMALLER_ATTEMPTS_ALLOWED = 1 - 1;

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;
    private static final Integer SMALLER_SORT_ORDER = 1 - 1;

    private static final String DEFAULT_QUESTIONS = "AAAAAAAAAA";
    private static final String UPDATED_QUESTIONS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_PUBLISHED = false;
    private static final Boolean UPDATED_IS_PUBLISHED = true;

    private static final Instant DEFAULT_AVAILABLE_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AVAILABLE_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_AVAILABLE_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AVAILABLE_UNTIL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/quizzes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizMapper quizMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuizMockMvc;

    private Quiz quiz;

    private Quiz insertedQuiz;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quiz createEntity() {
        return new Quiz()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .timeLimit(DEFAULT_TIME_LIMIT)
            .passingScore(DEFAULT_PASSING_SCORE)
            .attemptsAllowed(DEFAULT_ATTEMPTS_ALLOWED)
            .sortOrder(DEFAULT_SORT_ORDER)
            .questions(DEFAULT_QUESTIONS)
            .isPublished(DEFAULT_IS_PUBLISHED)
            .availableFrom(DEFAULT_AVAILABLE_FROM)
            .availableUntil(DEFAULT_AVAILABLE_UNTIL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quiz createUpdatedEntity() {
        return new Quiz()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .timeLimit(UPDATED_TIME_LIMIT)
            .passingScore(UPDATED_PASSING_SCORE)
            .attemptsAllowed(UPDATED_ATTEMPTS_ALLOWED)
            .sortOrder(UPDATED_SORT_ORDER)
            .questions(UPDATED_QUESTIONS)
            .isPublished(UPDATED_IS_PUBLISHED)
            .availableFrom(UPDATED_AVAILABLE_FROM)
            .availableUntil(UPDATED_AVAILABLE_UNTIL);
    }

    @BeforeEach
    void initTest() {
        quiz = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedQuiz != null) {
            quizRepository.delete(insertedQuiz);
            insertedQuiz = null;
        }
    }

    @Test
    @Transactional
    void createQuiz() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Quiz
        QuizDTO quizDTO = quizMapper.toDto(quiz);
        var returnedQuizDTO = om.readValue(
            restQuizMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuizDTO.class
        );

        // Validate the Quiz in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuiz = quizMapper.toEntity(returnedQuizDTO);
        assertQuizUpdatableFieldsEquals(returnedQuiz, getPersistedQuiz(returnedQuiz));

        insertedQuiz = returnedQuiz;
    }

    @Test
    @Transactional
    void createQuizWithExistingId() throws Exception {
        // Create the Quiz with an existing ID
        quiz.setId(1L);
        QuizDTO quizDTO = quizMapper.toDto(quiz);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuizMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quiz.setTitle(null);

        // Create the Quiz, which fails.
        QuizDTO quizDTO = quizMapper.toDto(quiz);

        restQuizMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllQuizzes() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList
        restQuizMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quiz.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].timeLimit").value(hasItem(DEFAULT_TIME_LIMIT)))
            .andExpect(jsonPath("$.[*].passingScore").value(hasItem(DEFAULT_PASSING_SCORE)))
            .andExpect(jsonPath("$.[*].attemptsAllowed").value(hasItem(DEFAULT_ATTEMPTS_ALLOWED)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)))
            .andExpect(jsonPath("$.[*].questions").value(hasItem(DEFAULT_QUESTIONS)))
            .andExpect(jsonPath("$.[*].isPublished").value(hasItem(DEFAULT_IS_PUBLISHED)))
            .andExpect(jsonPath("$.[*].availableFrom").value(hasItem(DEFAULT_AVAILABLE_FROM.toString())))
            .andExpect(jsonPath("$.[*].availableUntil").value(hasItem(DEFAULT_AVAILABLE_UNTIL.toString())));
    }

    @Test
    @Transactional
    void getQuiz() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get the quiz
        restQuizMockMvc
            .perform(get(ENTITY_API_URL_ID, quiz.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quiz.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.timeLimit").value(DEFAULT_TIME_LIMIT))
            .andExpect(jsonPath("$.passingScore").value(DEFAULT_PASSING_SCORE))
            .andExpect(jsonPath("$.attemptsAllowed").value(DEFAULT_ATTEMPTS_ALLOWED))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER))
            .andExpect(jsonPath("$.questions").value(DEFAULT_QUESTIONS))
            .andExpect(jsonPath("$.isPublished").value(DEFAULT_IS_PUBLISHED))
            .andExpect(jsonPath("$.availableFrom").value(DEFAULT_AVAILABLE_FROM.toString()))
            .andExpect(jsonPath("$.availableUntil").value(DEFAULT_AVAILABLE_UNTIL.toString()));
    }

    @Test
    @Transactional
    void getQuizzesByIdFiltering() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        Long id = quiz.getId();

        defaultQuizFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultQuizFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultQuizFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllQuizzesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where title equals to
        defaultQuizFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllQuizzesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where title in
        defaultQuizFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllQuizzesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where title is not null
        defaultQuizFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizzesByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where title contains
        defaultQuizFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllQuizzesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where title does not contain
        defaultQuizFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllQuizzesByTimeLimitIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where timeLimit equals to
        defaultQuizFiltering("timeLimit.equals=" + DEFAULT_TIME_LIMIT, "timeLimit.equals=" + UPDATED_TIME_LIMIT);
    }

    @Test
    @Transactional
    void getAllQuizzesByTimeLimitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where timeLimit in
        defaultQuizFiltering("timeLimit.in=" + DEFAULT_TIME_LIMIT + "," + UPDATED_TIME_LIMIT, "timeLimit.in=" + UPDATED_TIME_LIMIT);
    }

    @Test
    @Transactional
    void getAllQuizzesByTimeLimitIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where timeLimit is not null
        defaultQuizFiltering("timeLimit.specified=true", "timeLimit.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizzesByTimeLimitIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where timeLimit is greater than or equal to
        defaultQuizFiltering("timeLimit.greaterThanOrEqual=" + DEFAULT_TIME_LIMIT, "timeLimit.greaterThanOrEqual=" + UPDATED_TIME_LIMIT);
    }

    @Test
    @Transactional
    void getAllQuizzesByTimeLimitIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where timeLimit is less than or equal to
        defaultQuizFiltering("timeLimit.lessThanOrEqual=" + DEFAULT_TIME_LIMIT, "timeLimit.lessThanOrEqual=" + SMALLER_TIME_LIMIT);
    }

    @Test
    @Transactional
    void getAllQuizzesByTimeLimitIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where timeLimit is less than
        defaultQuizFiltering("timeLimit.lessThan=" + UPDATED_TIME_LIMIT, "timeLimit.lessThan=" + DEFAULT_TIME_LIMIT);
    }

    @Test
    @Transactional
    void getAllQuizzesByTimeLimitIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where timeLimit is greater than
        defaultQuizFiltering("timeLimit.greaterThan=" + SMALLER_TIME_LIMIT, "timeLimit.greaterThan=" + DEFAULT_TIME_LIMIT);
    }

    @Test
    @Transactional
    void getAllQuizzesByPassingScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingScore equals to
        defaultQuizFiltering("passingScore.equals=" + DEFAULT_PASSING_SCORE, "passingScore.equals=" + UPDATED_PASSING_SCORE);
    }

    @Test
    @Transactional
    void getAllQuizzesByPassingScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingScore in
        defaultQuizFiltering(
            "passingScore.in=" + DEFAULT_PASSING_SCORE + "," + UPDATED_PASSING_SCORE,
            "passingScore.in=" + UPDATED_PASSING_SCORE
        );
    }

    @Test
    @Transactional
    void getAllQuizzesByPassingScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingScore is not null
        defaultQuizFiltering("passingScore.specified=true", "passingScore.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizzesByPassingScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingScore is greater than or equal to
        defaultQuizFiltering(
            "passingScore.greaterThanOrEqual=" + DEFAULT_PASSING_SCORE,
            "passingScore.greaterThanOrEqual=" + UPDATED_PASSING_SCORE
        );
    }

    @Test
    @Transactional
    void getAllQuizzesByPassingScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingScore is less than or equal to
        defaultQuizFiltering(
            "passingScore.lessThanOrEqual=" + DEFAULT_PASSING_SCORE,
            "passingScore.lessThanOrEqual=" + SMALLER_PASSING_SCORE
        );
    }

    @Test
    @Transactional
    void getAllQuizzesByPassingScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingScore is less than
        defaultQuizFiltering("passingScore.lessThan=" + UPDATED_PASSING_SCORE, "passingScore.lessThan=" + DEFAULT_PASSING_SCORE);
    }

    @Test
    @Transactional
    void getAllQuizzesByPassingScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingScore is greater than
        defaultQuizFiltering("passingScore.greaterThan=" + SMALLER_PASSING_SCORE, "passingScore.greaterThan=" + DEFAULT_PASSING_SCORE);
    }

    @Test
    @Transactional
    void getAllQuizzesByAttemptsAllowedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where attemptsAllowed equals to
        defaultQuizFiltering("attemptsAllowed.equals=" + DEFAULT_ATTEMPTS_ALLOWED, "attemptsAllowed.equals=" + UPDATED_ATTEMPTS_ALLOWED);
    }

    @Test
    @Transactional
    void getAllQuizzesByAttemptsAllowedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where attemptsAllowed in
        defaultQuizFiltering(
            "attemptsAllowed.in=" + DEFAULT_ATTEMPTS_ALLOWED + "," + UPDATED_ATTEMPTS_ALLOWED,
            "attemptsAllowed.in=" + UPDATED_ATTEMPTS_ALLOWED
        );
    }

    @Test
    @Transactional
    void getAllQuizzesByAttemptsAllowedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where attemptsAllowed is not null
        defaultQuizFiltering("attemptsAllowed.specified=true", "attemptsAllowed.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizzesByAttemptsAllowedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where attemptsAllowed is greater than or equal to
        defaultQuizFiltering(
            "attemptsAllowed.greaterThanOrEqual=" + DEFAULT_ATTEMPTS_ALLOWED,
            "attemptsAllowed.greaterThanOrEqual=" + UPDATED_ATTEMPTS_ALLOWED
        );
    }

    @Test
    @Transactional
    void getAllQuizzesByAttemptsAllowedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where attemptsAllowed is less than or equal to
        defaultQuizFiltering(
            "attemptsAllowed.lessThanOrEqual=" + DEFAULT_ATTEMPTS_ALLOWED,
            "attemptsAllowed.lessThanOrEqual=" + SMALLER_ATTEMPTS_ALLOWED
        );
    }

    @Test
    @Transactional
    void getAllQuizzesByAttemptsAllowedIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where attemptsAllowed is less than
        defaultQuizFiltering(
            "attemptsAllowed.lessThan=" + UPDATED_ATTEMPTS_ALLOWED,
            "attemptsAllowed.lessThan=" + DEFAULT_ATTEMPTS_ALLOWED
        );
    }

    @Test
    @Transactional
    void getAllQuizzesByAttemptsAllowedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where attemptsAllowed is greater than
        defaultQuizFiltering(
            "attemptsAllowed.greaterThan=" + SMALLER_ATTEMPTS_ALLOWED,
            "attemptsAllowed.greaterThan=" + DEFAULT_ATTEMPTS_ALLOWED
        );
    }

    @Test
    @Transactional
    void getAllQuizzesBySortOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where sortOrder equals to
        defaultQuizFiltering("sortOrder.equals=" + DEFAULT_SORT_ORDER, "sortOrder.equals=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllQuizzesBySortOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where sortOrder in
        defaultQuizFiltering("sortOrder.in=" + DEFAULT_SORT_ORDER + "," + UPDATED_SORT_ORDER, "sortOrder.in=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllQuizzesBySortOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where sortOrder is not null
        defaultQuizFiltering("sortOrder.specified=true", "sortOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizzesBySortOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where sortOrder is greater than or equal to
        defaultQuizFiltering("sortOrder.greaterThanOrEqual=" + DEFAULT_SORT_ORDER, "sortOrder.greaterThanOrEqual=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllQuizzesBySortOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where sortOrder is less than or equal to
        defaultQuizFiltering("sortOrder.lessThanOrEqual=" + DEFAULT_SORT_ORDER, "sortOrder.lessThanOrEqual=" + SMALLER_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllQuizzesBySortOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where sortOrder is less than
        defaultQuizFiltering("sortOrder.lessThan=" + UPDATED_SORT_ORDER, "sortOrder.lessThan=" + DEFAULT_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllQuizzesBySortOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where sortOrder is greater than
        defaultQuizFiltering("sortOrder.greaterThan=" + SMALLER_SORT_ORDER, "sortOrder.greaterThan=" + DEFAULT_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllQuizzesByIsPublishedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where isPublished equals to
        defaultQuizFiltering("isPublished.equals=" + DEFAULT_IS_PUBLISHED, "isPublished.equals=" + UPDATED_IS_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllQuizzesByIsPublishedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where isPublished in
        defaultQuizFiltering(
            "isPublished.in=" + DEFAULT_IS_PUBLISHED + "," + UPDATED_IS_PUBLISHED,
            "isPublished.in=" + UPDATED_IS_PUBLISHED
        );
    }

    @Test
    @Transactional
    void getAllQuizzesByIsPublishedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where isPublished is not null
        defaultQuizFiltering("isPublished.specified=true", "isPublished.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizzesByAvailableFromIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where availableFrom equals to
        defaultQuizFiltering("availableFrom.equals=" + DEFAULT_AVAILABLE_FROM, "availableFrom.equals=" + UPDATED_AVAILABLE_FROM);
    }

    @Test
    @Transactional
    void getAllQuizzesByAvailableFromIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where availableFrom in
        defaultQuizFiltering(
            "availableFrom.in=" + DEFAULT_AVAILABLE_FROM + "," + UPDATED_AVAILABLE_FROM,
            "availableFrom.in=" + UPDATED_AVAILABLE_FROM
        );
    }

    @Test
    @Transactional
    void getAllQuizzesByAvailableFromIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where availableFrom is not null
        defaultQuizFiltering("availableFrom.specified=true", "availableFrom.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizzesByAvailableUntilIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where availableUntil equals to
        defaultQuizFiltering("availableUntil.equals=" + DEFAULT_AVAILABLE_UNTIL, "availableUntil.equals=" + UPDATED_AVAILABLE_UNTIL);
    }

    @Test
    @Transactional
    void getAllQuizzesByAvailableUntilIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where availableUntil in
        defaultQuizFiltering(
            "availableUntil.in=" + DEFAULT_AVAILABLE_UNTIL + "," + UPDATED_AVAILABLE_UNTIL,
            "availableUntil.in=" + UPDATED_AVAILABLE_UNTIL
        );
    }

    @Test
    @Transactional
    void getAllQuizzesByAvailableUntilIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        // Get all the quizList where availableUntil is not null
        defaultQuizFiltering("availableUntil.specified=true", "availableUntil.specified=false");
    }

    @Test
    @Transactional
    void getAllQuizzesByCourseIsEqualToSomething() throws Exception {
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            quizRepository.saveAndFlush(quiz);
            course = CourseResourceIT.createEntity();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        em.persist(course);
        em.flush();
        quiz.setCourse(course);
        quizRepository.saveAndFlush(quiz);
        Long courseId = course.getId();
        // Get all the quizList where course equals to courseId
        defaultQuizShouldBeFound("courseId.equals=" + courseId);

        // Get all the quizList where course equals to (courseId + 1)
        defaultQuizShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    @Test
    @Transactional
    void getAllQuizzesByLessonIsEqualToSomething() throws Exception {
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            quizRepository.saveAndFlush(quiz);
            lesson = LessonResourceIT.createEntity();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).get(0);
        }
        em.persist(lesson);
        em.flush();
        quiz.setLesson(lesson);
        quizRepository.saveAndFlush(quiz);
        Long lessonId = lesson.getId();
        // Get all the quizList where lesson equals to lessonId
        defaultQuizShouldBeFound("lessonId.equals=" + lessonId);

        // Get all the quizList where lesson equals to (lessonId + 1)
        defaultQuizShouldNotBeFound("lessonId.equals=" + (lessonId + 1));
    }

    private void defaultQuizFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultQuizShouldBeFound(shouldBeFound);
        defaultQuizShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultQuizShouldBeFound(String filter) throws Exception {
        restQuizMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quiz.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].timeLimit").value(hasItem(DEFAULT_TIME_LIMIT)))
            .andExpect(jsonPath("$.[*].passingScore").value(hasItem(DEFAULT_PASSING_SCORE)))
            .andExpect(jsonPath("$.[*].attemptsAllowed").value(hasItem(DEFAULT_ATTEMPTS_ALLOWED)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)))
            .andExpect(jsonPath("$.[*].questions").value(hasItem(DEFAULT_QUESTIONS)))
            .andExpect(jsonPath("$.[*].isPublished").value(hasItem(DEFAULT_IS_PUBLISHED)))
            .andExpect(jsonPath("$.[*].availableFrom").value(hasItem(DEFAULT_AVAILABLE_FROM.toString())))
            .andExpect(jsonPath("$.[*].availableUntil").value(hasItem(DEFAULT_AVAILABLE_UNTIL.toString())));

        // Check, that the count call also returns 1
        restQuizMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultQuizShouldNotBeFound(String filter) throws Exception {
        restQuizMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restQuizMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingQuiz() throws Exception {
        // Get the quiz
        restQuizMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuiz() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quiz
        Quiz updatedQuiz = quizRepository.findById(quiz.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuiz are not directly saved in db
        em.detach(updatedQuiz);
        updatedQuiz
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .timeLimit(UPDATED_TIME_LIMIT)
            .passingScore(UPDATED_PASSING_SCORE)
            .attemptsAllowed(UPDATED_ATTEMPTS_ALLOWED)
            .sortOrder(UPDATED_SORT_ORDER)
            .questions(UPDATED_QUESTIONS)
            .isPublished(UPDATED_IS_PUBLISHED)
            .availableFrom(UPDATED_AVAILABLE_FROM)
            .availableUntil(UPDATED_AVAILABLE_UNTIL);
        QuizDTO quizDTO = quizMapper.toDto(updatedQuiz);

        restQuizMockMvc
            .perform(put(ENTITY_API_URL_ID, quizDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizDTO)))
            .andExpect(status().isOk());

        // Validate the Quiz in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuizToMatchAllProperties(updatedQuiz);
    }

    @Test
    @Transactional
    void putNonExistingQuiz() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quiz.setId(longCount.incrementAndGet());

        // Create the Quiz
        QuizDTO quizDTO = quizMapper.toDto(quiz);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(put(ENTITY_API_URL_ID, quizDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuiz() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quiz.setId(longCount.incrementAndGet());

        // Create the Quiz
        QuizDTO quizDTO = quizMapper.toDto(quiz);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quizDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuiz() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quiz.setId(longCount.incrementAndGet());

        // Create the Quiz
        QuizDTO quizDTO = quizMapper.toDto(quiz);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Quiz in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuizWithPatch() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quiz using partial update
        Quiz partialUpdatedQuiz = new Quiz();
        partialUpdatedQuiz.setId(quiz.getId());

        partialUpdatedQuiz
            .title(UPDATED_TITLE)
            .timeLimit(UPDATED_TIME_LIMIT)
            .attemptsAllowed(UPDATED_ATTEMPTS_ALLOWED)
            .sortOrder(UPDATED_SORT_ORDER)
            .isPublished(UPDATED_IS_PUBLISHED)
            .availableFrom(UPDATED_AVAILABLE_FROM);

        restQuizMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuiz.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuiz))
            )
            .andExpect(status().isOk());

        // Validate the Quiz in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuizUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedQuiz, quiz), getPersistedQuiz(quiz));
    }

    @Test
    @Transactional
    void fullUpdateQuizWithPatch() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quiz using partial update
        Quiz partialUpdatedQuiz = new Quiz();
        partialUpdatedQuiz.setId(quiz.getId());

        partialUpdatedQuiz
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .timeLimit(UPDATED_TIME_LIMIT)
            .passingScore(UPDATED_PASSING_SCORE)
            .attemptsAllowed(UPDATED_ATTEMPTS_ALLOWED)
            .sortOrder(UPDATED_SORT_ORDER)
            .questions(UPDATED_QUESTIONS)
            .isPublished(UPDATED_IS_PUBLISHED)
            .availableFrom(UPDATED_AVAILABLE_FROM)
            .availableUntil(UPDATED_AVAILABLE_UNTIL);

        restQuizMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuiz.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuiz))
            )
            .andExpect(status().isOk());

        // Validate the Quiz in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuizUpdatableFieldsEquals(partialUpdatedQuiz, getPersistedQuiz(partialUpdatedQuiz));
    }

    @Test
    @Transactional
    void patchNonExistingQuiz() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quiz.setId(longCount.incrementAndGet());

        // Create the Quiz
        QuizDTO quizDTO = quizMapper.toDto(quiz);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, quizDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(quizDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuiz() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quiz.setId(longCount.incrementAndGet());

        // Create the Quiz
        QuizDTO quizDTO = quizMapper.toDto(quiz);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quizDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuiz() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quiz.setId(longCount.incrementAndGet());

        // Create the Quiz
        QuizDTO quizDTO = quizMapper.toDto(quiz);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(quizDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Quiz in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuiz() throws Exception {
        // Initialize the database
        insertedQuiz = quizRepository.saveAndFlush(quiz);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the quiz
        restQuizMockMvc
            .perform(delete(ENTITY_API_URL_ID, quiz.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return quizRepository.count();
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

    protected Quiz getPersistedQuiz(Quiz quiz) {
        return quizRepository.findById(quiz.getId()).orElseThrow();
    }

    protected void assertPersistedQuizToMatchAllProperties(Quiz expectedQuiz) {
        assertQuizAllPropertiesEquals(expectedQuiz, getPersistedQuiz(expectedQuiz));
    }

    protected void assertPersistedQuizToMatchUpdatableProperties(Quiz expectedQuiz) {
        assertQuizAllUpdatablePropertiesEquals(expectedQuiz, getPersistedQuiz(expectedQuiz));
    }
}

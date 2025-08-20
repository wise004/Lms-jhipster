package com.edupress.web.rest;

import static com.edupress.domain.LessonAsserts.*;
import static com.edupress.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edupress.IntegrationTest;
import com.edupress.domain.Course;
import com.edupress.domain.Lesson;
import com.edupress.domain.enumeration.LessonType;
import com.edupress.repository.LessonRepository;
import com.edupress.security.AuthoritiesConstants;
import com.edupress.service.dto.LessonDTO;
import com.edupress.service.mapper.LessonMapper;
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
 * Integration tests for the {@link LessonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class LessonResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_VIDEO_URL = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURATION = 1;
    private static final Integer UPDATED_DURATION = 2;
    private static final Integer SMALLER_DURATION = 1 - 1;

    private static final LessonType DEFAULT_TYPE = LessonType.VIDEO;
    private static final LessonType UPDATED_TYPE = LessonType.ARTICLE;

    private static final Boolean DEFAULT_IS_FREE = false;
    private static final Boolean UPDATED_IS_FREE = true;

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;
    private static final Integer SMALLER_SORT_ORDER = 1 - 1;

    private static final Boolean DEFAULT_IS_PUBLISHED = false;
    private static final Boolean UPDATED_IS_PUBLISHED = true;

    private static final String ENTITY_API_URL = "/api/lessons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonMapper lessonMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLessonMockMvc;

    private Lesson lesson;

    private Lesson insertedLesson;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lesson createEntity() {
        return new Lesson()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .content(DEFAULT_CONTENT)
            .videoUrl(DEFAULT_VIDEO_URL)
            .duration(DEFAULT_DURATION)
            .type(DEFAULT_TYPE)
            .isFree(DEFAULT_IS_FREE)
            .sortOrder(DEFAULT_SORT_ORDER)
            .isPublished(DEFAULT_IS_PUBLISHED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lesson createUpdatedEntity() {
        return new Lesson()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .content(UPDATED_CONTENT)
            .videoUrl(UPDATED_VIDEO_URL)
            .duration(UPDATED_DURATION)
            .type(UPDATED_TYPE)
            .isFree(UPDATED_IS_FREE)
            .sortOrder(UPDATED_SORT_ORDER)
            .isPublished(UPDATED_IS_PUBLISHED);
    }

    @BeforeEach
    void initTest() {
        lesson = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLesson != null) {
            lessonRepository.delete(insertedLesson);
            insertedLesson = null;
        }
    }

    @Test
    @Transactional
    void createLesson() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);
        var returnedLessonDTO = om.readValue(
            restLessonMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LessonDTO.class
        );

        // Validate the Lesson in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLesson = lessonMapper.toEntity(returnedLessonDTO);
        assertLessonUpdatableFieldsEquals(returnedLesson, getPersistedLesson(returnedLesson));

        insertedLesson = returnedLesson;
    }

    @Test
    @Transactional
    void createLessonWithExistingId() throws Exception {
        // Create the Lesson with an existing ID
        lesson.setId(1L);
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLessonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lesson.setTitle(null);

        // Create the Lesson, which fails.
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        restLessonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lesson.setType(null);

        // Create the Lesson, which fails.
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        restLessonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLessons() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].videoUrl").value(hasItem(DEFAULT_VIDEO_URL)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].isFree").value(hasItem(DEFAULT_IS_FREE)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)))
            .andExpect(jsonPath("$.[*].isPublished").value(hasItem(DEFAULT_IS_PUBLISHED)));
    }

    @Test
    @Transactional
    void getLesson() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get the lesson
        restLessonMockMvc
            .perform(get(ENTITY_API_URL_ID, lesson.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lesson.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.videoUrl").value(DEFAULT_VIDEO_URL))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.isFree").value(DEFAULT_IS_FREE))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER))
            .andExpect(jsonPath("$.isPublished").value(DEFAULT_IS_PUBLISHED));
    }

    @Test
    @Transactional
    void getLessonsByIdFiltering() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        Long id = lesson.getId();

        defaultLessonFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLessonFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLessonFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLessonsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where title equals to
        defaultLessonFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLessonsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where title in
        defaultLessonFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLessonsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where title is not null
        defaultLessonFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where title contains
        defaultLessonFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLessonsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where title does not contain
        defaultLessonFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoUrl equals to
        defaultLessonFiltering("videoUrl.equals=" + DEFAULT_VIDEO_URL, "videoUrl.equals=" + UPDATED_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoUrl in
        defaultLessonFiltering("videoUrl.in=" + DEFAULT_VIDEO_URL + "," + UPDATED_VIDEO_URL, "videoUrl.in=" + UPDATED_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoUrl is not null
        defaultLessonFiltering("videoUrl.specified=true", "videoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByVideoUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoUrl contains
        defaultLessonFiltering("videoUrl.contains=" + DEFAULT_VIDEO_URL, "videoUrl.contains=" + UPDATED_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllLessonsByVideoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where videoUrl does not contain
        defaultLessonFiltering("videoUrl.doesNotContain=" + UPDATED_VIDEO_URL, "videoUrl.doesNotContain=" + DEFAULT_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllLessonsByDurationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where duration equals to
        defaultLessonFiltering("duration.equals=" + DEFAULT_DURATION, "duration.equals=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllLessonsByDurationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where duration in
        defaultLessonFiltering("duration.in=" + DEFAULT_DURATION + "," + UPDATED_DURATION, "duration.in=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllLessonsByDurationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where duration is not null
        defaultLessonFiltering("duration.specified=true", "duration.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByDurationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where duration is greater than or equal to
        defaultLessonFiltering("duration.greaterThanOrEqual=" + DEFAULT_DURATION, "duration.greaterThanOrEqual=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllLessonsByDurationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where duration is less than or equal to
        defaultLessonFiltering("duration.lessThanOrEqual=" + DEFAULT_DURATION, "duration.lessThanOrEqual=" + SMALLER_DURATION);
    }

    @Test
    @Transactional
    void getAllLessonsByDurationIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where duration is less than
        defaultLessonFiltering("duration.lessThan=" + UPDATED_DURATION, "duration.lessThan=" + DEFAULT_DURATION);
    }

    @Test
    @Transactional
    void getAllLessonsByDurationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where duration is greater than
        defaultLessonFiltering("duration.greaterThan=" + SMALLER_DURATION, "duration.greaterThan=" + DEFAULT_DURATION);
    }

    @Test
    @Transactional
    void getAllLessonsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where type equals to
        defaultLessonFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllLessonsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where type in
        defaultLessonFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllLessonsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where type is not null
        defaultLessonFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByIsFreeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where isFree equals to
        defaultLessonFiltering("isFree.equals=" + DEFAULT_IS_FREE, "isFree.equals=" + UPDATED_IS_FREE);
    }

    @Test
    @Transactional
    void getAllLessonsByIsFreeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where isFree in
        defaultLessonFiltering("isFree.in=" + DEFAULT_IS_FREE + "," + UPDATED_IS_FREE, "isFree.in=" + UPDATED_IS_FREE);
    }

    @Test
    @Transactional
    void getAllLessonsByIsFreeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where isFree is not null
        defaultLessonFiltering("isFree.specified=true", "isFree.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsBySortOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where sortOrder equals to
        defaultLessonFiltering("sortOrder.equals=" + DEFAULT_SORT_ORDER, "sortOrder.equals=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllLessonsBySortOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where sortOrder in
        defaultLessonFiltering("sortOrder.in=" + DEFAULT_SORT_ORDER + "," + UPDATED_SORT_ORDER, "sortOrder.in=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllLessonsBySortOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where sortOrder is not null
        defaultLessonFiltering("sortOrder.specified=true", "sortOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsBySortOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where sortOrder is greater than or equal to
        defaultLessonFiltering("sortOrder.greaterThanOrEqual=" + DEFAULT_SORT_ORDER, "sortOrder.greaterThanOrEqual=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllLessonsBySortOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where sortOrder is less than or equal to
        defaultLessonFiltering("sortOrder.lessThanOrEqual=" + DEFAULT_SORT_ORDER, "sortOrder.lessThanOrEqual=" + SMALLER_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllLessonsBySortOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where sortOrder is less than
        defaultLessonFiltering("sortOrder.lessThan=" + UPDATED_SORT_ORDER, "sortOrder.lessThan=" + DEFAULT_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllLessonsBySortOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where sortOrder is greater than
        defaultLessonFiltering("sortOrder.greaterThan=" + SMALLER_SORT_ORDER, "sortOrder.greaterThan=" + DEFAULT_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllLessonsByIsPublishedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where isPublished equals to
        defaultLessonFiltering("isPublished.equals=" + DEFAULT_IS_PUBLISHED, "isPublished.equals=" + UPDATED_IS_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllLessonsByIsPublishedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where isPublished in
        defaultLessonFiltering(
            "isPublished.in=" + DEFAULT_IS_PUBLISHED + "," + UPDATED_IS_PUBLISHED,
            "isPublished.in=" + UPDATED_IS_PUBLISHED
        );
    }

    @Test
    @Transactional
    void getAllLessonsByIsPublishedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where isPublished is not null
        defaultLessonFiltering("isPublished.specified=true", "isPublished.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByCourseIsEqualToSomething() throws Exception {
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            lessonRepository.saveAndFlush(lesson);
            course = CourseResourceIT.createEntity();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        em.persist(course);
        em.flush();
        lesson.setCourse(course);
        lessonRepository.saveAndFlush(lesson);
        Long courseId = course.getId();
        // Get all the lessonList where course equals to courseId
        defaultLessonShouldBeFound("courseId.equals=" + courseId);

        // Get all the lessonList where course equals to (courseId + 1)
        defaultLessonShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    private void defaultLessonFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLessonShouldBeFound(shouldBeFound);
        defaultLessonShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLessonShouldBeFound(String filter) throws Exception {
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].videoUrl").value(hasItem(DEFAULT_VIDEO_URL)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].isFree").value(hasItem(DEFAULT_IS_FREE)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)))
            .andExpect(jsonPath("$.[*].isPublished").value(hasItem(DEFAULT_IS_PUBLISHED)));

        // Check, that the count call also returns 1
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLessonShouldNotBeFound(String filter) throws Exception {
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLesson() throws Exception {
        // Get the lesson
        restLessonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLesson() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lesson
        Lesson updatedLesson = lessonRepository.findById(lesson.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLesson are not directly saved in db
        em.detach(updatedLesson);
        updatedLesson
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .content(UPDATED_CONTENT)
            .videoUrl(UPDATED_VIDEO_URL)
            .duration(UPDATED_DURATION)
            .type(UPDATED_TYPE)
            .isFree(UPDATED_IS_FREE)
            .sortOrder(UPDATED_SORT_ORDER)
            .isPublished(UPDATED_IS_PUBLISHED);
        LessonDTO lessonDTO = lessonMapper.toDto(updatedLesson);

        restLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO))
            )
            .andExpect(status().isOk());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLessonToMatchAllProperties(updatedLesson);
    }

    @Test
    @Transactional
    void putNonExistingLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLessonWithPatch() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lesson using partial update
        Lesson partialUpdatedLesson = new Lesson();
        partialUpdatedLesson.setId(lesson.getId());

        partialUpdatedLesson
            .description(UPDATED_DESCRIPTION)
            .content(UPDATED_CONTENT)
            .duration(UPDATED_DURATION)
            .type(UPDATED_TYPE)
            .sortOrder(UPDATED_SORT_ORDER)
            .isPublished(UPDATED_IS_PUBLISHED);

        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLesson.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLesson))
            )
            .andExpect(status().isOk());

        // Validate the Lesson in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLessonUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedLesson, lesson), getPersistedLesson(lesson));
    }

    @Test
    @Transactional
    void fullUpdateLessonWithPatch() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lesson using partial update
        Lesson partialUpdatedLesson = new Lesson();
        partialUpdatedLesson.setId(lesson.getId());

        partialUpdatedLesson
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .content(UPDATED_CONTENT)
            .videoUrl(UPDATED_VIDEO_URL)
            .duration(UPDATED_DURATION)
            .type(UPDATED_TYPE)
            .isFree(UPDATED_IS_FREE)
            .sortOrder(UPDATED_SORT_ORDER)
            .isPublished(UPDATED_IS_PUBLISHED);

        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLesson.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLesson))
            )
            .andExpect(status().isOk());

        // Validate the Lesson in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLessonUpdatableFieldsEquals(partialUpdatedLesson, getPersistedLesson(partialUpdatedLesson));
    }

    @Test
    @Transactional
    void patchNonExistingLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lessonDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLesson() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lesson.setId(longCount.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(lessonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lesson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLesson() throws Exception {
        // Initialize the database
        insertedLesson = lessonRepository.saveAndFlush(lesson);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the lesson
        restLessonMockMvc
            .perform(delete(ENTITY_API_URL_ID, lesson.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return lessonRepository.count();
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

    protected Lesson getPersistedLesson(Lesson lesson) {
        return lessonRepository.findById(lesson.getId()).orElseThrow();
    }

    protected void assertPersistedLessonToMatchAllProperties(Lesson expectedLesson) {
        assertLessonAllPropertiesEquals(expectedLesson, getPersistedLesson(expectedLesson));
    }

    protected void assertPersistedLessonToMatchUpdatableProperties(Lesson expectedLesson) {
        assertLessonAllUpdatablePropertiesEquals(expectedLesson, getPersistedLesson(expectedLesson));
    }
}

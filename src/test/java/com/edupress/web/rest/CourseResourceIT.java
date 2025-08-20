package com.edupress.web.rest;

import static com.edupress.domain.CourseAsserts.*;
import static com.edupress.web.rest.TestUtil.createUpdateProxyForBean;
import static com.edupress.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edupress.IntegrationTest;
import com.edupress.domain.AppUser;
import com.edupress.domain.Category;
import com.edupress.domain.Course;
import com.edupress.domain.enumeration.CourseStatus;
import com.edupress.repository.CourseRepository;
import com.edupress.security.AuthoritiesConstants;
import com.edupress.service.CourseService;
import com.edupress.service.dto.CourseDTO;
import com.edupress.service.mapper.CourseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link CourseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class CourseResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_THUMBNAIL_URL = "AAAAAAAAAA";
    private static final String UPDATED_THUMBNAIL_URL = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_ORIGINAL_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ORIGINAL_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_ORIGINAL_PRICE = new BigDecimal(1 - 1);

    private static final String DEFAULT_LEVEL = "AAAAAAAAAA";
    private static final String UPDATED_LEVEL = "BBBBBBBBBB";

    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final CourseStatus DEFAULT_STATUS = CourseStatus.DRAFT;
    private static final CourseStatus UPDATED_STATUS = CourseStatus.PUBLISHED;

    private static final Boolean DEFAULT_IS_PUBLISHED = false;
    private static final Boolean UPDATED_IS_PUBLISHED = true;

    private static final Boolean DEFAULT_IS_FEATURED = false;
    private static final Boolean UPDATED_IS_FEATURED = true;

    private static final Double DEFAULT_AVERAGE_RATING = 1D;
    private static final Double UPDATED_AVERAGE_RATING = 2D;
    private static final Double SMALLER_AVERAGE_RATING = 1D - 1D;

    private static final Integer DEFAULT_ENROLLMENT_COUNT = 1;
    private static final Integer UPDATED_ENROLLMENT_COUNT = 2;
    private static final Integer SMALLER_ENROLLMENT_COUNT = 1 - 1;

    private static final String ENTITY_API_URL = "/api/courses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseRepository courseRepository;

    @Mock
    private CourseRepository courseRepositoryMock;

    @Autowired
    private CourseMapper courseMapper;

    @Mock
    private CourseService courseServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseMockMvc;

    private Course course;

    private Course insertedCourse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createEntity() {
        return new Course()
            .title(DEFAULT_TITLE)
            .slug(DEFAULT_SLUG)
            .shortDescription(DEFAULT_SHORT_DESCRIPTION)
            .description(DEFAULT_DESCRIPTION)
            .thumbnailUrl(DEFAULT_THUMBNAIL_URL)
            .price(DEFAULT_PRICE)
            .originalPrice(DEFAULT_ORIGINAL_PRICE)
            .level(DEFAULT_LEVEL)
            .language(DEFAULT_LANGUAGE)
            .status(DEFAULT_STATUS)
            .isPublished(DEFAULT_IS_PUBLISHED)
            .isFeatured(DEFAULT_IS_FEATURED)
            .averageRating(DEFAULT_AVERAGE_RATING)
            .enrollmentCount(DEFAULT_ENROLLMENT_COUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createUpdatedEntity() {
        return new Course()
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .description(UPDATED_DESCRIPTION)
            .thumbnailUrl(UPDATED_THUMBNAIL_URL)
            .price(UPDATED_PRICE)
            .originalPrice(UPDATED_ORIGINAL_PRICE)
            .level(UPDATED_LEVEL)
            .language(UPDATED_LANGUAGE)
            .status(UPDATED_STATUS)
            .isPublished(UPDATED_IS_PUBLISHED)
            .isFeatured(UPDATED_IS_FEATURED)
            .averageRating(UPDATED_AVERAGE_RATING)
            .enrollmentCount(UPDATED_ENROLLMENT_COUNT);
    }

    @BeforeEach
    void initTest() {
        course = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCourse != null) {
            courseRepository.delete(insertedCourse);
            insertedCourse = null;
        }
    }

    @Test
    @Transactional
    void createCourse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);
        var returnedCourseDTO = om.readValue(
            restCourseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseDTO.class
        );

        // Validate the Course in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCourse = courseMapper.toEntity(returnedCourseDTO);
        assertCourseUpdatableFieldsEquals(returnedCourse, getPersistedCourse(returnedCourse));

        insertedCourse = returnedCourse;
    }

    @Test
    @Transactional
    void createCourseWithExistingId() throws Exception {
        // Create the Course with an existing ID
        course.setId(1L);
        CourseDTO courseDTO = courseMapper.toDto(course);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        course.setTitle(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        course.setSlug(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        course.setPrice(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        course.setStatus(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsPublishedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        course.setIsPublished(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourses() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].thumbnailUrl").value(hasItem(DEFAULT_THUMBNAIL_URL)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].originalPrice").value(hasItem(sameNumber(DEFAULT_ORIGINAL_PRICE))))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isPublished").value(hasItem(DEFAULT_IS_PUBLISHED)))
            .andExpect(jsonPath("$.[*].isFeatured").value(hasItem(DEFAULT_IS_FEATURED)))
            .andExpect(jsonPath("$.[*].averageRating").value(hasItem(DEFAULT_AVERAGE_RATING)))
            .andExpect(jsonPath("$.[*].enrollmentCount").value(hasItem(DEFAULT_ENROLLMENT_COUNT)));
    }

    void getAllCoursesWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl<CourseDTO>(new ArrayList<>()));

        restCourseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    void getAllCoursesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl<CourseDTO>(new ArrayList<>()));

        restCourseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(courseRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCourse() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get the course
        restCourseMockMvc
            .perform(get(ENTITY_API_URL_ID, course.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(course.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.shortDescription").value(DEFAULT_SHORT_DESCRIPTION))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.thumbnailUrl").value(DEFAULT_THUMBNAIL_URL))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.originalPrice").value(sameNumber(DEFAULT_ORIGINAL_PRICE)))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.isPublished").value(DEFAULT_IS_PUBLISHED))
            .andExpect(jsonPath("$.isFeatured").value(DEFAULT_IS_FEATURED))
            .andExpect(jsonPath("$.averageRating").value(DEFAULT_AVERAGE_RATING))
            .andExpect(jsonPath("$.enrollmentCount").value(DEFAULT_ENROLLMENT_COUNT));
    }

    @Test
    @Transactional
    void getCoursesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        Long id = course.getId();

        defaultCourseFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCourseFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCourseFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCoursesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where title equals to
        defaultCourseFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where title in
        defaultCourseFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where title is not null
        defaultCourseFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where title contains
        defaultCourseFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where title does not contain
        defaultCourseFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where slug equals to
        defaultCourseFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllCoursesBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where slug in
        defaultCourseFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllCoursesBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where slug is not null
        defaultCourseFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where slug contains
        defaultCourseFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllCoursesBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where slug does not contain
        defaultCourseFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllCoursesByShortDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where shortDescription equals to
        defaultCourseFiltering(
            "shortDescription.equals=" + DEFAULT_SHORT_DESCRIPTION,
            "shortDescription.equals=" + UPDATED_SHORT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllCoursesByShortDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where shortDescription in
        defaultCourseFiltering(
            "shortDescription.in=" + DEFAULT_SHORT_DESCRIPTION + "," + UPDATED_SHORT_DESCRIPTION,
            "shortDescription.in=" + UPDATED_SHORT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllCoursesByShortDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where shortDescription is not null
        defaultCourseFiltering("shortDescription.specified=true", "shortDescription.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByShortDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where shortDescription contains
        defaultCourseFiltering(
            "shortDescription.contains=" + DEFAULT_SHORT_DESCRIPTION,
            "shortDescription.contains=" + UPDATED_SHORT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllCoursesByShortDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where shortDescription does not contain
        defaultCourseFiltering(
            "shortDescription.doesNotContain=" + UPDATED_SHORT_DESCRIPTION,
            "shortDescription.doesNotContain=" + DEFAULT_SHORT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllCoursesByThumbnailUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where thumbnailUrl equals to
        defaultCourseFiltering("thumbnailUrl.equals=" + DEFAULT_THUMBNAIL_URL, "thumbnailUrl.equals=" + UPDATED_THUMBNAIL_URL);
    }

    @Test
    @Transactional
    void getAllCoursesByThumbnailUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where thumbnailUrl in
        defaultCourseFiltering(
            "thumbnailUrl.in=" + DEFAULT_THUMBNAIL_URL + "," + UPDATED_THUMBNAIL_URL,
            "thumbnailUrl.in=" + UPDATED_THUMBNAIL_URL
        );
    }

    @Test
    @Transactional
    void getAllCoursesByThumbnailUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where thumbnailUrl is not null
        defaultCourseFiltering("thumbnailUrl.specified=true", "thumbnailUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByThumbnailUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where thumbnailUrl contains
        defaultCourseFiltering("thumbnailUrl.contains=" + DEFAULT_THUMBNAIL_URL, "thumbnailUrl.contains=" + UPDATED_THUMBNAIL_URL);
    }

    @Test
    @Transactional
    void getAllCoursesByThumbnailUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where thumbnailUrl does not contain
        defaultCourseFiltering(
            "thumbnailUrl.doesNotContain=" + UPDATED_THUMBNAIL_URL,
            "thumbnailUrl.doesNotContain=" + DEFAULT_THUMBNAIL_URL
        );
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price equals to
        defaultCourseFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price in
        defaultCourseFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price is not null
        defaultCourseFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price is greater than or equal to
        defaultCourseFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price is less than or equal to
        defaultCourseFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price is less than
        defaultCourseFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where price is greater than
        defaultCourseFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByOriginalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where originalPrice equals to
        defaultCourseFiltering("originalPrice.equals=" + DEFAULT_ORIGINAL_PRICE, "originalPrice.equals=" + UPDATED_ORIGINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByOriginalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where originalPrice in
        defaultCourseFiltering(
            "originalPrice.in=" + DEFAULT_ORIGINAL_PRICE + "," + UPDATED_ORIGINAL_PRICE,
            "originalPrice.in=" + UPDATED_ORIGINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByOriginalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where originalPrice is not null
        defaultCourseFiltering("originalPrice.specified=true", "originalPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByOriginalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where originalPrice is greater than or equal to
        defaultCourseFiltering(
            "originalPrice.greaterThanOrEqual=" + DEFAULT_ORIGINAL_PRICE,
            "originalPrice.greaterThanOrEqual=" + UPDATED_ORIGINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByOriginalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where originalPrice is less than or equal to
        defaultCourseFiltering(
            "originalPrice.lessThanOrEqual=" + DEFAULT_ORIGINAL_PRICE,
            "originalPrice.lessThanOrEqual=" + SMALLER_ORIGINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByOriginalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where originalPrice is less than
        defaultCourseFiltering("originalPrice.lessThan=" + UPDATED_ORIGINAL_PRICE, "originalPrice.lessThan=" + DEFAULT_ORIGINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllCoursesByOriginalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where originalPrice is greater than
        defaultCourseFiltering(
            "originalPrice.greaterThan=" + SMALLER_ORIGINAL_PRICE,
            "originalPrice.greaterThan=" + DEFAULT_ORIGINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllCoursesByLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where level equals to
        defaultCourseFiltering("level.equals=" + DEFAULT_LEVEL, "level.equals=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllCoursesByLevelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where level in
        defaultCourseFiltering("level.in=" + DEFAULT_LEVEL + "," + UPDATED_LEVEL, "level.in=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllCoursesByLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where level is not null
        defaultCourseFiltering("level.specified=true", "level.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByLevelContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where level contains
        defaultCourseFiltering("level.contains=" + DEFAULT_LEVEL, "level.contains=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllCoursesByLevelNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where level does not contain
        defaultCourseFiltering("level.doesNotContain=" + UPDATED_LEVEL, "level.doesNotContain=" + DEFAULT_LEVEL);
    }

    @Test
    @Transactional
    void getAllCoursesByLanguageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where language equals to
        defaultCourseFiltering("language.equals=" + DEFAULT_LANGUAGE, "language.equals=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllCoursesByLanguageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where language in
        defaultCourseFiltering("language.in=" + DEFAULT_LANGUAGE + "," + UPDATED_LANGUAGE, "language.in=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllCoursesByLanguageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where language is not null
        defaultCourseFiltering("language.specified=true", "language.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByLanguageContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where language contains
        defaultCourseFiltering("language.contains=" + DEFAULT_LANGUAGE, "language.contains=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllCoursesByLanguageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where language does not contain
        defaultCourseFiltering("language.doesNotContain=" + UPDATED_LANGUAGE, "language.doesNotContain=" + DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllCoursesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where status equals to
        defaultCourseFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCoursesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where status in
        defaultCourseFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCoursesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where status is not null
        defaultCourseFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByIsPublishedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where isPublished equals to
        defaultCourseFiltering("isPublished.equals=" + DEFAULT_IS_PUBLISHED, "isPublished.equals=" + UPDATED_IS_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllCoursesByIsPublishedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where isPublished in
        defaultCourseFiltering(
            "isPublished.in=" + DEFAULT_IS_PUBLISHED + "," + UPDATED_IS_PUBLISHED,
            "isPublished.in=" + UPDATED_IS_PUBLISHED
        );
    }

    @Test
    @Transactional
    void getAllCoursesByIsPublishedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where isPublished is not null
        defaultCourseFiltering("isPublished.specified=true", "isPublished.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByIsFeaturedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where isFeatured equals to
        defaultCourseFiltering("isFeatured.equals=" + DEFAULT_IS_FEATURED, "isFeatured.equals=" + UPDATED_IS_FEATURED);
    }

    @Test
    @Transactional
    void getAllCoursesByIsFeaturedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where isFeatured in
        defaultCourseFiltering("isFeatured.in=" + DEFAULT_IS_FEATURED + "," + UPDATED_IS_FEATURED, "isFeatured.in=" + UPDATED_IS_FEATURED);
    }

    @Test
    @Transactional
    void getAllCoursesByIsFeaturedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where isFeatured is not null
        defaultCourseFiltering("isFeatured.specified=true", "isFeatured.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByAverageRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where averageRating equals to
        defaultCourseFiltering("averageRating.equals=" + DEFAULT_AVERAGE_RATING, "averageRating.equals=" + UPDATED_AVERAGE_RATING);
    }

    @Test
    @Transactional
    void getAllCoursesByAverageRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where averageRating in
        defaultCourseFiltering(
            "averageRating.in=" + DEFAULT_AVERAGE_RATING + "," + UPDATED_AVERAGE_RATING,
            "averageRating.in=" + UPDATED_AVERAGE_RATING
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAverageRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where averageRating is not null
        defaultCourseFiltering("averageRating.specified=true", "averageRating.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByAverageRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where averageRating is greater than or equal to
        defaultCourseFiltering(
            "averageRating.greaterThanOrEqual=" + DEFAULT_AVERAGE_RATING,
            "averageRating.greaterThanOrEqual=" + UPDATED_AVERAGE_RATING
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAverageRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where averageRating is less than or equal to
        defaultCourseFiltering(
            "averageRating.lessThanOrEqual=" + DEFAULT_AVERAGE_RATING,
            "averageRating.lessThanOrEqual=" + SMALLER_AVERAGE_RATING
        );
    }

    @Test
    @Transactional
    void getAllCoursesByAverageRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where averageRating is less than
        defaultCourseFiltering("averageRating.lessThan=" + UPDATED_AVERAGE_RATING, "averageRating.lessThan=" + DEFAULT_AVERAGE_RATING);
    }

    @Test
    @Transactional
    void getAllCoursesByAverageRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where averageRating is greater than
        defaultCourseFiltering(
            "averageRating.greaterThan=" + SMALLER_AVERAGE_RATING,
            "averageRating.greaterThan=" + DEFAULT_AVERAGE_RATING
        );
    }

    @Test
    @Transactional
    void getAllCoursesByEnrollmentCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where enrollmentCount equals to
        defaultCourseFiltering("enrollmentCount.equals=" + DEFAULT_ENROLLMENT_COUNT, "enrollmentCount.equals=" + UPDATED_ENROLLMENT_COUNT);
    }

    @Test
    @Transactional
    void getAllCoursesByEnrollmentCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where enrollmentCount in
        defaultCourseFiltering(
            "enrollmentCount.in=" + DEFAULT_ENROLLMENT_COUNT + "," + UPDATED_ENROLLMENT_COUNT,
            "enrollmentCount.in=" + UPDATED_ENROLLMENT_COUNT
        );
    }

    @Test
    @Transactional
    void getAllCoursesByEnrollmentCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where enrollmentCount is not null
        defaultCourseFiltering("enrollmentCount.specified=true", "enrollmentCount.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByEnrollmentCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where enrollmentCount is greater than or equal to
        defaultCourseFiltering(
            "enrollmentCount.greaterThanOrEqual=" + DEFAULT_ENROLLMENT_COUNT,
            "enrollmentCount.greaterThanOrEqual=" + UPDATED_ENROLLMENT_COUNT
        );
    }

    @Test
    @Transactional
    void getAllCoursesByEnrollmentCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where enrollmentCount is less than or equal to
        defaultCourseFiltering(
            "enrollmentCount.lessThanOrEqual=" + DEFAULT_ENROLLMENT_COUNT,
            "enrollmentCount.lessThanOrEqual=" + SMALLER_ENROLLMENT_COUNT
        );
    }

    @Test
    @Transactional
    void getAllCoursesByEnrollmentCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where enrollmentCount is less than
        defaultCourseFiltering(
            "enrollmentCount.lessThan=" + UPDATED_ENROLLMENT_COUNT,
            "enrollmentCount.lessThan=" + DEFAULT_ENROLLMENT_COUNT
        );
    }

    @Test
    @Transactional
    void getAllCoursesByEnrollmentCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList where enrollmentCount is greater than
        defaultCourseFiltering(
            "enrollmentCount.greaterThan=" + SMALLER_ENROLLMENT_COUNT,
            "enrollmentCount.greaterThan=" + DEFAULT_ENROLLMENT_COUNT
        );
    }

    @Test
    @Transactional
    void getAllCoursesByInstructorIsEqualToSomething() throws Exception {
        AppUser instructor;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            courseRepository.saveAndFlush(course);
            instructor = AppUserResourceIT.createEntity();
        } else {
            instructor = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(instructor);
        em.flush();
        course.setInstructor(instructor);
        courseRepository.saveAndFlush(course);
        Long instructorId = instructor.getId();
        // Get all the courseList where instructor equals to instructorId
        defaultCourseShouldBeFound("instructorId.equals=" + instructorId);

        // Get all the courseList where instructor equals to (instructorId + 1)
        defaultCourseShouldNotBeFound("instructorId.equals=" + (instructorId + 1));
    }

    @Test
    @Transactional
    void getAllCoursesByCategoryIsEqualToSomething() throws Exception {
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            courseRepository.saveAndFlush(course);
            category = CategoryResourceIT.createEntity();
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        em.persist(category);
        em.flush();
        course.setCategory(category);
        courseRepository.saveAndFlush(course);
        Long categoryId = category.getId();
        // Get all the courseList where category equals to categoryId
        defaultCourseShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the courseList where category equals to (categoryId + 1)
        defaultCourseShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    private void defaultCourseFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCourseShouldBeFound(shouldBeFound);
        defaultCourseShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCourseShouldBeFound(String filter) throws Exception {
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].thumbnailUrl").value(hasItem(DEFAULT_THUMBNAIL_URL)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].originalPrice").value(hasItem(sameNumber(DEFAULT_ORIGINAL_PRICE))))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isPublished").value(hasItem(DEFAULT_IS_PUBLISHED)))
            .andExpect(jsonPath("$.[*].isFeatured").value(hasItem(DEFAULT_IS_FEATURED)))
            .andExpect(jsonPath("$.[*].averageRating").value(hasItem(DEFAULT_AVERAGE_RATING)))
            .andExpect(jsonPath("$.[*].enrollmentCount").value(hasItem(DEFAULT_ENROLLMENT_COUNT)));

        // Check, that the count call also returns 1
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCourseShouldNotBeFound(String filter) throws Exception {
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCourse() throws Exception {
        // Get the course
        restCourseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourse() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the course
        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourse are not directly saved in db
        em.detach(updatedCourse);
        updatedCourse
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .description(UPDATED_DESCRIPTION)
            .thumbnailUrl(UPDATED_THUMBNAIL_URL)
            .price(UPDATED_PRICE)
            .originalPrice(UPDATED_ORIGINAL_PRICE)
            .level(UPDATED_LEVEL)
            .language(UPDATED_LANGUAGE)
            .status(UPDATED_STATUS)
            .isPublished(UPDATED_IS_PUBLISHED)
            .isFeatured(UPDATED_IS_FEATURED)
            .averageRating(UPDATED_AVERAGE_RATING)
            .enrollmentCount(UPDATED_ENROLLMENT_COUNT);
        CourseDTO courseDTO = courseMapper.toDto(updatedCourse);

        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseToMatchAllProperties(updatedCourse);
    }

    @Test
    @Transactional
    void putNonExistingCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .originalPrice(UPDATED_ORIGINAL_PRICE)
            .language(UPDATED_LANGUAGE)
            .status(UPDATED_STATUS)
            .averageRating(UPDATED_AVERAGE_RATING);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCourse, course), getPersistedCourse(course));
    }

    @Test
    @Transactional
    void fullUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .description(UPDATED_DESCRIPTION)
            .thumbnailUrl(UPDATED_THUMBNAIL_URL)
            .price(UPDATED_PRICE)
            .originalPrice(UPDATED_ORIGINAL_PRICE)
            .level(UPDATED_LEVEL)
            .language(UPDATED_LANGUAGE)
            .status(UPDATED_STATUS)
            .isPublished(UPDATED_IS_PUBLISHED)
            .isFeatured(UPDATED_IS_FEATURED)
            .averageRating(UPDATED_AVERAGE_RATING)
            .enrollmentCount(UPDATED_ENROLLMENT_COUNT);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseUpdatableFieldsEquals(partialUpdatedCourse, getPersistedCourse(partialUpdatedCourse));
    }

    @Test
    @Transactional
    void patchNonExistingCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourse() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the course
        restCourseMockMvc
            .perform(delete(ENTITY_API_URL_ID, course.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseRepository.count();
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

    protected Course getPersistedCourse(Course course) {
        return courseRepository.findById(course.getId()).orElseThrow();
    }

    protected void assertPersistedCourseToMatchAllProperties(Course expectedCourse) {
        assertCourseAllPropertiesEquals(expectedCourse, getPersistedCourse(expectedCourse));
    }

    protected void assertPersistedCourseToMatchUpdatableProperties(Course expectedCourse) {
        assertCourseAllUpdatablePropertiesEquals(expectedCourse, getPersistedCourse(expectedCourse));
    }
}

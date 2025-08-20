package com.edupress.web.rest;

import static com.edupress.domain.EnrollmentAsserts.*;
import static com.edupress.web.rest.TestUtil.createUpdateProxyForBean;
import static com.edupress.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edupress.IntegrationTest;
import com.edupress.domain.AppUser;
import com.edupress.domain.Course;
import com.edupress.domain.Enrollment;
import com.edupress.domain.enumeration.EnrollmentStatus;
import com.edupress.domain.enumeration.PaymentStatus;
import com.edupress.repository.EnrollmentRepository;
import com.edupress.service.EnrollmentService;
import com.edupress.service.dto.EnrollmentDTO;
import com.edupress.service.mapper.EnrollmentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link EnrollmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EnrollmentResourceIT {

    private static final Instant DEFAULT_ENROLLMENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENROLLMENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_PROGRESS_PERCENTAGE = 1;
    private static final Integer UPDATED_PROGRESS_PERCENTAGE = 2;
    private static final Integer SMALLER_PROGRESS_PERCENTAGE = 1 - 1;

    private static final String DEFAULT_PROGRESS = "AAAAAAAAAA";
    private static final String UPDATED_PROGRESS = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_ACCESSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_ACCESSED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final EnrollmentStatus DEFAULT_STATUS = EnrollmentStatus.ACTIVE;
    private static final EnrollmentStatus UPDATED_STATUS = EnrollmentStatus.COMPLETED;

    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.PENDING;
    private static final PaymentStatus UPDATED_PAYMENT_STATUS = PaymentStatus.COMPLETED;

    private static final BigDecimal DEFAULT_AMOUNT_PAID = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_PAID = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT_PAID = new BigDecimal(1 - 1);

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/enrollments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private EnrollmentRepository enrollmentRepositoryMock;

    @Autowired
    private EnrollmentMapper enrollmentMapper;

    @Mock
    private EnrollmentService enrollmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnrollmentMockMvc;

    private Enrollment enrollment;

    private Enrollment insertedEnrollment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enrollment createEntity() {
        return new Enrollment()
            .enrollmentDate(DEFAULT_ENROLLMENT_DATE)
            .progressPercentage(DEFAULT_PROGRESS_PERCENTAGE)
            .progress(DEFAULT_PROGRESS)
            .lastAccessedAt(DEFAULT_LAST_ACCESSED_AT)
            .status(DEFAULT_STATUS)
            .paymentStatus(DEFAULT_PAYMENT_STATUS)
            .amountPaid(DEFAULT_AMOUNT_PAID)
            .transactionId(DEFAULT_TRANSACTION_ID)
            .completedAt(DEFAULT_COMPLETED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enrollment createUpdatedEntity() {
        return new Enrollment()
            .enrollmentDate(UPDATED_ENROLLMENT_DATE)
            .progressPercentage(UPDATED_PROGRESS_PERCENTAGE)
            .progress(UPDATED_PROGRESS)
            .lastAccessedAt(UPDATED_LAST_ACCESSED_AT)
            .status(UPDATED_STATUS)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .transactionId(UPDATED_TRANSACTION_ID)
            .completedAt(UPDATED_COMPLETED_AT);
    }

    @BeforeEach
    void initTest() {
        enrollment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEnrollment != null) {
            enrollmentRepository.delete(insertedEnrollment);
            insertedEnrollment = null;
        }
    }

    @Test
    @Transactional
    void createEnrollment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);
        var returnedEnrollmentDTO = om.readValue(
            restEnrollmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(enrollmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EnrollmentDTO.class
        );

        // Validate the Enrollment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEnrollment = enrollmentMapper.toEntity(returnedEnrollmentDTO);
        assertEnrollmentUpdatableFieldsEquals(returnedEnrollment, getPersistedEnrollment(returnedEnrollment));

        insertedEnrollment = returnedEnrollment;
    }

    @Test
    @Transactional
    void createEnrollmentWithExistingId() throws Exception {
        // Create the Enrollment with an existing ID
        enrollment.setId(1L);
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnrollmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(enrollmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEnrollments() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(enrollment.getId().intValue())))
            .andExpect(jsonPath("$.[*].enrollmentDate").value(hasItem(DEFAULT_ENROLLMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].progressPercentage").value(hasItem(DEFAULT_PROGRESS_PERCENTAGE)))
            .andExpect(jsonPath("$.[*].progress").value(hasItem(DEFAULT_PROGRESS)))
            .andExpect(jsonPath("$.[*].lastAccessedAt").value(hasItem(DEFAULT_LAST_ACCESSED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID))))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnrollmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(enrollmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEnrollmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(enrollmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnrollmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(enrollmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEnrollmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(enrollmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEnrollment() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get the enrollment
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL_ID, enrollment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(enrollment.getId().intValue()))
            .andExpect(jsonPath("$.enrollmentDate").value(DEFAULT_ENROLLMENT_DATE.toString()))
            .andExpect(jsonPath("$.progressPercentage").value(DEFAULT_PROGRESS_PERCENTAGE))
            .andExpect(jsonPath("$.progress").value(DEFAULT_PROGRESS))
            .andExpect(jsonPath("$.lastAccessedAt").value(DEFAULT_LAST_ACCESSED_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.paymentStatus").value(DEFAULT_PAYMENT_STATUS.toString()))
            .andExpect(jsonPath("$.amountPaid").value(sameNumber(DEFAULT_AMOUNT_PAID)))
            .andExpect(jsonPath("$.transactionId").value(DEFAULT_TRANSACTION_ID))
            .andExpect(jsonPath("$.completedAt").value(DEFAULT_COMPLETED_AT.toString()));
    }

    @Test
    @Transactional
    void getEnrollmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        Long id = enrollment.getId();

        defaultEnrollmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEnrollmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEnrollmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByEnrollmentDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where enrollmentDate equals to
        defaultEnrollmentFiltering("enrollmentDate.equals=" + DEFAULT_ENROLLMENT_DATE, "enrollmentDate.equals=" + UPDATED_ENROLLMENT_DATE);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByEnrollmentDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where enrollmentDate in
        defaultEnrollmentFiltering(
            "enrollmentDate.in=" + DEFAULT_ENROLLMENT_DATE + "," + UPDATED_ENROLLMENT_DATE,
            "enrollmentDate.in=" + UPDATED_ENROLLMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByEnrollmentDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where enrollmentDate is not null
        defaultEnrollmentFiltering("enrollmentDate.specified=true", "enrollmentDate.specified=false");
    }

    @Test
    @Transactional
    void getAllEnrollmentsByProgressPercentageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where progressPercentage equals to
        defaultEnrollmentFiltering(
            "progressPercentage.equals=" + DEFAULT_PROGRESS_PERCENTAGE,
            "progressPercentage.equals=" + UPDATED_PROGRESS_PERCENTAGE
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByProgressPercentageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where progressPercentage in
        defaultEnrollmentFiltering(
            "progressPercentage.in=" + DEFAULT_PROGRESS_PERCENTAGE + "," + UPDATED_PROGRESS_PERCENTAGE,
            "progressPercentage.in=" + UPDATED_PROGRESS_PERCENTAGE
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByProgressPercentageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where progressPercentage is not null
        defaultEnrollmentFiltering("progressPercentage.specified=true", "progressPercentage.specified=false");
    }

    @Test
    @Transactional
    void getAllEnrollmentsByProgressPercentageIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where progressPercentage is greater than or equal to
        defaultEnrollmentFiltering(
            "progressPercentage.greaterThanOrEqual=" + DEFAULT_PROGRESS_PERCENTAGE,
            "progressPercentage.greaterThanOrEqual=" + UPDATED_PROGRESS_PERCENTAGE
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByProgressPercentageIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where progressPercentage is less than or equal to
        defaultEnrollmentFiltering(
            "progressPercentage.lessThanOrEqual=" + DEFAULT_PROGRESS_PERCENTAGE,
            "progressPercentage.lessThanOrEqual=" + SMALLER_PROGRESS_PERCENTAGE
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByProgressPercentageIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where progressPercentage is less than
        defaultEnrollmentFiltering(
            "progressPercentage.lessThan=" + UPDATED_PROGRESS_PERCENTAGE,
            "progressPercentage.lessThan=" + DEFAULT_PROGRESS_PERCENTAGE
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByProgressPercentageIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where progressPercentage is greater than
        defaultEnrollmentFiltering(
            "progressPercentage.greaterThan=" + SMALLER_PROGRESS_PERCENTAGE,
            "progressPercentage.greaterThan=" + DEFAULT_PROGRESS_PERCENTAGE
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByLastAccessedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where lastAccessedAt equals to
        defaultEnrollmentFiltering(
            "lastAccessedAt.equals=" + DEFAULT_LAST_ACCESSED_AT,
            "lastAccessedAt.equals=" + UPDATED_LAST_ACCESSED_AT
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByLastAccessedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where lastAccessedAt in
        defaultEnrollmentFiltering(
            "lastAccessedAt.in=" + DEFAULT_LAST_ACCESSED_AT + "," + UPDATED_LAST_ACCESSED_AT,
            "lastAccessedAt.in=" + UPDATED_LAST_ACCESSED_AT
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByLastAccessedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where lastAccessedAt is not null
        defaultEnrollmentFiltering("lastAccessedAt.specified=true", "lastAccessedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEnrollmentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where status equals to
        defaultEnrollmentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where status in
        defaultEnrollmentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where status is not null
        defaultEnrollmentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllEnrollmentsByPaymentStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where paymentStatus equals to
        defaultEnrollmentFiltering("paymentStatus.equals=" + DEFAULT_PAYMENT_STATUS, "paymentStatus.equals=" + UPDATED_PAYMENT_STATUS);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByPaymentStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where paymentStatus in
        defaultEnrollmentFiltering(
            "paymentStatus.in=" + DEFAULT_PAYMENT_STATUS + "," + UPDATED_PAYMENT_STATUS,
            "paymentStatus.in=" + UPDATED_PAYMENT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByPaymentStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where paymentStatus is not null
        defaultEnrollmentFiltering("paymentStatus.specified=true", "paymentStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllEnrollmentsByAmountPaidIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where amountPaid equals to
        defaultEnrollmentFiltering("amountPaid.equals=" + DEFAULT_AMOUNT_PAID, "amountPaid.equals=" + UPDATED_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByAmountPaidIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where amountPaid in
        defaultEnrollmentFiltering(
            "amountPaid.in=" + DEFAULT_AMOUNT_PAID + "," + UPDATED_AMOUNT_PAID,
            "amountPaid.in=" + UPDATED_AMOUNT_PAID
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByAmountPaidIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where amountPaid is not null
        defaultEnrollmentFiltering("amountPaid.specified=true", "amountPaid.specified=false");
    }

    @Test
    @Transactional
    void getAllEnrollmentsByAmountPaidIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where amountPaid is greater than or equal to
        defaultEnrollmentFiltering(
            "amountPaid.greaterThanOrEqual=" + DEFAULT_AMOUNT_PAID,
            "amountPaid.greaterThanOrEqual=" + UPDATED_AMOUNT_PAID
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByAmountPaidIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where amountPaid is less than or equal to
        defaultEnrollmentFiltering(
            "amountPaid.lessThanOrEqual=" + DEFAULT_AMOUNT_PAID,
            "amountPaid.lessThanOrEqual=" + SMALLER_AMOUNT_PAID
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByAmountPaidIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where amountPaid is less than
        defaultEnrollmentFiltering("amountPaid.lessThan=" + UPDATED_AMOUNT_PAID, "amountPaid.lessThan=" + DEFAULT_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByAmountPaidIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where amountPaid is greater than
        defaultEnrollmentFiltering("amountPaid.greaterThan=" + SMALLER_AMOUNT_PAID, "amountPaid.greaterThan=" + DEFAULT_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByTransactionIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where transactionId equals to
        defaultEnrollmentFiltering("transactionId.equals=" + DEFAULT_TRANSACTION_ID, "transactionId.equals=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByTransactionIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where transactionId in
        defaultEnrollmentFiltering(
            "transactionId.in=" + DEFAULT_TRANSACTION_ID + "," + UPDATED_TRANSACTION_ID,
            "transactionId.in=" + UPDATED_TRANSACTION_ID
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByTransactionIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where transactionId is not null
        defaultEnrollmentFiltering("transactionId.specified=true", "transactionId.specified=false");
    }

    @Test
    @Transactional
    void getAllEnrollmentsByTransactionIdContainsSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where transactionId contains
        defaultEnrollmentFiltering("transactionId.contains=" + DEFAULT_TRANSACTION_ID, "transactionId.contains=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByTransactionIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where transactionId does not contain
        defaultEnrollmentFiltering(
            "transactionId.doesNotContain=" + UPDATED_TRANSACTION_ID,
            "transactionId.doesNotContain=" + DEFAULT_TRANSACTION_ID
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByCompletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where completedAt equals to
        defaultEnrollmentFiltering("completedAt.equals=" + DEFAULT_COMPLETED_AT, "completedAt.equals=" + UPDATED_COMPLETED_AT);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByCompletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where completedAt in
        defaultEnrollmentFiltering(
            "completedAt.in=" + DEFAULT_COMPLETED_AT + "," + UPDATED_COMPLETED_AT,
            "completedAt.in=" + UPDATED_COMPLETED_AT
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByCompletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where completedAt is not null
        defaultEnrollmentFiltering("completedAt.specified=true", "completedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEnrollmentsByCourseIsEqualToSomething() throws Exception {
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            enrollmentRepository.saveAndFlush(enrollment);
            course = CourseResourceIT.createEntity();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        em.persist(course);
        em.flush();
        enrollment.setCourse(course);
        enrollmentRepository.saveAndFlush(enrollment);
        Long courseId = course.getId();
        // Get all the enrollmentList where course equals to courseId
        defaultEnrollmentShouldBeFound("courseId.equals=" + courseId);

        // Get all the enrollmentList where course equals to (courseId + 1)
        defaultEnrollmentShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    @Test
    @Transactional
    void getAllEnrollmentsByStudentIsEqualToSomething() throws Exception {
        AppUser student;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            enrollmentRepository.saveAndFlush(enrollment);
            student = AppUserResourceIT.createEntity();
        } else {
            student = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(student);
        em.flush();
        enrollment.setStudent(student);
        enrollmentRepository.saveAndFlush(enrollment);
        Long studentId = student.getId();
        // Get all the enrollmentList where student equals to studentId
        defaultEnrollmentShouldBeFound("studentId.equals=" + studentId);

        // Get all the enrollmentList where student equals to (studentId + 1)
        defaultEnrollmentShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    private void defaultEnrollmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEnrollmentShouldBeFound(shouldBeFound);
        defaultEnrollmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEnrollmentShouldBeFound(String filter) throws Exception {
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(enrollment.getId().intValue())))
            .andExpect(jsonPath("$.[*].enrollmentDate").value(hasItem(DEFAULT_ENROLLMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].progressPercentage").value(hasItem(DEFAULT_PROGRESS_PERCENTAGE)))
            .andExpect(jsonPath("$.[*].progress").value(hasItem(DEFAULT_PROGRESS)))
            .andExpect(jsonPath("$.[*].lastAccessedAt").value(hasItem(DEFAULT_LAST_ACCESSED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID))))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())));

        // Check, that the count call also returns 1
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEnrollmentShouldNotBeFound(String filter) throws Exception {
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEnrollment() throws Exception {
        // Get the enrollment
        restEnrollmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEnrollment() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the enrollment
        Enrollment updatedEnrollment = enrollmentRepository.findById(enrollment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEnrollment are not directly saved in db
        em.detach(updatedEnrollment);
        updatedEnrollment
            .enrollmentDate(UPDATED_ENROLLMENT_DATE)
            .progressPercentage(UPDATED_PROGRESS_PERCENTAGE)
            .progress(UPDATED_PROGRESS)
            .lastAccessedAt(UPDATED_LAST_ACCESSED_AT)
            .status(UPDATED_STATUS)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .transactionId(UPDATED_TRANSACTION_ID)
            .completedAt(UPDATED_COMPLETED_AT);
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(updatedEnrollment);

        restEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, enrollmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(enrollmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEnrollmentToMatchAllProperties(updatedEnrollment);
    }

    @Test
    @Transactional
    void putNonExistingEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, enrollmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(enrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(enrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(enrollmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnrollmentWithPatch() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the enrollment using partial update
        Enrollment partialUpdatedEnrollment = new Enrollment();
        partialUpdatedEnrollment.setId(enrollment.getId());

        partialUpdatedEnrollment
            .enrollmentDate(UPDATED_ENROLLMENT_DATE)
            .progress(UPDATED_PROGRESS)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .transactionId(UPDATED_TRANSACTION_ID);

        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnrollment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnrollment))
            )
            .andExpect(status().isOk());

        // Validate the Enrollment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnrollmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEnrollment, enrollment),
            getPersistedEnrollment(enrollment)
        );
    }

    @Test
    @Transactional
    void fullUpdateEnrollmentWithPatch() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the enrollment using partial update
        Enrollment partialUpdatedEnrollment = new Enrollment();
        partialUpdatedEnrollment.setId(enrollment.getId());

        partialUpdatedEnrollment
            .enrollmentDate(UPDATED_ENROLLMENT_DATE)
            .progressPercentage(UPDATED_PROGRESS_PERCENTAGE)
            .progress(UPDATED_PROGRESS)
            .lastAccessedAt(UPDATED_LAST_ACCESSED_AT)
            .status(UPDATED_STATUS)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .transactionId(UPDATED_TRANSACTION_ID)
            .completedAt(UPDATED_COMPLETED_AT);

        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnrollment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnrollment))
            )
            .andExpect(status().isOk());

        // Validate the Enrollment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnrollmentUpdatableFieldsEquals(partialUpdatedEnrollment, getPersistedEnrollment(partialUpdatedEnrollment));
    }

    @Test
    @Transactional
    void patchNonExistingEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, enrollmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(enrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(enrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(enrollmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEnrollment() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the enrollment
        restEnrollmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, enrollment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return enrollmentRepository.count();
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

    protected Enrollment getPersistedEnrollment(Enrollment enrollment) {
        return enrollmentRepository.findById(enrollment.getId()).orElseThrow();
    }

    protected void assertPersistedEnrollmentToMatchAllProperties(Enrollment expectedEnrollment) {
        assertEnrollmentAllPropertiesEquals(expectedEnrollment, getPersistedEnrollment(expectedEnrollment));
    }

    protected void assertPersistedEnrollmentToMatchUpdatableProperties(Enrollment expectedEnrollment) {
        assertEnrollmentAllUpdatablePropertiesEquals(expectedEnrollment, getPersistedEnrollment(expectedEnrollment));
    }
}

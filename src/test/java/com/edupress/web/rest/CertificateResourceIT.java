package com.edupress.web.rest;

import static com.edupress.domain.CertificateAsserts.*;
import static com.edupress.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edupress.IntegrationTest;
import com.edupress.domain.Certificate;
import com.edupress.domain.Enrollment;
import com.edupress.domain.enumeration.CertificateStatus;
import com.edupress.repository.CertificateRepository;
import com.edupress.service.dto.CertificateDTO;
import com.edupress.service.mapper.CertificateMapper;
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
 * Integration tests for the {@link CertificateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CertificateResourceIT {

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_ISSUED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ISSUED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final CertificateStatus DEFAULT_STATUS = CertificateStatus.GENERATED;
    private static final CertificateStatus UPDATED_STATUS = CertificateStatus.REVOKED;

    private static final String ENTITY_API_URL = "/api/certificates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateMapper certificateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCertificateMockMvc;

    private Certificate certificate;

    private Certificate insertedCertificate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certificate createEntity() {
        return new Certificate().url(DEFAULT_URL).issuedAt(DEFAULT_ISSUED_AT).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certificate createUpdatedEntity() {
        return new Certificate().url(UPDATED_URL).issuedAt(UPDATED_ISSUED_AT).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        certificate = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCertificate != null) {
            certificateRepository.delete(insertedCertificate);
            insertedCertificate = null;
        }
    }

    @Test
    @Transactional
    void createCertificate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);
        var returnedCertificateDTO = om.readValue(
            restCertificateMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CertificateDTO.class
        );

        // Validate the Certificate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCertificate = certificateMapper.toEntity(returnedCertificateDTO);
        assertCertificateUpdatableFieldsEquals(returnedCertificate, getPersistedCertificate(returnedCertificate));

        insertedCertificate = returnedCertificate;
    }

    @Test
    @Transactional
    void createCertificateWithExistingId() throws Exception {
        // Create the Certificate with an existing ID
        certificate.setId(1L);
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCertificateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        certificate.setUrl(null);

        // Create the Certificate, which fails.
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        restCertificateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIssuedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        certificate.setIssuedAt(null);

        // Create the Certificate, which fails.
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        restCertificateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        certificate.setStatus(null);

        // Create the Certificate, which fails.
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        restCertificateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCertificates() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(certificate.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].issuedAt").value(hasItem(DEFAULT_ISSUED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getCertificate() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get the certificate
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL_ID, certificate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(certificate.getId().intValue()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.issuedAt").value(DEFAULT_ISSUED_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getCertificatesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        Long id = certificate.getId();

        defaultCertificateFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCertificateFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCertificateFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCertificatesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where url equals to
        defaultCertificateFiltering("url.equals=" + DEFAULT_URL, "url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllCertificatesByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where url in
        defaultCertificateFiltering("url.in=" + DEFAULT_URL + "," + UPDATED_URL, "url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllCertificatesByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where url is not null
        defaultCertificateFiltering("url.specified=true", "url.specified=false");
    }

    @Test
    @Transactional
    void getAllCertificatesByUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where url contains
        defaultCertificateFiltering("url.contains=" + DEFAULT_URL, "url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllCertificatesByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where url does not contain
        defaultCertificateFiltering("url.doesNotContain=" + UPDATED_URL, "url.doesNotContain=" + DEFAULT_URL);
    }

    @Test
    @Transactional
    void getAllCertificatesByIssuedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where issuedAt equals to
        defaultCertificateFiltering("issuedAt.equals=" + DEFAULT_ISSUED_AT, "issuedAt.equals=" + UPDATED_ISSUED_AT);
    }

    @Test
    @Transactional
    void getAllCertificatesByIssuedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where issuedAt in
        defaultCertificateFiltering("issuedAt.in=" + DEFAULT_ISSUED_AT + "," + UPDATED_ISSUED_AT, "issuedAt.in=" + UPDATED_ISSUED_AT);
    }

    @Test
    @Transactional
    void getAllCertificatesByIssuedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where issuedAt is not null
        defaultCertificateFiltering("issuedAt.specified=true", "issuedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCertificatesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where status equals to
        defaultCertificateFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCertificatesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where status in
        defaultCertificateFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCertificatesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where status is not null
        defaultCertificateFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllCertificatesByEnrollmentIsEqualToSomething() throws Exception {
        Enrollment enrollment;
        if (TestUtil.findAll(em, Enrollment.class).isEmpty()) {
            certificateRepository.saveAndFlush(certificate);
            enrollment = EnrollmentResourceIT.createEntity();
        } else {
            enrollment = TestUtil.findAll(em, Enrollment.class).get(0);
        }
        em.persist(enrollment);
        em.flush();
        certificate.setEnrollment(enrollment);
        certificateRepository.saveAndFlush(certificate);
        Long enrollmentId = enrollment.getId();
        // Get all the certificateList where enrollment equals to enrollmentId
        defaultCertificateShouldBeFound("enrollmentId.equals=" + enrollmentId);

        // Get all the certificateList where enrollment equals to (enrollmentId + 1)
        defaultCertificateShouldNotBeFound("enrollmentId.equals=" + (enrollmentId + 1));
    }

    private void defaultCertificateFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCertificateShouldBeFound(shouldBeFound);
        defaultCertificateShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCertificateShouldBeFound(String filter) throws Exception {
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(certificate.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].issuedAt").value(hasItem(DEFAULT_ISSUED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCertificateShouldNotBeFound(String filter) throws Exception {
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCertificate() throws Exception {
        // Get the certificate
        restCertificateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCertificate() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificate
        Certificate updatedCertificate = certificateRepository.findById(certificate.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCertificate are not directly saved in db
        em.detach(updatedCertificate);
        updatedCertificate.url(UPDATED_URL).issuedAt(UPDATED_ISSUED_AT).status(UPDATED_STATUS);
        CertificateDTO certificateDTO = certificateMapper.toDto(updatedCertificate);

        restCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, certificateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificateDTO))
            )
            .andExpect(status().isOk());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCertificateToMatchAllProperties(updatedCertificate);
    }

    @Test
    @Transactional
    void putNonExistingCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, certificateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCertificateWithPatch() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificate using partial update
        Certificate partialUpdatedCertificate = new Certificate();
        partialUpdatedCertificate.setId(certificate.getId());

        partialUpdatedCertificate.status(UPDATED_STATUS);

        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertificate))
            )
            .andExpect(status().isOk());

        // Validate the Certificate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCertificate, certificate),
            getPersistedCertificate(certificate)
        );
    }

    @Test
    @Transactional
    void fullUpdateCertificateWithPatch() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificate using partial update
        Certificate partialUpdatedCertificate = new Certificate();
        partialUpdatedCertificate.setId(certificate.getId());

        partialUpdatedCertificate.url(UPDATED_URL).issuedAt(UPDATED_ISSUED_AT).status(UPDATED_STATUS);

        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertificate))
            )
            .andExpect(status().isOk());

        // Validate the Certificate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificateUpdatableFieldsEquals(partialUpdatedCertificate, getPersistedCertificate(partialUpdatedCertificate));
    }

    @Test
    @Transactional
    void patchNonExistingCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, certificateDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certificateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certificateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(certificateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCertificate() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the certificate
        restCertificateMockMvc
            .perform(delete(ENTITY_API_URL_ID, certificate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return certificateRepository.count();
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

    protected Certificate getPersistedCertificate(Certificate certificate) {
        return certificateRepository.findById(certificate.getId()).orElseThrow();
    }

    protected void assertPersistedCertificateToMatchAllProperties(Certificate expectedCertificate) {
        assertCertificateAllPropertiesEquals(expectedCertificate, getPersistedCertificate(expectedCertificate));
    }

    protected void assertPersistedCertificateToMatchUpdatableProperties(Certificate expectedCertificate) {
        assertCertificateAllUpdatablePropertiesEquals(expectedCertificate, getPersistedCertificate(expectedCertificate));
    }
}

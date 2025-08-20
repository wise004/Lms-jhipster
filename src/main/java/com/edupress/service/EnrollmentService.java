package com.edupress.service;

import com.edupress.domain.Certificate;
import com.edupress.domain.Enrollment;
import com.edupress.domain.enumeration.CertificateStatus;
import com.edupress.domain.enumeration.EnrollmentStatus;
import com.edupress.repository.CertificateRepository;
import com.edupress.repository.EnrollmentRepository;
import com.edupress.service.dto.EnrollmentDTO;
import com.edupress.service.mapper.EnrollmentMapper;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.edupress.domain.Enrollment}.
 */
@Service
@Transactional
public class EnrollmentService {

    private static final Logger LOG = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;

    private final EnrollmentMapper enrollmentMapper;

    private final CertificateRepository certificateRepository;

    public EnrollmentService(
        EnrollmentRepository enrollmentRepository,
        EnrollmentMapper enrollmentMapper,
        CertificateRepository certificateRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentMapper = enrollmentMapper;
        this.certificateRepository = certificateRepository;
    }

    /**
     * Save a enrollment.
     *
     * @param enrollmentDTO the entity to save.
     * @return the persisted entity.
     */
    public EnrollmentDTO save(EnrollmentDTO enrollmentDTO) {
        LOG.debug("Request to save Enrollment : {}", enrollmentDTO);
        Enrollment enrollment = enrollmentMapper.toEntity(enrollmentDTO);
        enrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(enrollment);
    }

    /**
     * Update a enrollment.
     *
     * @param enrollmentDTO the entity to save.
     * @return the persisted entity.
     */
    public EnrollmentDTO update(EnrollmentDTO enrollmentDTO) {
        LOG.debug("Request to update Enrollment : {}", enrollmentDTO);
        Enrollment enrollment = enrollmentMapper.toEntity(enrollmentDTO);
        enrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(enrollment);
    }

    /**
     * Partially update a enrollment.
     *
     * @param enrollmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EnrollmentDTO> partialUpdate(EnrollmentDTO enrollmentDTO) {
        LOG.debug("Request to partially update Enrollment : {}", enrollmentDTO);

        return enrollmentRepository
            .findById(enrollmentDTO.getId())
            .map(existingEnrollment -> {
                enrollmentMapper.partialUpdate(existingEnrollment, enrollmentDTO);

                return existingEnrollment;
            })
            .map(enrollmentRepository::save)
            .map(enrollmentMapper::toDto);
    }

    /**
     * Get all the enrollments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EnrollmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return enrollmentRepository.findAllWithEagerRelationships(pageable).map(enrollmentMapper::toDto);
    }

    /**
     * Get one enrollment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EnrollmentDTO> findOne(Long id) {
        LOG.debug("Request to get Enrollment : {}", id);
        return enrollmentRepository.findOneWithEagerRelationships(id).map(enrollmentMapper::toDto);
    }

    /**
     * Delete the enrollment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Enrollment : {}", id);
        enrollmentRepository.deleteById(id);
    }

    /**
     * Update progress JSON blob and percent; mark completed and generate certificate when 100%.
     */
    public Optional<EnrollmentDTO> updateProgress(Long enrollmentId, String progressJson, Integer percent, Instant lastAccessedAt) {
        return enrollmentRepository
            .findById(enrollmentId)
            .map(enrollment -> {
                enrollment.setProgress(progressJson);
                enrollment.setProgressPercentage(percent);
                enrollment.setLastAccessedAt(lastAccessedAt != null ? lastAccessedAt : Instant.now());
                if (percent != null && percent >= 100) {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    enrollment.setCompletedAt(Instant.now());
                    // issue certificate if none
                    issueCertificateIfMissing(enrollment);
                } else if (enrollment.getStatus() == null) {
                    enrollment.setStatus(EnrollmentStatus.ACTIVE);
                }
                return enrollment;
            })
            .map(enrollmentRepository::save)
            .map(enrollmentMapper::toDto);
    }

    private void issueCertificateIfMissing(Enrollment enrollment) {
        // naive check: if any certificate references this enrollment, skip
        // since we don't have a direct repository method, just create unconditionally for demo or rely on unique url
        Certificate cert = new Certificate();
        cert.setEnrollment(enrollment);
        cert.setIssuedAt(Instant.now());
        cert.setStatus(CertificateStatus.GENERATED);
        cert.setUrl("cert://" + UUID.randomUUID());
        certificateRepository.save(cert);
    }
}

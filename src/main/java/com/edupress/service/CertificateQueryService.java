package com.edupress.service;

import com.edupress.domain.*; // for static metamodels
import com.edupress.domain.Certificate;
import com.edupress.repository.CertificateRepository;
import com.edupress.service.criteria.CertificateCriteria;
import com.edupress.service.dto.CertificateDTO;
import com.edupress.service.mapper.CertificateMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Certificate} entities in the database.
 * The main input is a {@link CertificateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CertificateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CertificateQueryService extends QueryService<Certificate> {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateQueryService.class);

    private final CertificateRepository certificateRepository;

    private final CertificateMapper certificateMapper;

    public CertificateQueryService(CertificateRepository certificateRepository, CertificateMapper certificateMapper) {
        this.certificateRepository = certificateRepository;
        this.certificateMapper = certificateMapper;
    }

    /**
     * Return a {@link Page} of {@link CertificateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CertificateDTO> findByCriteria(CertificateCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Certificate> specification = createSpecification(criteria);
        return certificateRepository.findAll(specification, page).map(certificateMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CertificateCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Certificate> specification = createSpecification(criteria);
        return certificateRepository.count(specification);
    }

    /**
     * Function to convert {@link CertificateCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Certificate> createSpecification(CertificateCriteria criteria) {
        Specification<Certificate> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Certificate_.id),
                buildStringSpecification(criteria.getUrl(), Certificate_.url),
                buildRangeSpecification(criteria.getIssuedAt(), Certificate_.issuedAt),
                buildSpecification(criteria.getStatus(), Certificate_.status),
                buildSpecification(criteria.getEnrollmentId(), root -> root.join(Certificate_.enrollment, JoinType.LEFT).get(Enrollment_.id)
                )
            );
        }
        return specification;
    }
}

package com.edupress.service;

import com.edupress.domain.*; // for static metamodels
import com.edupress.domain.AssignmentSubmission;
import com.edupress.repository.AssignmentSubmissionRepository;
import com.edupress.service.criteria.AssignmentSubmissionCriteria;
import com.edupress.service.dto.AssignmentSubmissionDTO;
import com.edupress.service.mapper.AssignmentSubmissionMapper;
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
 * Service for executing complex queries for {@link AssignmentSubmission} entities in the database.
 * The main input is a {@link AssignmentSubmissionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AssignmentSubmissionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AssignmentSubmissionQueryService extends QueryService<AssignmentSubmission> {

    private static final Logger LOG = LoggerFactory.getLogger(AssignmentSubmissionQueryService.class);

    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    private final AssignmentSubmissionMapper assignmentSubmissionMapper;

    public AssignmentSubmissionQueryService(
        AssignmentSubmissionRepository assignmentSubmissionRepository,
        AssignmentSubmissionMapper assignmentSubmissionMapper
    ) {
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.assignmentSubmissionMapper = assignmentSubmissionMapper;
    }

    /**
     * Return a {@link Page} of {@link AssignmentSubmissionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AssignmentSubmissionDTO> findByCriteria(AssignmentSubmissionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AssignmentSubmission> specification = createSpecification(criteria);
        return assignmentSubmissionRepository.findAll(specification, page).map(assignmentSubmissionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AssignmentSubmissionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AssignmentSubmission> specification = createSpecification(criteria);
        return assignmentSubmissionRepository.count(specification);
    }

    /**
     * Function to convert {@link AssignmentSubmissionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AssignmentSubmission> createSpecification(AssignmentSubmissionCriteria criteria) {
        Specification<AssignmentSubmission> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), AssignmentSubmission_.id),
                buildRangeSpecification(criteria.getSubmittedAt(), AssignmentSubmission_.submittedAt),
                buildRangeSpecification(criteria.getGrade(), AssignmentSubmission_.grade),
                buildSpecification(criteria.getStatus(), AssignmentSubmission_.status),
                buildSpecification(criteria.getAssignmentId(), root ->
                    root.join(AssignmentSubmission_.assignment, JoinType.LEFT).get(Assignment_.id)
                ),
                buildSpecification(criteria.getStudentId(), root -> root.join(AssignmentSubmission_.student, JoinType.LEFT).get(AppUser_.id)
                )
            );
        }
        return specification;
    }
}

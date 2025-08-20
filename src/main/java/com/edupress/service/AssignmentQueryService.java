package com.edupress.service;

import com.edupress.domain.*; // for static metamodels
import com.edupress.domain.Assignment;
import com.edupress.repository.AssignmentRepository;
import com.edupress.service.criteria.AssignmentCriteria;
import com.edupress.service.dto.AssignmentDTO;
import com.edupress.service.mapper.AssignmentMapper;
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
 * Service for executing complex queries for {@link Assignment} entities in the database.
 * The main input is a {@link AssignmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AssignmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AssignmentQueryService extends QueryService<Assignment> {

    private static final Logger LOG = LoggerFactory.getLogger(AssignmentQueryService.class);

    private final AssignmentRepository assignmentRepository;

    private final AssignmentMapper assignmentMapper;

    public AssignmentQueryService(AssignmentRepository assignmentRepository, AssignmentMapper assignmentMapper) {
        this.assignmentRepository = assignmentRepository;
        this.assignmentMapper = assignmentMapper;
    }

    /**
     * Return a {@link Page} of {@link AssignmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AssignmentDTO> findByCriteria(AssignmentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Assignment> specification = createSpecification(criteria);
        return assignmentRepository.findAll(specification, page).map(assignmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AssignmentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Assignment> specification = createSpecification(criteria);
        return assignmentRepository.count(specification);
    }

    /**
     * Function to convert {@link AssignmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Assignment> createSpecification(AssignmentCriteria criteria) {
        Specification<Assignment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Assignment_.id),
                buildStringSpecification(criteria.getTitle(), Assignment_.title),
                buildRangeSpecification(criteria.getDueDate(), Assignment_.dueDate),
                buildRangeSpecification(criteria.getMaxPoints(), Assignment_.maxPoints),
                buildStringSpecification(criteria.getSubmissionType(), Assignment_.submissionType),
                buildRangeSpecification(criteria.getMaxFileSize(), Assignment_.maxFileSize),
                buildSpecification(criteria.getIsPublished(), Assignment_.isPublished),
                buildSpecification(criteria.getAllowLateSubmission(), Assignment_.allowLateSubmission),
                buildRangeSpecification(criteria.getLateSubmissionPenalty(), Assignment_.lateSubmissionPenalty),
                buildRangeSpecification(criteria.getSortOrder(), Assignment_.sortOrder),
                buildSpecification(criteria.getCourseId(), root -> root.join(Assignment_.course, JoinType.LEFT).get(Course_.id)),
                buildSpecification(criteria.getLessonId(), root -> root.join(Assignment_.lesson, JoinType.LEFT).get(Lesson_.id))
            );
        }
        return specification;
    }
}

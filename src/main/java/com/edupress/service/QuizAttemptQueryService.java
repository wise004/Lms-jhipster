package com.edupress.service;

import com.edupress.domain.*; // for static metamodels
import com.edupress.domain.QuizAttempt;
import com.edupress.repository.QuizAttemptRepository;
import com.edupress.service.criteria.QuizAttemptCriteria;
import com.edupress.service.dto.QuizAttemptDTO;
import com.edupress.service.mapper.QuizAttemptMapper;
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
 * Service for executing complex queries for {@link QuizAttempt} entities in the database.
 * The main input is a {@link QuizAttemptCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link QuizAttemptDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuizAttemptQueryService extends QueryService<QuizAttempt> {

    private static final Logger LOG = LoggerFactory.getLogger(QuizAttemptQueryService.class);

    private final QuizAttemptRepository quizAttemptRepository;

    private final QuizAttemptMapper quizAttemptMapper;

    public QuizAttemptQueryService(QuizAttemptRepository quizAttemptRepository, QuizAttemptMapper quizAttemptMapper) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizAttemptMapper = quizAttemptMapper;
    }

    /**
     * Return a {@link Page} of {@link QuizAttemptDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<QuizAttemptDTO> findByCriteria(QuizAttemptCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<QuizAttempt> specification = createSpecification(criteria);
        return quizAttemptRepository.findAll(specification, page).map(quizAttemptMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(QuizAttemptCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<QuizAttempt> specification = createSpecification(criteria);
        return quizAttemptRepository.count(specification);
    }

    /**
     * Function to convert {@link QuizAttemptCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<QuizAttempt> createSpecification(QuizAttemptCriteria criteria) {
        Specification<QuizAttempt> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), QuizAttempt_.id),
                buildRangeSpecification(criteria.getStartedAt(), QuizAttempt_.startedAt),
                buildRangeSpecification(criteria.getSubmittedAt(), QuizAttempt_.submittedAt),
                buildRangeSpecification(criteria.getScore(), QuizAttempt_.score),
                buildSpecification(criteria.getPassed(), QuizAttempt_.passed),
                buildRangeSpecification(criteria.getAttemptNumber(), QuizAttempt_.attemptNumber),
                buildSpecification(criteria.getStatus(), QuizAttempt_.status),
                buildSpecification(criteria.getQuizId(), root -> root.join(QuizAttempt_.quiz, JoinType.LEFT).get(Quiz_.id)),
                buildSpecification(criteria.getStudentId(), root -> root.join(QuizAttempt_.student, JoinType.LEFT).get(AppUser_.id))
            );
        }
        return specification;
    }
}

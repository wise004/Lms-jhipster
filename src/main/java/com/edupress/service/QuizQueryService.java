package com.edupress.service;

import com.edupress.domain.*; // for static metamodels
import com.edupress.domain.Quiz;
import com.edupress.repository.QuizRepository;
import com.edupress.service.criteria.QuizCriteria;
import com.edupress.service.dto.QuizDTO;
import com.edupress.service.mapper.QuizMapper;
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
 * Service for executing complex queries for {@link Quiz} entities in the database.
 * The main input is a {@link QuizCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link QuizDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuizQueryService extends QueryService<Quiz> {

    private static final Logger LOG = LoggerFactory.getLogger(QuizQueryService.class);

    private final QuizRepository quizRepository;

    private final QuizMapper quizMapper;

    public QuizQueryService(QuizRepository quizRepository, QuizMapper quizMapper) {
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
    }

    /**
     * Return a {@link Page} of {@link QuizDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<QuizDTO> findByCriteria(QuizCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Quiz> specification = createSpecification(criteria);
        return quizRepository.findAll(specification, page).map(quizMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(QuizCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Quiz> specification = createSpecification(criteria);
        return quizRepository.count(specification);
    }

    /**
     * Function to convert {@link QuizCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Quiz> createSpecification(QuizCriteria criteria) {
        Specification<Quiz> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Quiz_.id),
                buildStringSpecification(criteria.getTitle(), Quiz_.title),
                buildRangeSpecification(criteria.getTimeLimit(), Quiz_.timeLimit),
                buildRangeSpecification(criteria.getPassingScore(), Quiz_.passingScore),
                buildRangeSpecification(criteria.getAttemptsAllowed(), Quiz_.attemptsAllowed),
                buildRangeSpecification(criteria.getSortOrder(), Quiz_.sortOrder),
                buildSpecification(criteria.getIsPublished(), Quiz_.isPublished),
                buildRangeSpecification(criteria.getAvailableFrom(), Quiz_.availableFrom),
                buildRangeSpecification(criteria.getAvailableUntil(), Quiz_.availableUntil),
                buildSpecification(criteria.getCourseId(), root -> root.join(Quiz_.course, JoinType.LEFT).get(Course_.id)),
                buildSpecification(criteria.getLessonId(), root -> root.join(Quiz_.lesson, JoinType.LEFT).get(Lesson_.id))
            );
        }
        return specification;
    }
}

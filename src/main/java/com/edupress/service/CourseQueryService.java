package com.edupress.service;

import com.edupress.domain.*; // for static metamodels
import com.edupress.domain.Course;
import com.edupress.repository.CourseRepository;
import com.edupress.service.criteria.CourseCriteria;
import com.edupress.service.dto.CourseDTO;
import com.edupress.service.mapper.CourseMapper;
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
 * Service for executing complex queries for {@link Course} entities in the database.
 * The main input is a {@link CourseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CourseDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CourseQueryService extends QueryService<Course> {

    private static final Logger LOG = LoggerFactory.getLogger(CourseQueryService.class);

    private final CourseRepository courseRepository;

    private final CourseMapper courseMapper;

    public CourseQueryService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    /**
     * Return a {@link Page} of {@link CourseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseDTO> findByCriteria(CourseCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Course> specification = createSpecification(criteria);
        return courseRepository.findAll(specification, page).map(courseMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CourseCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Course> specification = createSpecification(criteria);
        return courseRepository.count(specification);
    }

    /**
     * Function to convert {@link CourseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Course> createSpecification(CourseCriteria criteria) {
        Specification<Course> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Course_.id),
                buildStringSpecification(criteria.getTitle(), Course_.title),
                buildStringSpecification(criteria.getSlug(), Course_.slug),
                buildStringSpecification(criteria.getShortDescription(), Course_.shortDescription),
                buildStringSpecification(criteria.getThumbnailUrl(), Course_.thumbnailUrl),
                buildRangeSpecification(criteria.getPrice(), Course_.price),
                buildRangeSpecification(criteria.getOriginalPrice(), Course_.originalPrice),
                buildStringSpecification(criteria.getLevel(), Course_.level),
                buildStringSpecification(criteria.getLanguage(), Course_.language),
                buildSpecification(criteria.getStatus(), Course_.status),
                buildSpecification(criteria.getIsPublished(), Course_.isPublished),
                buildSpecification(criteria.getIsFeatured(), Course_.isFeatured),
                buildRangeSpecification(criteria.getAverageRating(), Course_.averageRating),
                buildRangeSpecification(criteria.getEnrollmentCount(), Course_.enrollmentCount),
                buildSpecification(criteria.getInstructorId(), root -> root.join(Course_.instructor, JoinType.LEFT).get(AppUser_.id)),
                buildSpecification(criteria.getCategoryId(), root -> root.join(Course_.category, JoinType.LEFT).get(Category_.id))
            );
        }
        return specification;
    }
}

package com.edupress.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AssignmentCriteriaTest {

    @Test
    void newAssignmentCriteriaHasAllFiltersNullTest() {
        var assignmentCriteria = new AssignmentCriteria();
        assertThat(assignmentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void assignmentCriteriaFluentMethodsCreatesFiltersTest() {
        var assignmentCriteria = new AssignmentCriteria();

        setAllFilters(assignmentCriteria);

        assertThat(assignmentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void assignmentCriteriaCopyCreatesNullFilterTest() {
        var assignmentCriteria = new AssignmentCriteria();
        var copy = assignmentCriteria.copy();

        assertThat(assignmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(assignmentCriteria)
        );
    }

    @Test
    void assignmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var assignmentCriteria = new AssignmentCriteria();
        setAllFilters(assignmentCriteria);

        var copy = assignmentCriteria.copy();

        assertThat(assignmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(assignmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var assignmentCriteria = new AssignmentCriteria();

        assertThat(assignmentCriteria).hasToString("AssignmentCriteria{}");
    }

    private static void setAllFilters(AssignmentCriteria assignmentCriteria) {
        assignmentCriteria.id();
        assignmentCriteria.title();
        assignmentCriteria.dueDate();
        assignmentCriteria.maxPoints();
        assignmentCriteria.submissionType();
        assignmentCriteria.maxFileSize();
        assignmentCriteria.isPublished();
        assignmentCriteria.allowLateSubmission();
        assignmentCriteria.lateSubmissionPenalty();
        assignmentCriteria.sortOrder();
        assignmentCriteria.courseId();
        assignmentCriteria.lessonId();
        assignmentCriteria.distinct();
    }

    private static Condition<AssignmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getDueDate()) &&
                condition.apply(criteria.getMaxPoints()) &&
                condition.apply(criteria.getSubmissionType()) &&
                condition.apply(criteria.getMaxFileSize()) &&
                condition.apply(criteria.getIsPublished()) &&
                condition.apply(criteria.getAllowLateSubmission()) &&
                condition.apply(criteria.getLateSubmissionPenalty()) &&
                condition.apply(criteria.getSortOrder()) &&
                condition.apply(criteria.getCourseId()) &&
                condition.apply(criteria.getLessonId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AssignmentCriteria> copyFiltersAre(AssignmentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getDueDate(), copy.getDueDate()) &&
                condition.apply(criteria.getMaxPoints(), copy.getMaxPoints()) &&
                condition.apply(criteria.getSubmissionType(), copy.getSubmissionType()) &&
                condition.apply(criteria.getMaxFileSize(), copy.getMaxFileSize()) &&
                condition.apply(criteria.getIsPublished(), copy.getIsPublished()) &&
                condition.apply(criteria.getAllowLateSubmission(), copy.getAllowLateSubmission()) &&
                condition.apply(criteria.getLateSubmissionPenalty(), copy.getLateSubmissionPenalty()) &&
                condition.apply(criteria.getSortOrder(), copy.getSortOrder()) &&
                condition.apply(criteria.getCourseId(), copy.getCourseId()) &&
                condition.apply(criteria.getLessonId(), copy.getLessonId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

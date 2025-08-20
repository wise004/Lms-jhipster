package com.edupress.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class QuizCriteriaTest {

    @Test
    void newQuizCriteriaHasAllFiltersNullTest() {
        var quizCriteria = new QuizCriteria();
        assertThat(quizCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void quizCriteriaFluentMethodsCreatesFiltersTest() {
        var quizCriteria = new QuizCriteria();

        setAllFilters(quizCriteria);

        assertThat(quizCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void quizCriteriaCopyCreatesNullFilterTest() {
        var quizCriteria = new QuizCriteria();
        var copy = quizCriteria.copy();

        assertThat(quizCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(quizCriteria)
        );
    }

    @Test
    void quizCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var quizCriteria = new QuizCriteria();
        setAllFilters(quizCriteria);

        var copy = quizCriteria.copy();

        assertThat(quizCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(quizCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var quizCriteria = new QuizCriteria();

        assertThat(quizCriteria).hasToString("QuizCriteria{}");
    }

    private static void setAllFilters(QuizCriteria quizCriteria) {
        quizCriteria.id();
        quizCriteria.title();
        quizCriteria.timeLimit();
        quizCriteria.passingScore();
        quizCriteria.attemptsAllowed();
        quizCriteria.sortOrder();
        quizCriteria.isPublished();
        quizCriteria.availableFrom();
        quizCriteria.availableUntil();
        quizCriteria.courseId();
        quizCriteria.lessonId();
        quizCriteria.distinct();
    }

    private static Condition<QuizCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getTimeLimit()) &&
                condition.apply(criteria.getPassingScore()) &&
                condition.apply(criteria.getAttemptsAllowed()) &&
                condition.apply(criteria.getSortOrder()) &&
                condition.apply(criteria.getIsPublished()) &&
                condition.apply(criteria.getAvailableFrom()) &&
                condition.apply(criteria.getAvailableUntil()) &&
                condition.apply(criteria.getCourseId()) &&
                condition.apply(criteria.getLessonId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<QuizCriteria> copyFiltersAre(QuizCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getTimeLimit(), copy.getTimeLimit()) &&
                condition.apply(criteria.getPassingScore(), copy.getPassingScore()) &&
                condition.apply(criteria.getAttemptsAllowed(), copy.getAttemptsAllowed()) &&
                condition.apply(criteria.getSortOrder(), copy.getSortOrder()) &&
                condition.apply(criteria.getIsPublished(), copy.getIsPublished()) &&
                condition.apply(criteria.getAvailableFrom(), copy.getAvailableFrom()) &&
                condition.apply(criteria.getAvailableUntil(), copy.getAvailableUntil()) &&
                condition.apply(criteria.getCourseId(), copy.getCourseId()) &&
                condition.apply(criteria.getLessonId(), copy.getLessonId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

package com.edupress.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class QuizAttemptCriteriaTest {

    @Test
    void newQuizAttemptCriteriaHasAllFiltersNullTest() {
        var quizAttemptCriteria = new QuizAttemptCriteria();
        assertThat(quizAttemptCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void quizAttemptCriteriaFluentMethodsCreatesFiltersTest() {
        var quizAttemptCriteria = new QuizAttemptCriteria();

        setAllFilters(quizAttemptCriteria);

        assertThat(quizAttemptCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void quizAttemptCriteriaCopyCreatesNullFilterTest() {
        var quizAttemptCriteria = new QuizAttemptCriteria();
        var copy = quizAttemptCriteria.copy();

        assertThat(quizAttemptCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(quizAttemptCriteria)
        );
    }

    @Test
    void quizAttemptCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var quizAttemptCriteria = new QuizAttemptCriteria();
        setAllFilters(quizAttemptCriteria);

        var copy = quizAttemptCriteria.copy();

        assertThat(quizAttemptCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(quizAttemptCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var quizAttemptCriteria = new QuizAttemptCriteria();

        assertThat(quizAttemptCriteria).hasToString("QuizAttemptCriteria{}");
    }

    private static void setAllFilters(QuizAttemptCriteria quizAttemptCriteria) {
        quizAttemptCriteria.id();
        quizAttemptCriteria.startedAt();
        quizAttemptCriteria.submittedAt();
        quizAttemptCriteria.score();
        quizAttemptCriteria.passed();
        quizAttemptCriteria.attemptNumber();
        quizAttemptCriteria.status();
        quizAttemptCriteria.quizId();
        quizAttemptCriteria.studentId();
        quizAttemptCriteria.distinct();
    }

    private static Condition<QuizAttemptCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStartedAt()) &&
                condition.apply(criteria.getSubmittedAt()) &&
                condition.apply(criteria.getScore()) &&
                condition.apply(criteria.getPassed()) &&
                condition.apply(criteria.getAttemptNumber()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getQuizId()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<QuizAttemptCriteria> copyFiltersAre(QuizAttemptCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStartedAt(), copy.getStartedAt()) &&
                condition.apply(criteria.getSubmittedAt(), copy.getSubmittedAt()) &&
                condition.apply(criteria.getScore(), copy.getScore()) &&
                condition.apply(criteria.getPassed(), copy.getPassed()) &&
                condition.apply(criteria.getAttemptNumber(), copy.getAttemptNumber()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getQuizId(), copy.getQuizId()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

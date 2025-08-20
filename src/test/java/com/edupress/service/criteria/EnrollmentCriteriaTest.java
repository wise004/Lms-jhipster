package com.edupress.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EnrollmentCriteriaTest {

    @Test
    void newEnrollmentCriteriaHasAllFiltersNullTest() {
        var enrollmentCriteria = new EnrollmentCriteria();
        assertThat(enrollmentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void enrollmentCriteriaFluentMethodsCreatesFiltersTest() {
        var enrollmentCriteria = new EnrollmentCriteria();

        setAllFilters(enrollmentCriteria);

        assertThat(enrollmentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void enrollmentCriteriaCopyCreatesNullFilterTest() {
        var enrollmentCriteria = new EnrollmentCriteria();
        var copy = enrollmentCriteria.copy();

        assertThat(enrollmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(enrollmentCriteria)
        );
    }

    @Test
    void enrollmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var enrollmentCriteria = new EnrollmentCriteria();
        setAllFilters(enrollmentCriteria);

        var copy = enrollmentCriteria.copy();

        assertThat(enrollmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(enrollmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var enrollmentCriteria = new EnrollmentCriteria();

        assertThat(enrollmentCriteria).hasToString("EnrollmentCriteria{}");
    }

    private static void setAllFilters(EnrollmentCriteria enrollmentCriteria) {
        enrollmentCriteria.id();
        enrollmentCriteria.enrollmentDate();
        enrollmentCriteria.progressPercentage();
        enrollmentCriteria.lastAccessedAt();
        enrollmentCriteria.status();
        enrollmentCriteria.paymentStatus();
        enrollmentCriteria.amountPaid();
        enrollmentCriteria.transactionId();
        enrollmentCriteria.completedAt();
        enrollmentCriteria.courseId();
        enrollmentCriteria.studentId();
        enrollmentCriteria.distinct();
    }

    private static Condition<EnrollmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEnrollmentDate()) &&
                condition.apply(criteria.getProgressPercentage()) &&
                condition.apply(criteria.getLastAccessedAt()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getPaymentStatus()) &&
                condition.apply(criteria.getAmountPaid()) &&
                condition.apply(criteria.getTransactionId()) &&
                condition.apply(criteria.getCompletedAt()) &&
                condition.apply(criteria.getCourseId()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EnrollmentCriteria> copyFiltersAre(EnrollmentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEnrollmentDate(), copy.getEnrollmentDate()) &&
                condition.apply(criteria.getProgressPercentage(), copy.getProgressPercentage()) &&
                condition.apply(criteria.getLastAccessedAt(), copy.getLastAccessedAt()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getPaymentStatus(), copy.getPaymentStatus()) &&
                condition.apply(criteria.getAmountPaid(), copy.getAmountPaid()) &&
                condition.apply(criteria.getTransactionId(), copy.getTransactionId()) &&
                condition.apply(criteria.getCompletedAt(), copy.getCompletedAt()) &&
                condition.apply(criteria.getCourseId(), copy.getCourseId()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

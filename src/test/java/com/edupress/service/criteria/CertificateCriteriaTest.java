package com.edupress.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CertificateCriteriaTest {

    @Test
    void newCertificateCriteriaHasAllFiltersNullTest() {
        var certificateCriteria = new CertificateCriteria();
        assertThat(certificateCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void certificateCriteriaFluentMethodsCreatesFiltersTest() {
        var certificateCriteria = new CertificateCriteria();

        setAllFilters(certificateCriteria);

        assertThat(certificateCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void certificateCriteriaCopyCreatesNullFilterTest() {
        var certificateCriteria = new CertificateCriteria();
        var copy = certificateCriteria.copy();

        assertThat(certificateCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(certificateCriteria)
        );
    }

    @Test
    void certificateCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var certificateCriteria = new CertificateCriteria();
        setAllFilters(certificateCriteria);

        var copy = certificateCriteria.copy();

        assertThat(certificateCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(certificateCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var certificateCriteria = new CertificateCriteria();

        assertThat(certificateCriteria).hasToString("CertificateCriteria{}");
    }

    private static void setAllFilters(CertificateCriteria certificateCriteria) {
        certificateCriteria.id();
        certificateCriteria.url();
        certificateCriteria.issuedAt();
        certificateCriteria.status();
        certificateCriteria.enrollmentId();
        certificateCriteria.distinct();
    }

    private static Condition<CertificateCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getUrl()) &&
                condition.apply(criteria.getIssuedAt()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getEnrollmentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CertificateCriteria> copyFiltersAre(CertificateCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getUrl(), copy.getUrl()) &&
                condition.apply(criteria.getIssuedAt(), copy.getIssuedAt()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getEnrollmentId(), copy.getEnrollmentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

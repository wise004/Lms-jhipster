package com.edupress.domain;

import static com.edupress.domain.AppUserTestSamples.*;
import static com.edupress.domain.CourseTestSamples.*;
import static com.edupress.domain.ReviewTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edupress.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Review.class);
        Review review1 = getReviewSample1();
        Review review2 = new Review();
        assertThat(review1).isNotEqualTo(review2);

        review2.setId(review1.getId());
        assertThat(review1).isEqualTo(review2);

        review2 = getReviewSample2();
        assertThat(review1).isNotEqualTo(review2);
    }

    @Test
    void courseTest() {
        Review review = getReviewRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        review.setCourse(courseBack);
        assertThat(review.getCourse()).isEqualTo(courseBack);

        review.course(null);
        assertThat(review.getCourse()).isNull();
    }

    @Test
    void studentTest() {
        Review review = getReviewRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        review.setStudent(appUserBack);
        assertThat(review.getStudent()).isEqualTo(appUserBack);

        review.student(null);
        assertThat(review.getStudent()).isNull();
    }
}

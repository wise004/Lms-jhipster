package com.edupress.domain;

import static com.edupress.domain.AppUserTestSamples.*;
import static com.edupress.domain.CategoryTestSamples.*;
import static com.edupress.domain.CourseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edupress.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Course.class);
        Course course1 = getCourseSample1();
        Course course2 = new Course();
        assertThat(course1).isNotEqualTo(course2);

        course2.setId(course1.getId());
        assertThat(course1).isEqualTo(course2);

        course2 = getCourseSample2();
        assertThat(course1).isNotEqualTo(course2);
    }

    @Test
    void instructorTest() {
        Course course = getCourseRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        course.setInstructor(appUserBack);
        assertThat(course.getInstructor()).isEqualTo(appUserBack);

        course.instructor(null);
        assertThat(course.getInstructor()).isNull();
    }

    @Test
    void categoryTest() {
        Course course = getCourseRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        course.setCategory(categoryBack);
        assertThat(course.getCategory()).isEqualTo(categoryBack);

        course.category(null);
        assertThat(course.getCategory()).isNull();
    }
}

package com.edupress.domain;

import static com.edupress.domain.CourseTestSamples.*;
import static com.edupress.domain.LessonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edupress.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LessonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Lesson.class);
        Lesson lesson1 = getLessonSample1();
        Lesson lesson2 = new Lesson();
        assertThat(lesson1).isNotEqualTo(lesson2);

        lesson2.setId(lesson1.getId());
        assertThat(lesson1).isEqualTo(lesson2);

        lesson2 = getLessonSample2();
        assertThat(lesson1).isNotEqualTo(lesson2);
    }

    @Test
    void courseTest() {
        Lesson lesson = getLessonRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        lesson.setCourse(courseBack);
        assertThat(lesson.getCourse()).isEqualTo(courseBack);

        lesson.course(null);
        assertThat(lesson.getCourse()).isNull();
    }
}

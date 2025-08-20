package com.edupress.domain;

import static com.edupress.domain.AssignmentTestSamples.*;
import static com.edupress.domain.CourseTestSamples.*;
import static com.edupress.domain.LessonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edupress.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Assignment.class);
        Assignment assignment1 = getAssignmentSample1();
        Assignment assignment2 = new Assignment();
        assertThat(assignment1).isNotEqualTo(assignment2);

        assignment2.setId(assignment1.getId());
        assertThat(assignment1).isEqualTo(assignment2);

        assignment2 = getAssignmentSample2();
        assertThat(assignment1).isNotEqualTo(assignment2);
    }

    @Test
    void courseTest() {
        Assignment assignment = getAssignmentRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        assignment.setCourse(courseBack);
        assertThat(assignment.getCourse()).isEqualTo(courseBack);

        assignment.course(null);
        assertThat(assignment.getCourse()).isNull();
    }

    @Test
    void lessonTest() {
        Assignment assignment = getAssignmentRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        assignment.setLesson(lessonBack);
        assertThat(assignment.getLesson()).isEqualTo(lessonBack);

        assignment.lesson(null);
        assertThat(assignment.getLesson()).isNull();
    }
}

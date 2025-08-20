package com.edupress.domain;

import static com.edupress.domain.CourseTestSamples.*;
import static com.edupress.domain.LessonTestSamples.*;
import static com.edupress.domain.QuizTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edupress.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuizTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Quiz.class);
        Quiz quiz1 = getQuizSample1();
        Quiz quiz2 = new Quiz();
        assertThat(quiz1).isNotEqualTo(quiz2);

        quiz2.setId(quiz1.getId());
        assertThat(quiz1).isEqualTo(quiz2);

        quiz2 = getQuizSample2();
        assertThat(quiz1).isNotEqualTo(quiz2);
    }

    @Test
    void courseTest() {
        Quiz quiz = getQuizRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        quiz.setCourse(courseBack);
        assertThat(quiz.getCourse()).isEqualTo(courseBack);

        quiz.course(null);
        assertThat(quiz.getCourse()).isNull();
    }

    @Test
    void lessonTest() {
        Quiz quiz = getQuizRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        quiz.setLesson(lessonBack);
        assertThat(quiz.getLesson()).isEqualTo(lessonBack);

        quiz.lesson(null);
        assertThat(quiz.getLesson()).isNull();
    }
}

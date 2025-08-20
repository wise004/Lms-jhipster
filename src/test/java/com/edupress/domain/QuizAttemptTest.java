package com.edupress.domain;

import static com.edupress.domain.AppUserTestSamples.*;
import static com.edupress.domain.QuizAttemptTestSamples.*;
import static com.edupress.domain.QuizTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edupress.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuizAttemptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuizAttempt.class);
        QuizAttempt quizAttempt1 = getQuizAttemptSample1();
        QuizAttempt quizAttempt2 = new QuizAttempt();
        assertThat(quizAttempt1).isNotEqualTo(quizAttempt2);

        quizAttempt2.setId(quizAttempt1.getId());
        assertThat(quizAttempt1).isEqualTo(quizAttempt2);

        quizAttempt2 = getQuizAttemptSample2();
        assertThat(quizAttempt1).isNotEqualTo(quizAttempt2);
    }

    @Test
    void quizTest() {
        QuizAttempt quizAttempt = getQuizAttemptRandomSampleGenerator();
        Quiz quizBack = getQuizRandomSampleGenerator();

        quizAttempt.setQuiz(quizBack);
        assertThat(quizAttempt.getQuiz()).isEqualTo(quizBack);

        quizAttempt.quiz(null);
        assertThat(quizAttempt.getQuiz()).isNull();
    }

    @Test
    void studentTest() {
        QuizAttempt quizAttempt = getQuizAttemptRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        quizAttempt.setStudent(appUserBack);
        assertThat(quizAttempt.getStudent()).isEqualTo(appUserBack);

        quizAttempt.student(null);
        assertThat(quizAttempt.getStudent()).isNull();
    }
}

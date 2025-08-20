package com.edupress.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.edupress.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuizAttemptDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuizAttemptDTO.class);
        QuizAttemptDTO quizAttemptDTO1 = new QuizAttemptDTO();
        quizAttemptDTO1.setId(1L);
        QuizAttemptDTO quizAttemptDTO2 = new QuizAttemptDTO();
        assertThat(quizAttemptDTO1).isNotEqualTo(quizAttemptDTO2);
        quizAttemptDTO2.setId(quizAttemptDTO1.getId());
        assertThat(quizAttemptDTO1).isEqualTo(quizAttemptDTO2);
        quizAttemptDTO2.setId(2L);
        assertThat(quizAttemptDTO1).isNotEqualTo(quizAttemptDTO2);
        quizAttemptDTO1.setId(null);
        assertThat(quizAttemptDTO1).isNotEqualTo(quizAttemptDTO2);
    }
}

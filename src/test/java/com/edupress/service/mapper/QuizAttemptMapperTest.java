package com.edupress.service.mapper;

import static com.edupress.domain.QuizAttemptAsserts.*;
import static com.edupress.domain.QuizAttemptTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuizAttemptMapperTest {

    private QuizAttemptMapper quizAttemptMapper;

    @BeforeEach
    void setUp() {
        quizAttemptMapper = new QuizAttemptMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getQuizAttemptSample1();
        var actual = quizAttemptMapper.toEntity(quizAttemptMapper.toDto(expected));
        assertQuizAttemptAllPropertiesEquals(expected, actual);
    }
}

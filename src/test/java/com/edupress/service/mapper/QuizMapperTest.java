package com.edupress.service.mapper;

import static com.edupress.domain.QuizAsserts.*;
import static com.edupress.domain.QuizTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuizMapperTest {

    private QuizMapper quizMapper;

    @BeforeEach
    void setUp() {
        quizMapper = new QuizMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getQuizSample1();
        var actual = quizMapper.toEntity(quizMapper.toDto(expected));
        assertQuizAllPropertiesEquals(expected, actual);
    }
}

package com.edupress.service.mapper;

import static com.edupress.domain.AssignmentSubmissionAsserts.*;
import static com.edupress.domain.AssignmentSubmissionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssignmentSubmissionMapperTest {

    private AssignmentSubmissionMapper assignmentSubmissionMapper;

    @BeforeEach
    void setUp() {
        assignmentSubmissionMapper = new AssignmentSubmissionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAssignmentSubmissionSample1();
        var actual = assignmentSubmissionMapper.toEntity(assignmentSubmissionMapper.toDto(expected));
        assertAssignmentSubmissionAllPropertiesEquals(expected, actual);
    }
}

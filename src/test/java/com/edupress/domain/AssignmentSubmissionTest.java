package com.edupress.domain;

import static com.edupress.domain.AppUserTestSamples.*;
import static com.edupress.domain.AssignmentSubmissionTestSamples.*;
import static com.edupress.domain.AssignmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edupress.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssignmentSubmissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssignmentSubmission.class);
        AssignmentSubmission assignmentSubmission1 = getAssignmentSubmissionSample1();
        AssignmentSubmission assignmentSubmission2 = new AssignmentSubmission();
        assertThat(assignmentSubmission1).isNotEqualTo(assignmentSubmission2);

        assignmentSubmission2.setId(assignmentSubmission1.getId());
        assertThat(assignmentSubmission1).isEqualTo(assignmentSubmission2);

        assignmentSubmission2 = getAssignmentSubmissionSample2();
        assertThat(assignmentSubmission1).isNotEqualTo(assignmentSubmission2);
    }

    @Test
    void assignmentTest() {
        AssignmentSubmission assignmentSubmission = getAssignmentSubmissionRandomSampleGenerator();
        Assignment assignmentBack = getAssignmentRandomSampleGenerator();

        assignmentSubmission.setAssignment(assignmentBack);
        assertThat(assignmentSubmission.getAssignment()).isEqualTo(assignmentBack);

        assignmentSubmission.assignment(null);
        assertThat(assignmentSubmission.getAssignment()).isNull();
    }

    @Test
    void studentTest() {
        AssignmentSubmission assignmentSubmission = getAssignmentSubmissionRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        assignmentSubmission.setStudent(appUserBack);
        assertThat(assignmentSubmission.getStudent()).isEqualTo(appUserBack);

        assignmentSubmission.student(null);
        assertThat(assignmentSubmission.getStudent()).isNull();
    }
}

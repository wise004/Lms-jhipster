package com.edupress.service.dto;

import com.edupress.domain.enumeration.SubmissionStatus;
import jakarta.persistence.Lob;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.edupress.domain.AssignmentSubmission} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentSubmissionDTO implements Serializable {

    private Long id;

    private Instant submittedAt;

    @Lob
    private String submissionText;

    @Lob
    private String fileUrls;

    private Integer grade;

    @Lob
    private String feedback;

    private SubmissionStatus status;

    private AssignmentDTO assignment;

    private AppUserDTO student;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getSubmissionText() {
        return submissionText;
    }

    public void setSubmissionText(String submissionText) {
        this.submissionText = submissionText;
    }

    public String getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(String fileUrls) {
        this.fileUrls = fileUrls;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public AssignmentDTO getAssignment() {
        return assignment;
    }

    public void setAssignment(AssignmentDTO assignment) {
        this.assignment = assignment;
    }

    public AppUserDTO getStudent() {
        return student;
    }

    public void setStudent(AppUserDTO student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssignmentSubmissionDTO)) {
            return false;
        }

        AssignmentSubmissionDTO assignmentSubmissionDTO = (AssignmentSubmissionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, assignmentSubmissionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentSubmissionDTO{" +
            "id=" + getId() +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", submissionText='" + getSubmissionText() + "'" +
            ", fileUrls='" + getFileUrls() + "'" +
            ", grade=" + getGrade() +
            ", feedback='" + getFeedback() + "'" +
            ", status='" + getStatus() + "'" +
            ", assignment=" + getAssignment() +
            ", student=" + getStudent() +
            "}";
    }
}

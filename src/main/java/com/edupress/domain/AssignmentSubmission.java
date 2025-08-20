package com.edupress.domain;

import com.edupress.domain.enumeration.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AssignmentSubmission.
 */
@Entity
@Table(name = "assignment_submission")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentSubmission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Lob
    @Column(name = "submission_text")
    private String submissionText;

    @Lob
    @Column(name = "file_urls")
    private String fileUrls;

    @Column(name = "grade")
    private Integer grade;

    @Lob
    @Column(name = "feedback")
    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubmissionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "course", "lesson" }, allowSetters = true)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser student;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AssignmentSubmission id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public AssignmentSubmission submittedAt(Instant submittedAt) {
        this.setSubmittedAt(submittedAt);
        return this;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getSubmissionText() {
        return this.submissionText;
    }

    public AssignmentSubmission submissionText(String submissionText) {
        this.setSubmissionText(submissionText);
        return this;
    }

    public void setSubmissionText(String submissionText) {
        this.submissionText = submissionText;
    }

    public String getFileUrls() {
        return this.fileUrls;
    }

    public AssignmentSubmission fileUrls(String fileUrls) {
        this.setFileUrls(fileUrls);
        return this;
    }

    public void setFileUrls(String fileUrls) {
        this.fileUrls = fileUrls;
    }

    public Integer getGrade() {
        return this.grade;
    }

    public AssignmentSubmission grade(Integer grade) {
        this.setGrade(grade);
        return this;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return this.feedback;
    }

    public AssignmentSubmission feedback(String feedback) {
        this.setFeedback(feedback);
        return this;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public SubmissionStatus getStatus() {
        return this.status;
    }

    public AssignmentSubmission status(SubmissionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public Assignment getAssignment() {
        return this.assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public AssignmentSubmission assignment(Assignment assignment) {
        this.setAssignment(assignment);
        return this;
    }

    public AppUser getStudent() {
        return this.student;
    }

    public void setStudent(AppUser appUser) {
        this.student = appUser;
    }

    public AssignmentSubmission student(AppUser appUser) {
        this.setStudent(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssignmentSubmission)) {
            return false;
        }
        return getId() != null && getId().equals(((AssignmentSubmission) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentSubmission{" +
            "id=" + getId() +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", submissionText='" + getSubmissionText() + "'" +
            ", fileUrls='" + getFileUrls() + "'" +
            ", grade=" + getGrade() +
            ", feedback='" + getFeedback() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}

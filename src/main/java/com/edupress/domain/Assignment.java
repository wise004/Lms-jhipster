package com.edupress.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Assignment.
 */
@Entity
@Table(name = "assignment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Assignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "instructions")
    private String instructions;

    @Column(name = "due_date")
    private Instant dueDate;

    @Column(name = "max_points")
    private Integer maxPoints;

    @Column(name = "submission_type")
    private String submissionType;

    @Lob
    @Column(name = "allowed_file_types")
    private String allowedFileTypes;

    @Column(name = "max_file_size")
    private Integer maxFileSize;

    @Column(name = "is_published")
    private Boolean isPublished;

    @Column(name = "allow_late_submission")
    private Boolean allowLateSubmission;

    @Column(name = "late_submission_penalty")
    private Integer lateSubmissionPenalty;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instructor", "category" }, allowSetters = true)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "course" }, allowSetters = true)
    private Lesson lesson;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Assignment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Assignment title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Assignment description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return this.instructions;
    }

    public Assignment instructions(String instructions) {
        this.setInstructions(instructions);
        return this;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Instant getDueDate() {
        return this.dueDate;
    }

    public Assignment dueDate(Instant dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getMaxPoints() {
        return this.maxPoints;
    }

    public Assignment maxPoints(Integer maxPoints) {
        this.setMaxPoints(maxPoints);
        return this;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }

    public String getSubmissionType() {
        return this.submissionType;
    }

    public Assignment submissionType(String submissionType) {
        this.setSubmissionType(submissionType);
        return this;
    }

    public void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
    }

    public String getAllowedFileTypes() {
        return this.allowedFileTypes;
    }

    public Assignment allowedFileTypes(String allowedFileTypes) {
        this.setAllowedFileTypes(allowedFileTypes);
        return this;
    }

    public void setAllowedFileTypes(String allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public Integer getMaxFileSize() {
        return this.maxFileSize;
    }

    public Assignment maxFileSize(Integer maxFileSize) {
        this.setMaxFileSize(maxFileSize);
        return this;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Boolean getIsPublished() {
        return this.isPublished;
    }

    public Assignment isPublished(Boolean isPublished) {
        this.setIsPublished(isPublished);
        return this;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Boolean getAllowLateSubmission() {
        return this.allowLateSubmission;
    }

    public Assignment allowLateSubmission(Boolean allowLateSubmission) {
        this.setAllowLateSubmission(allowLateSubmission);
        return this;
    }

    public void setAllowLateSubmission(Boolean allowLateSubmission) {
        this.allowLateSubmission = allowLateSubmission;
    }

    public Integer getLateSubmissionPenalty() {
        return this.lateSubmissionPenalty;
    }

    public Assignment lateSubmissionPenalty(Integer lateSubmissionPenalty) {
        this.setLateSubmissionPenalty(lateSubmissionPenalty);
        return this;
    }

    public void setLateSubmissionPenalty(Integer lateSubmissionPenalty) {
        this.lateSubmissionPenalty = lateSubmissionPenalty;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public Assignment sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Assignment course(Course course) {
        this.setCourse(course);
        return this;
    }

    public Lesson getLesson() {
        return this.lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Assignment lesson(Lesson lesson) {
        this.setLesson(lesson);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Assignment)) {
            return false;
        }
        return getId() != null && getId().equals(((Assignment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Assignment{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", instructions='" + getInstructions() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", maxPoints=" + getMaxPoints() +
            ", submissionType='" + getSubmissionType() + "'" +
            ", allowedFileTypes='" + getAllowedFileTypes() + "'" +
            ", maxFileSize=" + getMaxFileSize() +
            ", isPublished='" + getIsPublished() + "'" +
            ", allowLateSubmission='" + getAllowLateSubmission() + "'" +
            ", lateSubmissionPenalty=" + getLateSubmissionPenalty() +
            ", sortOrder=" + getSortOrder() +
            "}";
    }
}

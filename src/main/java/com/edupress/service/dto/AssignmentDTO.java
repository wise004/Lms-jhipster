package com.edupress.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.edupress.domain.Assignment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @Lob
    private String description;

    @Lob
    private String instructions;

    private Instant dueDate;

    private Integer maxPoints;

    private String submissionType;

    @Lob
    private String allowedFileTypes;

    private Integer maxFileSize;

    private Boolean isPublished;

    private Boolean allowLateSubmission;

    private Integer lateSubmissionPenalty;

    private Integer sortOrder;

    private CourseDTO course;

    private LessonDTO lesson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }

    public String getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
    }

    public String getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(String allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Boolean getAllowLateSubmission() {
        return allowLateSubmission;
    }

    public void setAllowLateSubmission(Boolean allowLateSubmission) {
        this.allowLateSubmission = allowLateSubmission;
    }

    public Integer getLateSubmissionPenalty() {
        return lateSubmissionPenalty;
    }

    public void setLateSubmissionPenalty(Integer lateSubmissionPenalty) {
        this.lateSubmissionPenalty = lateSubmissionPenalty;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public LessonDTO getLesson() {
        return lesson;
    }

    public void setLesson(LessonDTO lesson) {
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssignmentDTO)) {
            return false;
        }

        AssignmentDTO assignmentDTO = (AssignmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, assignmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentDTO{" +
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
            ", course=" + getCourse() +
            ", lesson=" + getLesson() +
            "}";
    }
}

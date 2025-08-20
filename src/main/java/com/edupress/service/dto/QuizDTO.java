package com.edupress.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.edupress.domain.Quiz} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuizDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @Lob
    private String description;

    private Integer timeLimit;

    private Integer passingScore;

    private Integer attemptsAllowed;

    private Integer sortOrder;

    @Lob
    private String questions;

    private Boolean isPublished;

    private Instant availableFrom;

    private Instant availableUntil;

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

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public Integer getAttemptsAllowed() {
        return attemptsAllowed;
    }

    public void setAttemptsAllowed(Integer attemptsAllowed) {
        this.attemptsAllowed = attemptsAllowed;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Instant getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(Instant availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Instant getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(Instant availableUntil) {
        this.availableUntil = availableUntil;
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
        if (!(o instanceof QuizDTO)) {
            return false;
        }

        QuizDTO quizDTO = (QuizDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, quizDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuizDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", timeLimit=" + getTimeLimit() +
            ", passingScore=" + getPassingScore() +
            ", attemptsAllowed=" + getAttemptsAllowed() +
            ", sortOrder=" + getSortOrder() +
            ", questions='" + getQuestions() + "'" +
            ", isPublished='" + getIsPublished() + "'" +
            ", availableFrom='" + getAvailableFrom() + "'" +
            ", availableUntil='" + getAvailableUntil() + "'" +
            ", course=" + getCourse() +
            ", lesson=" + getLesson() +
            "}";
    }
}

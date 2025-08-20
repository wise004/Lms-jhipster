package com.edupress.service.dto;

import com.edupress.domain.enumeration.AttemptStatus;
import jakarta.persistence.Lob;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.edupress.domain.QuizAttempt} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuizAttemptDTO implements Serializable {

    private Long id;

    private Instant startedAt;

    private Instant submittedAt;

    private Integer score;

    private Boolean passed;

    @Lob
    private String answers;

    private Integer attemptNumber;

    private AttemptStatus status;

    private QuizDTO quiz;

    private AppUserDTO student;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public QuizDTO getQuiz() {
        return quiz;
    }

    public void setQuiz(QuizDTO quiz) {
        this.quiz = quiz;
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
        if (!(o instanceof QuizAttemptDTO)) {
            return false;
        }

        QuizAttemptDTO quizAttemptDTO = (QuizAttemptDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, quizAttemptDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuizAttemptDTO{" +
            "id=" + getId() +
            ", startedAt='" + getStartedAt() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", score=" + getScore() +
            ", passed='" + getPassed() + "'" +
            ", answers='" + getAnswers() + "'" +
            ", attemptNumber=" + getAttemptNumber() +
            ", status='" + getStatus() + "'" +
            ", quiz=" + getQuiz() +
            ", student=" + getStudent() +
            "}";
    }
}

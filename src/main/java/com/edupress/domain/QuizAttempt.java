package com.edupress.domain;

import com.edupress.domain.enumeration.AttemptStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A QuizAttempt.
 */
@Entity
@Table(name = "quiz_attempt")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuizAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "score")
    private Integer score;

    @Column(name = "passed")
    private Boolean passed;

    @Lob
    @Column(name = "answers")
    private String answers;

    @Column(name = "attempt_number")
    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AttemptStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "course", "lesson" }, allowSetters = true)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser student;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public QuizAttempt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public QuizAttempt startedAt(Instant startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public QuizAttempt submittedAt(Instant submittedAt) {
        this.setSubmittedAt(submittedAt);
        return this;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Integer getScore() {
        return this.score;
    }

    public QuizAttempt score(Integer score) {
        this.setScore(score);
        return this;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getPassed() {
        return this.passed;
    }

    public QuizAttempt passed(Boolean passed) {
        this.setPassed(passed);
        return this;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public String getAnswers() {
        return this.answers;
    }

    public QuizAttempt answers(String answers) {
        this.setAnswers(answers);
        return this;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public Integer getAttemptNumber() {
        return this.attemptNumber;
    }

    public QuizAttempt attemptNumber(Integer attemptNumber) {
        this.setAttemptNumber(attemptNumber);
        return this;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public AttemptStatus getStatus() {
        return this.status;
    }

    public QuizAttempt status(AttemptStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public Quiz getQuiz() {
        return this.quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public QuizAttempt quiz(Quiz quiz) {
        this.setQuiz(quiz);
        return this;
    }

    public AppUser getStudent() {
        return this.student;
    }

    public void setStudent(AppUser appUser) {
        this.student = appUser;
    }

    public QuizAttempt student(AppUser appUser) {
        this.setStudent(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuizAttempt)) {
            return false;
        }
        return getId() != null && getId().equals(((QuizAttempt) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuizAttempt{" +
            "id=" + getId() +
            ", startedAt='" + getStartedAt() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", score=" + getScore() +
            ", passed='" + getPassed() + "'" +
            ", answers='" + getAnswers() + "'" +
            ", attemptNumber=" + getAttemptNumber() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}

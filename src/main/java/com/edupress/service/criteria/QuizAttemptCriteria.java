package com.edupress.service.criteria;

import com.edupress.domain.enumeration.AttemptStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.edupress.domain.QuizAttempt} entity. This class is used
 * in {@link com.edupress.web.rest.QuizAttemptResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /quiz-attempts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuizAttemptCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AttemptStatus
     */
    public static class AttemptStatusFilter extends Filter<AttemptStatus> {

        public AttemptStatusFilter() {}

        public AttemptStatusFilter(AttemptStatusFilter filter) {
            super(filter);
        }

        @Override
        public AttemptStatusFilter copy() {
            return new AttemptStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter startedAt;

    private InstantFilter submittedAt;

    private IntegerFilter score;

    private BooleanFilter passed;

    private IntegerFilter attemptNumber;

    private AttemptStatusFilter status;

    private LongFilter quizId;

    private LongFilter studentId;

    private Boolean distinct;

    public QuizAttemptCriteria() {}

    public QuizAttemptCriteria(QuizAttemptCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.startedAt = other.optionalStartedAt().map(InstantFilter::copy).orElse(null);
        this.submittedAt = other.optionalSubmittedAt().map(InstantFilter::copy).orElse(null);
        this.score = other.optionalScore().map(IntegerFilter::copy).orElse(null);
        this.passed = other.optionalPassed().map(BooleanFilter::copy).orElse(null);
        this.attemptNumber = other.optionalAttemptNumber().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(AttemptStatusFilter::copy).orElse(null);
        this.quizId = other.optionalQuizId().map(LongFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public QuizAttemptCriteria copy() {
        return new QuizAttemptCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getStartedAt() {
        return startedAt;
    }

    public Optional<InstantFilter> optionalStartedAt() {
        return Optional.ofNullable(startedAt);
    }

    public InstantFilter startedAt() {
        if (startedAt == null) {
            setStartedAt(new InstantFilter());
        }
        return startedAt;
    }

    public void setStartedAt(InstantFilter startedAt) {
        this.startedAt = startedAt;
    }

    public InstantFilter getSubmittedAt() {
        return submittedAt;
    }

    public Optional<InstantFilter> optionalSubmittedAt() {
        return Optional.ofNullable(submittedAt);
    }

    public InstantFilter submittedAt() {
        if (submittedAt == null) {
            setSubmittedAt(new InstantFilter());
        }
        return submittedAt;
    }

    public void setSubmittedAt(InstantFilter submittedAt) {
        this.submittedAt = submittedAt;
    }

    public IntegerFilter getScore() {
        return score;
    }

    public Optional<IntegerFilter> optionalScore() {
        return Optional.ofNullable(score);
    }

    public IntegerFilter score() {
        if (score == null) {
            setScore(new IntegerFilter());
        }
        return score;
    }

    public void setScore(IntegerFilter score) {
        this.score = score;
    }

    public BooleanFilter getPassed() {
        return passed;
    }

    public Optional<BooleanFilter> optionalPassed() {
        return Optional.ofNullable(passed);
    }

    public BooleanFilter passed() {
        if (passed == null) {
            setPassed(new BooleanFilter());
        }
        return passed;
    }

    public void setPassed(BooleanFilter passed) {
        this.passed = passed;
    }

    public IntegerFilter getAttemptNumber() {
        return attemptNumber;
    }

    public Optional<IntegerFilter> optionalAttemptNumber() {
        return Optional.ofNullable(attemptNumber);
    }

    public IntegerFilter attemptNumber() {
        if (attemptNumber == null) {
            setAttemptNumber(new IntegerFilter());
        }
        return attemptNumber;
    }

    public void setAttemptNumber(IntegerFilter attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public AttemptStatusFilter getStatus() {
        return status;
    }

    public Optional<AttemptStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public AttemptStatusFilter status() {
        if (status == null) {
            setStatus(new AttemptStatusFilter());
        }
        return status;
    }

    public void setStatus(AttemptStatusFilter status) {
        this.status = status;
    }

    public LongFilter getQuizId() {
        return quizId;
    }

    public Optional<LongFilter> optionalQuizId() {
        return Optional.ofNullable(quizId);
    }

    public LongFilter quizId() {
        if (quizId == null) {
            setQuizId(new LongFilter());
        }
        return quizId;
    }

    public void setQuizId(LongFilter quizId) {
        this.quizId = quizId;
    }

    public LongFilter getStudentId() {
        return studentId;
    }

    public Optional<LongFilter> optionalStudentId() {
        return Optional.ofNullable(studentId);
    }

    public LongFilter studentId() {
        if (studentId == null) {
            setStudentId(new LongFilter());
        }
        return studentId;
    }

    public void setStudentId(LongFilter studentId) {
        this.studentId = studentId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final QuizAttemptCriteria that = (QuizAttemptCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(startedAt, that.startedAt) &&
            Objects.equals(submittedAt, that.submittedAt) &&
            Objects.equals(score, that.score) &&
            Objects.equals(passed, that.passed) &&
            Objects.equals(attemptNumber, that.attemptNumber) &&
            Objects.equals(status, that.status) &&
            Objects.equals(quizId, that.quizId) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startedAt, submittedAt, score, passed, attemptNumber, status, quizId, studentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuizAttemptCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStartedAt().map(f -> "startedAt=" + f + ", ").orElse("") +
            optionalSubmittedAt().map(f -> "submittedAt=" + f + ", ").orElse("") +
            optionalScore().map(f -> "score=" + f + ", ").orElse("") +
            optionalPassed().map(f -> "passed=" + f + ", ").orElse("") +
            optionalAttemptNumber().map(f -> "attemptNumber=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalQuizId().map(f -> "quizId=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

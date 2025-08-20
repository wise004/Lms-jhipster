package com.edupress.service.criteria;

import com.edupress.domain.enumeration.SubmissionStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.edupress.domain.AssignmentSubmission} entity. This class is used
 * in {@link com.edupress.web.rest.AssignmentSubmissionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /assignment-submissions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentSubmissionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering SubmissionStatus
     */
    public static class SubmissionStatusFilter extends Filter<SubmissionStatus> {

        public SubmissionStatusFilter() {}

        public SubmissionStatusFilter(SubmissionStatusFilter filter) {
            super(filter);
        }

        @Override
        public SubmissionStatusFilter copy() {
            return new SubmissionStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter submittedAt;

    private IntegerFilter grade;

    private SubmissionStatusFilter status;

    private LongFilter assignmentId;

    private LongFilter studentId;

    private Boolean distinct;

    public AssignmentSubmissionCriteria() {}

    public AssignmentSubmissionCriteria(AssignmentSubmissionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.submittedAt = other.optionalSubmittedAt().map(InstantFilter::copy).orElse(null);
        this.grade = other.optionalGrade().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(SubmissionStatusFilter::copy).orElse(null);
        this.assignmentId = other.optionalAssignmentId().map(LongFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AssignmentSubmissionCriteria copy() {
        return new AssignmentSubmissionCriteria(this);
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

    public IntegerFilter getGrade() {
        return grade;
    }

    public Optional<IntegerFilter> optionalGrade() {
        return Optional.ofNullable(grade);
    }

    public IntegerFilter grade() {
        if (grade == null) {
            setGrade(new IntegerFilter());
        }
        return grade;
    }

    public void setGrade(IntegerFilter grade) {
        this.grade = grade;
    }

    public SubmissionStatusFilter getStatus() {
        return status;
    }

    public Optional<SubmissionStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public SubmissionStatusFilter status() {
        if (status == null) {
            setStatus(new SubmissionStatusFilter());
        }
        return status;
    }

    public void setStatus(SubmissionStatusFilter status) {
        this.status = status;
    }

    public LongFilter getAssignmentId() {
        return assignmentId;
    }

    public Optional<LongFilter> optionalAssignmentId() {
        return Optional.ofNullable(assignmentId);
    }

    public LongFilter assignmentId() {
        if (assignmentId == null) {
            setAssignmentId(new LongFilter());
        }
        return assignmentId;
    }

    public void setAssignmentId(LongFilter assignmentId) {
        this.assignmentId = assignmentId;
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
        final AssignmentSubmissionCriteria that = (AssignmentSubmissionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(submittedAt, that.submittedAt) &&
            Objects.equals(grade, that.grade) &&
            Objects.equals(status, that.status) &&
            Objects.equals(assignmentId, that.assignmentId) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, submittedAt, grade, status, assignmentId, studentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentSubmissionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSubmittedAt().map(f -> "submittedAt=" + f + ", ").orElse("") +
            optionalGrade().map(f -> "grade=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalAssignmentId().map(f -> "assignmentId=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

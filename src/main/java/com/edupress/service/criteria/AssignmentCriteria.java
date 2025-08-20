package com.edupress.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.edupress.domain.Assignment} entity. This class is used
 * in {@link com.edupress.web.rest.AssignmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /assignments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private InstantFilter dueDate;

    private IntegerFilter maxPoints;

    private StringFilter submissionType;

    private IntegerFilter maxFileSize;

    private BooleanFilter isPublished;

    private BooleanFilter allowLateSubmission;

    private IntegerFilter lateSubmissionPenalty;

    private IntegerFilter sortOrder;

    private LongFilter courseId;

    private LongFilter lessonId;

    private Boolean distinct;

    public AssignmentCriteria() {}

    public AssignmentCriteria(AssignmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.dueDate = other.optionalDueDate().map(InstantFilter::copy).orElse(null);
        this.maxPoints = other.optionalMaxPoints().map(IntegerFilter::copy).orElse(null);
        this.submissionType = other.optionalSubmissionType().map(StringFilter::copy).orElse(null);
        this.maxFileSize = other.optionalMaxFileSize().map(IntegerFilter::copy).orElse(null);
        this.isPublished = other.optionalIsPublished().map(BooleanFilter::copy).orElse(null);
        this.allowLateSubmission = other.optionalAllowLateSubmission().map(BooleanFilter::copy).orElse(null);
        this.lateSubmissionPenalty = other.optionalLateSubmissionPenalty().map(IntegerFilter::copy).orElse(null);
        this.sortOrder = other.optionalSortOrder().map(IntegerFilter::copy).orElse(null);
        this.courseId = other.optionalCourseId().map(LongFilter::copy).orElse(null);
        this.lessonId = other.optionalLessonId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AssignmentCriteria copy() {
        return new AssignmentCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public InstantFilter getDueDate() {
        return dueDate;
    }

    public Optional<InstantFilter> optionalDueDate() {
        return Optional.ofNullable(dueDate);
    }

    public InstantFilter dueDate() {
        if (dueDate == null) {
            setDueDate(new InstantFilter());
        }
        return dueDate;
    }

    public void setDueDate(InstantFilter dueDate) {
        this.dueDate = dueDate;
    }

    public IntegerFilter getMaxPoints() {
        return maxPoints;
    }

    public Optional<IntegerFilter> optionalMaxPoints() {
        return Optional.ofNullable(maxPoints);
    }

    public IntegerFilter maxPoints() {
        if (maxPoints == null) {
            setMaxPoints(new IntegerFilter());
        }
        return maxPoints;
    }

    public void setMaxPoints(IntegerFilter maxPoints) {
        this.maxPoints = maxPoints;
    }

    public StringFilter getSubmissionType() {
        return submissionType;
    }

    public Optional<StringFilter> optionalSubmissionType() {
        return Optional.ofNullable(submissionType);
    }

    public StringFilter submissionType() {
        if (submissionType == null) {
            setSubmissionType(new StringFilter());
        }
        return submissionType;
    }

    public void setSubmissionType(StringFilter submissionType) {
        this.submissionType = submissionType;
    }

    public IntegerFilter getMaxFileSize() {
        return maxFileSize;
    }

    public Optional<IntegerFilter> optionalMaxFileSize() {
        return Optional.ofNullable(maxFileSize);
    }

    public IntegerFilter maxFileSize() {
        if (maxFileSize == null) {
            setMaxFileSize(new IntegerFilter());
        }
        return maxFileSize;
    }

    public void setMaxFileSize(IntegerFilter maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public BooleanFilter getIsPublished() {
        return isPublished;
    }

    public Optional<BooleanFilter> optionalIsPublished() {
        return Optional.ofNullable(isPublished);
    }

    public BooleanFilter isPublished() {
        if (isPublished == null) {
            setIsPublished(new BooleanFilter());
        }
        return isPublished;
    }

    public void setIsPublished(BooleanFilter isPublished) {
        this.isPublished = isPublished;
    }

    public BooleanFilter getAllowLateSubmission() {
        return allowLateSubmission;
    }

    public Optional<BooleanFilter> optionalAllowLateSubmission() {
        return Optional.ofNullable(allowLateSubmission);
    }

    public BooleanFilter allowLateSubmission() {
        if (allowLateSubmission == null) {
            setAllowLateSubmission(new BooleanFilter());
        }
        return allowLateSubmission;
    }

    public void setAllowLateSubmission(BooleanFilter allowLateSubmission) {
        this.allowLateSubmission = allowLateSubmission;
    }

    public IntegerFilter getLateSubmissionPenalty() {
        return lateSubmissionPenalty;
    }

    public Optional<IntegerFilter> optionalLateSubmissionPenalty() {
        return Optional.ofNullable(lateSubmissionPenalty);
    }

    public IntegerFilter lateSubmissionPenalty() {
        if (lateSubmissionPenalty == null) {
            setLateSubmissionPenalty(new IntegerFilter());
        }
        return lateSubmissionPenalty;
    }

    public void setLateSubmissionPenalty(IntegerFilter lateSubmissionPenalty) {
        this.lateSubmissionPenalty = lateSubmissionPenalty;
    }

    public IntegerFilter getSortOrder() {
        return sortOrder;
    }

    public Optional<IntegerFilter> optionalSortOrder() {
        return Optional.ofNullable(sortOrder);
    }

    public IntegerFilter sortOrder() {
        if (sortOrder == null) {
            setSortOrder(new IntegerFilter());
        }
        return sortOrder;
    }

    public void setSortOrder(IntegerFilter sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LongFilter getCourseId() {
        return courseId;
    }

    public Optional<LongFilter> optionalCourseId() {
        return Optional.ofNullable(courseId);
    }

    public LongFilter courseId() {
        if (courseId == null) {
            setCourseId(new LongFilter());
        }
        return courseId;
    }

    public void setCourseId(LongFilter courseId) {
        this.courseId = courseId;
    }

    public LongFilter getLessonId() {
        return lessonId;
    }

    public Optional<LongFilter> optionalLessonId() {
        return Optional.ofNullable(lessonId);
    }

    public LongFilter lessonId() {
        if (lessonId == null) {
            setLessonId(new LongFilter());
        }
        return lessonId;
    }

    public void setLessonId(LongFilter lessonId) {
        this.lessonId = lessonId;
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
        final AssignmentCriteria that = (AssignmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(dueDate, that.dueDate) &&
            Objects.equals(maxPoints, that.maxPoints) &&
            Objects.equals(submissionType, that.submissionType) &&
            Objects.equals(maxFileSize, that.maxFileSize) &&
            Objects.equals(isPublished, that.isPublished) &&
            Objects.equals(allowLateSubmission, that.allowLateSubmission) &&
            Objects.equals(lateSubmissionPenalty, that.lateSubmissionPenalty) &&
            Objects.equals(sortOrder, that.sortOrder) &&
            Objects.equals(courseId, that.courseId) &&
            Objects.equals(lessonId, that.lessonId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            dueDate,
            maxPoints,
            submissionType,
            maxFileSize,
            isPublished,
            allowLateSubmission,
            lateSubmissionPenalty,
            sortOrder,
            courseId,
            lessonId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalDueDate().map(f -> "dueDate=" + f + ", ").orElse("") +
            optionalMaxPoints().map(f -> "maxPoints=" + f + ", ").orElse("") +
            optionalSubmissionType().map(f -> "submissionType=" + f + ", ").orElse("") +
            optionalMaxFileSize().map(f -> "maxFileSize=" + f + ", ").orElse("") +
            optionalIsPublished().map(f -> "isPublished=" + f + ", ").orElse("") +
            optionalAllowLateSubmission().map(f -> "allowLateSubmission=" + f + ", ").orElse("") +
            optionalLateSubmissionPenalty().map(f -> "lateSubmissionPenalty=" + f + ", ").orElse("") +
            optionalSortOrder().map(f -> "sortOrder=" + f + ", ").orElse("") +
            optionalCourseId().map(f -> "courseId=" + f + ", ").orElse("") +
            optionalLessonId().map(f -> "lessonId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

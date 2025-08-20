package com.edupress.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.edupress.domain.Quiz} entity. This class is used
 * in {@link com.edupress.web.rest.QuizResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /quizzes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuizCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private IntegerFilter timeLimit;

    private IntegerFilter passingScore;

    private IntegerFilter attemptsAllowed;

    private IntegerFilter sortOrder;

    private BooleanFilter isPublished;

    private InstantFilter availableFrom;

    private InstantFilter availableUntil;

    private LongFilter courseId;

    private LongFilter lessonId;

    private Boolean distinct;

    public QuizCriteria() {}

    public QuizCriteria(QuizCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.timeLimit = other.optionalTimeLimit().map(IntegerFilter::copy).orElse(null);
        this.passingScore = other.optionalPassingScore().map(IntegerFilter::copy).orElse(null);
        this.attemptsAllowed = other.optionalAttemptsAllowed().map(IntegerFilter::copy).orElse(null);
        this.sortOrder = other.optionalSortOrder().map(IntegerFilter::copy).orElse(null);
        this.isPublished = other.optionalIsPublished().map(BooleanFilter::copy).orElse(null);
        this.availableFrom = other.optionalAvailableFrom().map(InstantFilter::copy).orElse(null);
        this.availableUntil = other.optionalAvailableUntil().map(InstantFilter::copy).orElse(null);
        this.courseId = other.optionalCourseId().map(LongFilter::copy).orElse(null);
        this.lessonId = other.optionalLessonId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public QuizCriteria copy() {
        return new QuizCriteria(this);
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

    public IntegerFilter getTimeLimit() {
        return timeLimit;
    }

    public Optional<IntegerFilter> optionalTimeLimit() {
        return Optional.ofNullable(timeLimit);
    }

    public IntegerFilter timeLimit() {
        if (timeLimit == null) {
            setTimeLimit(new IntegerFilter());
        }
        return timeLimit;
    }

    public void setTimeLimit(IntegerFilter timeLimit) {
        this.timeLimit = timeLimit;
    }

    public IntegerFilter getPassingScore() {
        return passingScore;
    }

    public Optional<IntegerFilter> optionalPassingScore() {
        return Optional.ofNullable(passingScore);
    }

    public IntegerFilter passingScore() {
        if (passingScore == null) {
            setPassingScore(new IntegerFilter());
        }
        return passingScore;
    }

    public void setPassingScore(IntegerFilter passingScore) {
        this.passingScore = passingScore;
    }

    public IntegerFilter getAttemptsAllowed() {
        return attemptsAllowed;
    }

    public Optional<IntegerFilter> optionalAttemptsAllowed() {
        return Optional.ofNullable(attemptsAllowed);
    }

    public IntegerFilter attemptsAllowed() {
        if (attemptsAllowed == null) {
            setAttemptsAllowed(new IntegerFilter());
        }
        return attemptsAllowed;
    }

    public void setAttemptsAllowed(IntegerFilter attemptsAllowed) {
        this.attemptsAllowed = attemptsAllowed;
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

    public InstantFilter getAvailableFrom() {
        return availableFrom;
    }

    public Optional<InstantFilter> optionalAvailableFrom() {
        return Optional.ofNullable(availableFrom);
    }

    public InstantFilter availableFrom() {
        if (availableFrom == null) {
            setAvailableFrom(new InstantFilter());
        }
        return availableFrom;
    }

    public void setAvailableFrom(InstantFilter availableFrom) {
        this.availableFrom = availableFrom;
    }

    public InstantFilter getAvailableUntil() {
        return availableUntil;
    }

    public Optional<InstantFilter> optionalAvailableUntil() {
        return Optional.ofNullable(availableUntil);
    }

    public InstantFilter availableUntil() {
        if (availableUntil == null) {
            setAvailableUntil(new InstantFilter());
        }
        return availableUntil;
    }

    public void setAvailableUntil(InstantFilter availableUntil) {
        this.availableUntil = availableUntil;
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
        final QuizCriteria that = (QuizCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(timeLimit, that.timeLimit) &&
            Objects.equals(passingScore, that.passingScore) &&
            Objects.equals(attemptsAllowed, that.attemptsAllowed) &&
            Objects.equals(sortOrder, that.sortOrder) &&
            Objects.equals(isPublished, that.isPublished) &&
            Objects.equals(availableFrom, that.availableFrom) &&
            Objects.equals(availableUntil, that.availableUntil) &&
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
            timeLimit,
            passingScore,
            attemptsAllowed,
            sortOrder,
            isPublished,
            availableFrom,
            availableUntil,
            courseId,
            lessonId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuizCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalTimeLimit().map(f -> "timeLimit=" + f + ", ").orElse("") +
            optionalPassingScore().map(f -> "passingScore=" + f + ", ").orElse("") +
            optionalAttemptsAllowed().map(f -> "attemptsAllowed=" + f + ", ").orElse("") +
            optionalSortOrder().map(f -> "sortOrder=" + f + ", ").orElse("") +
            optionalIsPublished().map(f -> "isPublished=" + f + ", ").orElse("") +
            optionalAvailableFrom().map(f -> "availableFrom=" + f + ", ").orElse("") +
            optionalAvailableUntil().map(f -> "availableUntil=" + f + ", ").orElse("") +
            optionalCourseId().map(f -> "courseId=" + f + ", ").orElse("") +
            optionalLessonId().map(f -> "lessonId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

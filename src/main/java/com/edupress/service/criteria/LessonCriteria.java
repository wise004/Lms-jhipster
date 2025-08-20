package com.edupress.service.criteria;

import com.edupress.domain.enumeration.LessonType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.edupress.domain.Lesson} entity. This class is used
 * in {@link com.edupress.web.rest.LessonResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /lessons?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonCriteria implements Serializable, Criteria {

    /**
     * Class for filtering LessonType
     */
    public static class LessonTypeFilter extends Filter<LessonType> {

        public LessonTypeFilter() {}

        public LessonTypeFilter(LessonTypeFilter filter) {
            super(filter);
        }

        @Override
        public LessonTypeFilter copy() {
            return new LessonTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter videoUrl;

    private IntegerFilter duration;

    private LessonTypeFilter type;

    private BooleanFilter isFree;

    private IntegerFilter sortOrder;

    private BooleanFilter isPublished;

    private LongFilter courseId;

    private Boolean distinct;

    public LessonCriteria() {}

    public LessonCriteria(LessonCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.videoUrl = other.optionalVideoUrl().map(StringFilter::copy).orElse(null);
        this.duration = other.optionalDuration().map(IntegerFilter::copy).orElse(null);
        this.type = other.optionalType().map(LessonTypeFilter::copy).orElse(null);
        this.isFree = other.optionalIsFree().map(BooleanFilter::copy).orElse(null);
        this.sortOrder = other.optionalSortOrder().map(IntegerFilter::copy).orElse(null);
        this.isPublished = other.optionalIsPublished().map(BooleanFilter::copy).orElse(null);
        this.courseId = other.optionalCourseId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LessonCriteria copy() {
        return new LessonCriteria(this);
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

    public StringFilter getVideoUrl() {
        return videoUrl;
    }

    public Optional<StringFilter> optionalVideoUrl() {
        return Optional.ofNullable(videoUrl);
    }

    public StringFilter videoUrl() {
        if (videoUrl == null) {
            setVideoUrl(new StringFilter());
        }
        return videoUrl;
    }

    public void setVideoUrl(StringFilter videoUrl) {
        this.videoUrl = videoUrl;
    }

    public IntegerFilter getDuration() {
        return duration;
    }

    public Optional<IntegerFilter> optionalDuration() {
        return Optional.ofNullable(duration);
    }

    public IntegerFilter duration() {
        if (duration == null) {
            setDuration(new IntegerFilter());
        }
        return duration;
    }

    public void setDuration(IntegerFilter duration) {
        this.duration = duration;
    }

    public LessonTypeFilter getType() {
        return type;
    }

    public Optional<LessonTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public LessonTypeFilter type() {
        if (type == null) {
            setType(new LessonTypeFilter());
        }
        return type;
    }

    public void setType(LessonTypeFilter type) {
        this.type = type;
    }

    public BooleanFilter getIsFree() {
        return isFree;
    }

    public Optional<BooleanFilter> optionalIsFree() {
        return Optional.ofNullable(isFree);
    }

    public BooleanFilter isFree() {
        if (isFree == null) {
            setIsFree(new BooleanFilter());
        }
        return isFree;
    }

    public void setIsFree(BooleanFilter isFree) {
        this.isFree = isFree;
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
        final LessonCriteria that = (LessonCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(videoUrl, that.videoUrl) &&
            Objects.equals(duration, that.duration) &&
            Objects.equals(type, that.type) &&
            Objects.equals(isFree, that.isFree) &&
            Objects.equals(sortOrder, that.sortOrder) &&
            Objects.equals(isPublished, that.isPublished) &&
            Objects.equals(courseId, that.courseId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, videoUrl, duration, type, isFree, sortOrder, isPublished, courseId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalVideoUrl().map(f -> "videoUrl=" + f + ", ").orElse("") +
            optionalDuration().map(f -> "duration=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalIsFree().map(f -> "isFree=" + f + ", ").orElse("") +
            optionalSortOrder().map(f -> "sortOrder=" + f + ", ").orElse("") +
            optionalIsPublished().map(f -> "isPublished=" + f + ", ").orElse("") +
            optionalCourseId().map(f -> "courseId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

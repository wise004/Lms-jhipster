package com.edupress.service.criteria;

import com.edupress.domain.enumeration.CourseStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.edupress.domain.Course} entity. This class is used
 * in {@link com.edupress.web.rest.CourseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /courses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CourseStatus
     */
    public static class CourseStatusFilter extends Filter<CourseStatus> {

        public CourseStatusFilter() {}

        public CourseStatusFilter(CourseStatusFilter filter) {
            super(filter);
        }

        @Override
        public CourseStatusFilter copy() {
            return new CourseStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter slug;

    private StringFilter shortDescription;

    private StringFilter thumbnailUrl;

    private BigDecimalFilter price;

    private BigDecimalFilter originalPrice;

    private StringFilter level;

    private StringFilter language;

    private CourseStatusFilter status;

    private BooleanFilter isPublished;

    private BooleanFilter isFeatured;

    private DoubleFilter averageRating;

    private IntegerFilter enrollmentCount;

    private LongFilter instructorId;

    private LongFilter categoryId;

    private Boolean distinct;

    public CourseCriteria() {}

    public CourseCriteria(CourseCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.slug = other.optionalSlug().map(StringFilter::copy).orElse(null);
        this.shortDescription = other.optionalShortDescription().map(StringFilter::copy).orElse(null);
        this.thumbnailUrl = other.optionalThumbnailUrl().map(StringFilter::copy).orElse(null);
        this.price = other.optionalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.originalPrice = other.optionalOriginalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.level = other.optionalLevel().map(StringFilter::copy).orElse(null);
        this.language = other.optionalLanguage().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(CourseStatusFilter::copy).orElse(null);
        this.isPublished = other.optionalIsPublished().map(BooleanFilter::copy).orElse(null);
        this.isFeatured = other.optionalIsFeatured().map(BooleanFilter::copy).orElse(null);
        this.averageRating = other.optionalAverageRating().map(DoubleFilter::copy).orElse(null);
        this.enrollmentCount = other.optionalEnrollmentCount().map(IntegerFilter::copy).orElse(null);
        this.instructorId = other.optionalInstructorId().map(LongFilter::copy).orElse(null);
        this.categoryId = other.optionalCategoryId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CourseCriteria copy() {
        return new CourseCriteria(this);
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

    public StringFilter getSlug() {
        return slug;
    }

    public Optional<StringFilter> optionalSlug() {
        return Optional.ofNullable(slug);
    }

    public StringFilter slug() {
        if (slug == null) {
            setSlug(new StringFilter());
        }
        return slug;
    }

    public void setSlug(StringFilter slug) {
        this.slug = slug;
    }

    public StringFilter getShortDescription() {
        return shortDescription;
    }

    public Optional<StringFilter> optionalShortDescription() {
        return Optional.ofNullable(shortDescription);
    }

    public StringFilter shortDescription() {
        if (shortDescription == null) {
            setShortDescription(new StringFilter());
        }
        return shortDescription;
    }

    public void setShortDescription(StringFilter shortDescription) {
        this.shortDescription = shortDescription;
    }

    public StringFilter getThumbnailUrl() {
        return thumbnailUrl;
    }

    public Optional<StringFilter> optionalThumbnailUrl() {
        return Optional.ofNullable(thumbnailUrl);
    }

    public StringFilter thumbnailUrl() {
        if (thumbnailUrl == null) {
            setThumbnailUrl(new StringFilter());
        }
        return thumbnailUrl;
    }

    public void setThumbnailUrl(StringFilter thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public Optional<BigDecimalFilter> optionalPrice() {
        return Optional.ofNullable(price);
    }

    public BigDecimalFilter price() {
        if (price == null) {
            setPrice(new BigDecimalFilter());
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public BigDecimalFilter getOriginalPrice() {
        return originalPrice;
    }

    public Optional<BigDecimalFilter> optionalOriginalPrice() {
        return Optional.ofNullable(originalPrice);
    }

    public BigDecimalFilter originalPrice() {
        if (originalPrice == null) {
            setOriginalPrice(new BigDecimalFilter());
        }
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimalFilter originalPrice) {
        this.originalPrice = originalPrice;
    }

    public StringFilter getLevel() {
        return level;
    }

    public Optional<StringFilter> optionalLevel() {
        return Optional.ofNullable(level);
    }

    public StringFilter level() {
        if (level == null) {
            setLevel(new StringFilter());
        }
        return level;
    }

    public void setLevel(StringFilter level) {
        this.level = level;
    }

    public StringFilter getLanguage() {
        return language;
    }

    public Optional<StringFilter> optionalLanguage() {
        return Optional.ofNullable(language);
    }

    public StringFilter language() {
        if (language == null) {
            setLanguage(new StringFilter());
        }
        return language;
    }

    public void setLanguage(StringFilter language) {
        this.language = language;
    }

    public CourseStatusFilter getStatus() {
        return status;
    }

    public Optional<CourseStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public CourseStatusFilter status() {
        if (status == null) {
            setStatus(new CourseStatusFilter());
        }
        return status;
    }

    public void setStatus(CourseStatusFilter status) {
        this.status = status;
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

    public BooleanFilter getIsFeatured() {
        return isFeatured;
    }

    public Optional<BooleanFilter> optionalIsFeatured() {
        return Optional.ofNullable(isFeatured);
    }

    public BooleanFilter isFeatured() {
        if (isFeatured == null) {
            setIsFeatured(new BooleanFilter());
        }
        return isFeatured;
    }

    public void setIsFeatured(BooleanFilter isFeatured) {
        this.isFeatured = isFeatured;
    }

    public DoubleFilter getAverageRating() {
        return averageRating;
    }

    public Optional<DoubleFilter> optionalAverageRating() {
        return Optional.ofNullable(averageRating);
    }

    public DoubleFilter averageRating() {
        if (averageRating == null) {
            setAverageRating(new DoubleFilter());
        }
        return averageRating;
    }

    public void setAverageRating(DoubleFilter averageRating) {
        this.averageRating = averageRating;
    }

    public IntegerFilter getEnrollmentCount() {
        return enrollmentCount;
    }

    public Optional<IntegerFilter> optionalEnrollmentCount() {
        return Optional.ofNullable(enrollmentCount);
    }

    public IntegerFilter enrollmentCount() {
        if (enrollmentCount == null) {
            setEnrollmentCount(new IntegerFilter());
        }
        return enrollmentCount;
    }

    public void setEnrollmentCount(IntegerFilter enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public LongFilter getInstructorId() {
        return instructorId;
    }

    public Optional<LongFilter> optionalInstructorId() {
        return Optional.ofNullable(instructorId);
    }

    public LongFilter instructorId() {
        if (instructorId == null) {
            setInstructorId(new LongFilter());
        }
        return instructorId;
    }

    public void setInstructorId(LongFilter instructorId) {
        this.instructorId = instructorId;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public Optional<LongFilter> optionalCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            setCategoryId(new LongFilter());
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
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
        final CourseCriteria that = (CourseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(shortDescription, that.shortDescription) &&
            Objects.equals(thumbnailUrl, that.thumbnailUrl) &&
            Objects.equals(price, that.price) &&
            Objects.equals(originalPrice, that.originalPrice) &&
            Objects.equals(level, that.level) &&
            Objects.equals(language, that.language) &&
            Objects.equals(status, that.status) &&
            Objects.equals(isPublished, that.isPublished) &&
            Objects.equals(isFeatured, that.isFeatured) &&
            Objects.equals(averageRating, that.averageRating) &&
            Objects.equals(enrollmentCount, that.enrollmentCount) &&
            Objects.equals(instructorId, that.instructorId) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            slug,
            shortDescription,
            thumbnailUrl,
            price,
            originalPrice,
            level,
            language,
            status,
            isPublished,
            isFeatured,
            averageRating,
            enrollmentCount,
            instructorId,
            categoryId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalSlug().map(f -> "slug=" + f + ", ").orElse("") +
            optionalShortDescription().map(f -> "shortDescription=" + f + ", ").orElse("") +
            optionalThumbnailUrl().map(f -> "thumbnailUrl=" + f + ", ").orElse("") +
            optionalPrice().map(f -> "price=" + f + ", ").orElse("") +
            optionalOriginalPrice().map(f -> "originalPrice=" + f + ", ").orElse("") +
            optionalLevel().map(f -> "level=" + f + ", ").orElse("") +
            optionalLanguage().map(f -> "language=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalIsPublished().map(f -> "isPublished=" + f + ", ").orElse("") +
            optionalIsFeatured().map(f -> "isFeatured=" + f + ", ").orElse("") +
            optionalAverageRating().map(f -> "averageRating=" + f + ", ").orElse("") +
            optionalEnrollmentCount().map(f -> "enrollmentCount=" + f + ", ").orElse("") +
            optionalInstructorId().map(f -> "instructorId=" + f + ", ").orElse("") +
            optionalCategoryId().map(f -> "categoryId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

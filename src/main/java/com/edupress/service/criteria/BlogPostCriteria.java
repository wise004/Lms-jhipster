package com.edupress.service.criteria;

import com.edupress.domain.enumeration.PostStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.edupress.domain.BlogPost} entity. This class is used
 * in {@link com.edupress.web.rest.BlogPostResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /blog-posts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BlogPostCriteria implements Serializable, Criteria {

    /**
     * Class for filtering PostStatus
     */
    public static class PostStatusFilter extends Filter<PostStatus> {

        public PostStatusFilter() {}

        public PostStatusFilter(PostStatusFilter filter) {
            super(filter);
        }

        @Override
        public PostStatusFilter copy() {
            return new PostStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter slug;

    private StringFilter summary;

    private StringFilter coverImageUrl;

    private InstantFilter publishDate;

    private PostStatusFilter status;

    private LongFilter authorId;

    private Boolean distinct;

    public BlogPostCriteria() {}

    public BlogPostCriteria(BlogPostCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.slug = other.optionalSlug().map(StringFilter::copy).orElse(null);
        this.summary = other.optionalSummary().map(StringFilter::copy).orElse(null);
        this.coverImageUrl = other.optionalCoverImageUrl().map(StringFilter::copy).orElse(null);
        this.publishDate = other.optionalPublishDate().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(PostStatusFilter::copy).orElse(null);
        this.authorId = other.optionalAuthorId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BlogPostCriteria copy() {
        return new BlogPostCriteria(this);
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

    public StringFilter getSummary() {
        return summary;
    }

    public Optional<StringFilter> optionalSummary() {
        return Optional.ofNullable(summary);
    }

    public StringFilter summary() {
        if (summary == null) {
            setSummary(new StringFilter());
        }
        return summary;
    }

    public void setSummary(StringFilter summary) {
        this.summary = summary;
    }

    public StringFilter getCoverImageUrl() {
        return coverImageUrl;
    }

    public Optional<StringFilter> optionalCoverImageUrl() {
        return Optional.ofNullable(coverImageUrl);
    }

    public StringFilter coverImageUrl() {
        if (coverImageUrl == null) {
            setCoverImageUrl(new StringFilter());
        }
        return coverImageUrl;
    }

    public void setCoverImageUrl(StringFilter coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public InstantFilter getPublishDate() {
        return publishDate;
    }

    public Optional<InstantFilter> optionalPublishDate() {
        return Optional.ofNullable(publishDate);
    }

    public InstantFilter publishDate() {
        if (publishDate == null) {
            setPublishDate(new InstantFilter());
        }
        return publishDate;
    }

    public void setPublishDate(InstantFilter publishDate) {
        this.publishDate = publishDate;
    }

    public PostStatusFilter getStatus() {
        return status;
    }

    public Optional<PostStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public PostStatusFilter status() {
        if (status == null) {
            setStatus(new PostStatusFilter());
        }
        return status;
    }

    public void setStatus(PostStatusFilter status) {
        this.status = status;
    }

    public LongFilter getAuthorId() {
        return authorId;
    }

    public Optional<LongFilter> optionalAuthorId() {
        return Optional.ofNullable(authorId);
    }

    public LongFilter authorId() {
        if (authorId == null) {
            setAuthorId(new LongFilter());
        }
        return authorId;
    }

    public void setAuthorId(LongFilter authorId) {
        this.authorId = authorId;
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
        final BlogPostCriteria that = (BlogPostCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(summary, that.summary) &&
            Objects.equals(coverImageUrl, that.coverImageUrl) &&
            Objects.equals(publishDate, that.publishDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(authorId, that.authorId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, slug, summary, coverImageUrl, publishDate, status, authorId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlogPostCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalSlug().map(f -> "slug=" + f + ", ").orElse("") +
            optionalSummary().map(f -> "summary=" + f + ", ").orElse("") +
            optionalCoverImageUrl().map(f -> "coverImageUrl=" + f + ", ").orElse("") +
            optionalPublishDate().map(f -> "publishDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalAuthorId().map(f -> "authorId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

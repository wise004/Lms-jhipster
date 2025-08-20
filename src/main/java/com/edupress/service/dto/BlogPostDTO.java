package com.edupress.service.dto;

import com.edupress.domain.enumeration.PostStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.edupress.domain.BlogPost} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BlogPostDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String slug;

    private String summary;

    @Lob
    private String content;

    private String coverImageUrl;

    private Instant publishDate;

    @NotNull
    private PostStatus status;

    private AppUserDTO author;

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Instant getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Instant publishDate) {
        this.publishDate = publishDate;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public AppUserDTO getAuthor() {
        return author;
    }

    public void setAuthor(AppUserDTO author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlogPostDTO)) {
            return false;
        }

        BlogPostDTO blogPostDTO = (BlogPostDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, blogPostDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlogPostDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", slug='" + getSlug() + "'" +
            ", summary='" + getSummary() + "'" +
            ", content='" + getContent() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", publishDate='" + getPublishDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", author=" + getAuthor() +
            "}";
    }
}

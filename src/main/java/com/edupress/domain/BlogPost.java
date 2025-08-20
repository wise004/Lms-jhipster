package com.edupress.domain;

import com.edupress.domain.enumeration.PostStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BlogPost.
 */
@Entity
@Table(name = "blog_post")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BlogPost implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "summary")
    private String summary;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "publish_date")
    private Instant publishDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser author;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BlogPost id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public BlogPost title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return this.slug;
    }

    public BlogPost slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return this.summary;
    }

    public BlogPost summary(String summary) {
        this.setSummary(summary);
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return this.content;
    }

    public BlogPost content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public BlogPost coverImageUrl(String coverImageUrl) {
        this.setCoverImageUrl(coverImageUrl);
        return this;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Instant getPublishDate() {
        return this.publishDate;
    }

    public BlogPost publishDate(Instant publishDate) {
        this.setPublishDate(publishDate);
        return this;
    }

    public void setPublishDate(Instant publishDate) {
        this.publishDate = publishDate;
    }

    public PostStatus getStatus() {
        return this.status;
    }

    public BlogPost status(PostStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public AppUser getAuthor() {
        return this.author;
    }

    public void setAuthor(AppUser appUser) {
        this.author = appUser;
    }

    public BlogPost author(AppUser appUser) {
        this.setAuthor(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlogPost)) {
            return false;
        }
        return getId() != null && getId().equals(((BlogPost) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlogPost{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", slug='" + getSlug() + "'" +
            ", summary='" + getSummary() + "'" +
            ", content='" + getContent() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", publishDate='" + getPublishDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}

package com.edupress.domain;

import com.edupress.domain.enumeration.CourseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Course.
 */
@Entity
@Table(name = "course")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Course implements Serializable {

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

    @Column(name = "short_description")
    private String shortDescription;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "original_price", precision = 21, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "level")
    private String level;

    @Column(name = "language")
    private String language;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CourseStatus status;

    @NotNull
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished;

    @Column(name = "is_featured")
    private Boolean isFeatured;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "enrollment_count")
    private Integer enrollmentCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Course id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Course title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return this.slug;
    }

    public Course slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public Course shortDescription(String shortDescription) {
        this.setShortDescription(shortDescription);
        return this;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return this.description;
    }

    public Course description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public Course thumbnailUrl(String thumbnailUrl) {
        this.setThumbnailUrl(thumbnailUrl);
        return this;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Course price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return this.originalPrice;
    }

    public Course originalPrice(BigDecimal originalPrice) {
        this.setOriginalPrice(originalPrice);
        return this;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getLevel() {
        return this.level;
    }

    public Course level(String level) {
        this.setLevel(level);
        return this;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLanguage() {
        return this.language;
    }

    public Course language(String language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public CourseStatus getStatus() {
        return this.status;
    }

    public Course status(CourseStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public Boolean getIsPublished() {
        return this.isPublished;
    }

    public Course isPublished(Boolean isPublished) {
        this.setIsPublished(isPublished);
        return this;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Boolean getIsFeatured() {
        return this.isFeatured;
    }

    public Course isFeatured(Boolean isFeatured) {
        this.setIsFeatured(isFeatured);
        return this;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Double getAverageRating() {
        return this.averageRating;
    }

    public Course averageRating(Double averageRating) {
        this.setAverageRating(averageRating);
        return this;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getEnrollmentCount() {
        return this.enrollmentCount;
    }

    public Course enrollmentCount(Integer enrollmentCount) {
        this.setEnrollmentCount(enrollmentCount);
        return this;
    }

    public void setEnrollmentCount(Integer enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public AppUser getInstructor() {
        return this.instructor;
    }

    public void setInstructor(AppUser appUser) {
        this.instructor = appUser;
    }

    public Course instructor(AppUser appUser) {
        this.setInstructor(appUser);
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Course category(Category category) {
        this.setCategory(category);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        return getId() != null && getId().equals(((Course) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Course{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", slug='" + getSlug() + "'" +
            ", shortDescription='" + getShortDescription() + "'" +
            ", description='" + getDescription() + "'" +
            ", thumbnailUrl='" + getThumbnailUrl() + "'" +
            ", price=" + getPrice() +
            ", originalPrice=" + getOriginalPrice() +
            ", level='" + getLevel() + "'" +
            ", language='" + getLanguage() + "'" +
            ", status='" + getStatus() + "'" +
            ", isPublished='" + getIsPublished() + "'" +
            ", isFeatured='" + getIsFeatured() + "'" +
            ", averageRating=" + getAverageRating() +
            ", enrollmentCount=" + getEnrollmentCount() +
            "}";
    }
}

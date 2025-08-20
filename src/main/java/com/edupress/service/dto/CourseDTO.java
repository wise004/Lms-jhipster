package com.edupress.service.dto;

import com.edupress.domain.enumeration.CourseStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.edupress.domain.Course} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String slug;

    private String shortDescription;

    @Lob
    private String description;

    private String thumbnailUrl;

    @NotNull
    private BigDecimal price;

    private BigDecimal originalPrice;

    private String level;

    private String language;

    @NotNull
    private CourseStatus status;

    @NotNull
    private Boolean isPublished;

    private Boolean isFeatured;

    private Double averageRating;

    private Integer enrollmentCount;

    private AppUserDTO instructor;

    private CategoryDTO category;

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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(Integer enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public AppUserDTO getInstructor() {
        return instructor;
    }

    public void setInstructor(AppUserDTO instructor) {
        this.instructor = instructor;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseDTO)) {
            return false;
        }

        CourseDTO courseDTO = (CourseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseDTO{" +
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
            ", instructor=" + getInstructor() +
            ", category=" + getCategory() +
            "}";
    }
}

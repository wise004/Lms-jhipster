package com.edupress.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Review.
 */
@Entity
@Table(name = "review")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Lob
    @Column(name = "review_text")
    private String reviewText;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instructor", "category" }, allowSetters = true)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser student;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Review id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return this.rating;
    }

    public Review rating(Integer rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return this.reviewText;
    }

    public Review reviewText(String reviewText) {
        this.setReviewText(reviewText);
        return this;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Review createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Review course(Course course) {
        this.setCourse(course);
        return this;
    }

    public AppUser getStudent() {
        return this.student;
    }

    public void setStudent(AppUser appUser) {
        this.student = appUser;
    }

    public Review student(AppUser appUser) {
        this.setStudent(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Review)) {
            return false;
        }
        return getId() != null && getId().equals(((Review) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Review{" +
            "id=" + getId() +
            ", rating=" + getRating() +
            ", reviewText='" + getReviewText() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}

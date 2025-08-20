package com.edupress.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Quiz.
 */
@Entity
@Table(name = "quiz")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Quiz implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "attempts_allowed")
    private Integer attemptsAllowed;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Lob
    @Column(name = "questions")
    private String questions;

    @Column(name = "is_published")
    private Boolean isPublished;

    @Column(name = "available_from")
    private Instant availableFrom;

    @Column(name = "available_until")
    private Instant availableUntil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instructor", "category" }, allowSetters = true)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "course" }, allowSetters = true)
    private Lesson lesson;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Quiz id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Quiz title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Quiz description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTimeLimit() {
        return this.timeLimit;
    }

    public Quiz timeLimit(Integer timeLimit) {
        this.setTimeLimit(timeLimit);
        return this;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getPassingScore() {
        return this.passingScore;
    }

    public Quiz passingScore(Integer passingScore) {
        this.setPassingScore(passingScore);
        return this;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public Integer getAttemptsAllowed() {
        return this.attemptsAllowed;
    }

    public Quiz attemptsAllowed(Integer attemptsAllowed) {
        this.setAttemptsAllowed(attemptsAllowed);
        return this;
    }

    public void setAttemptsAllowed(Integer attemptsAllowed) {
        this.attemptsAllowed = attemptsAllowed;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public Quiz sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getQuestions() {
        return this.questions;
    }

    public Quiz questions(String questions) {
        this.setQuestions(questions);
        return this;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public Boolean getIsPublished() {
        return this.isPublished;
    }

    public Quiz isPublished(Boolean isPublished) {
        this.setIsPublished(isPublished);
        return this;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Instant getAvailableFrom() {
        return this.availableFrom;
    }

    public Quiz availableFrom(Instant availableFrom) {
        this.setAvailableFrom(availableFrom);
        return this;
    }

    public void setAvailableFrom(Instant availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Instant getAvailableUntil() {
        return this.availableUntil;
    }

    public Quiz availableUntil(Instant availableUntil) {
        this.setAvailableUntil(availableUntil);
        return this;
    }

    public void setAvailableUntil(Instant availableUntil) {
        this.availableUntil = availableUntil;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Quiz course(Course course) {
        this.setCourse(course);
        return this;
    }

    public Lesson getLesson() {
        return this.lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Quiz lesson(Lesson lesson) {
        this.setLesson(lesson);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quiz)) {
            return false;
        }
        return getId() != null && getId().equals(((Quiz) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Quiz{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", timeLimit=" + getTimeLimit() +
            ", passingScore=" + getPassingScore() +
            ", attemptsAllowed=" + getAttemptsAllowed() +
            ", sortOrder=" + getSortOrder() +
            ", questions='" + getQuestions() + "'" +
            ", isPublished='" + getIsPublished() + "'" +
            ", availableFrom='" + getAvailableFrom() + "'" +
            ", availableUntil='" + getAvailableUntil() + "'" +
            "}";
    }
}

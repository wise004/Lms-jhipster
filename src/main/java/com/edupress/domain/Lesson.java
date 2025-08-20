package com.edupress.domain;

import com.edupress.domain.enumeration.LessonType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Lesson.
 */
@Entity
@Table(name = "lesson")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Lesson implements Serializable {

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

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "duration")
    private Integer duration;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private LessonType type;

    @Column(name = "is_free")
    private Boolean isFree;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_published")
    private Boolean isPublished;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instructor", "category" }, allowSetters = true)
    private Course course;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Lesson id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Lesson title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Lesson description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return this.content;
    }

    public Lesson content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public Lesson videoUrl(String videoUrl) {
        this.setVideoUrl(videoUrl);
        return this;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public Lesson duration(Integer duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LessonType getType() {
        return this.type;
    }

    public Lesson type(LessonType type) {
        this.setType(type);
        return this;
    }

    public void setType(LessonType type) {
        this.type = type;
    }

    public Boolean getIsFree() {
        return this.isFree;
    }

    public Lesson isFree(Boolean isFree) {
        this.setIsFree(isFree);
        return this;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public Lesson sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsPublished() {
        return this.isPublished;
    }

    public Lesson isPublished(Boolean isPublished) {
        this.setIsPublished(isPublished);
        return this;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Lesson course(Course course) {
        this.setCourse(course);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lesson)) {
            return false;
        }
        return getId() != null && getId().equals(((Lesson) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Lesson{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", content='" + getContent() + "'" +
            ", videoUrl='" + getVideoUrl() + "'" +
            ", duration=" + getDuration() +
            ", type='" + getType() + "'" +
            ", isFree='" + getIsFree() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", isPublished='" + getIsPublished() + "'" +
            "}";
    }
}

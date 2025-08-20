package com.edupress.domain;

import com.edupress.domain.enumeration.EnrollmentStatus;
import com.edupress.domain.enumeration.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Enrollment.
 */
@Entity
@Table(name = "enrollment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "enrollment_date")
    private Instant enrollmentDate;

    @Column(name = "progress_percentage")
    private Integer progressPercentage;

    @Lob
    @Column(name = "progress")
    private String progress;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnrollmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "amount_paid", precision = 21, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "completed_at")
    private Instant completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instructor", "category" }, allowSetters = true)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser student;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Enrollment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getEnrollmentDate() {
        return this.enrollmentDate;
    }

    public Enrollment enrollmentDate(Instant enrollmentDate) {
        this.setEnrollmentDate(enrollmentDate);
        return this;
    }

    public void setEnrollmentDate(Instant enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Integer getProgressPercentage() {
        return this.progressPercentage;
    }

    public Enrollment progressPercentage(Integer progressPercentage) {
        this.setProgressPercentage(progressPercentage);
        return this;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getProgress() {
        return this.progress;
    }

    public Enrollment progress(String progress) {
        this.setProgress(progress);
        return this;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public Instant getLastAccessedAt() {
        return this.lastAccessedAt;
    }

    public Enrollment lastAccessedAt(Instant lastAccessedAt) {
        this.setLastAccessedAt(lastAccessedAt);
        return this;
    }

    public void setLastAccessedAt(Instant lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public EnrollmentStatus getStatus() {
        return this.status;
    }

    public Enrollment status(EnrollmentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public Enrollment paymentStatus(PaymentStatus paymentStatus) {
        this.setPaymentStatus(paymentStatus);
        return this;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getAmountPaid() {
        return this.amountPaid;
    }

    public Enrollment amountPaid(BigDecimal amountPaid) {
        this.setAmountPaid(amountPaid);
        return this;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public Enrollment transactionId(String transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public Enrollment completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Enrollment course(Course course) {
        this.setCourse(course);
        return this;
    }

    public AppUser getStudent() {
        return this.student;
    }

    public void setStudent(AppUser appUser) {
        this.student = appUser;
    }

    public Enrollment student(AppUser appUser) {
        this.setStudent(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Enrollment)) {
            return false;
        }
        return getId() != null && getId().equals(((Enrollment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Enrollment{" +
            "id=" + getId() +
            ", enrollmentDate='" + getEnrollmentDate() + "'" +
            ", progressPercentage=" + getProgressPercentage() +
            ", progress='" + getProgress() + "'" +
            ", lastAccessedAt='" + getLastAccessedAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", amountPaid=" + getAmountPaid() +
            ", transactionId='" + getTransactionId() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            "}";
    }
}

package com.edupress.service.dto;

import com.edupress.domain.enumeration.EnrollmentStatus;
import com.edupress.domain.enumeration.PaymentStatus;
import jakarta.persistence.Lob;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.edupress.domain.Enrollment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnrollmentDTO implements Serializable {

    private Long id;

    private Instant enrollmentDate;

    private Integer progressPercentage;

    @Lob
    private String progress;

    private Instant lastAccessedAt;

    private EnrollmentStatus status;

    private PaymentStatus paymentStatus;

    private BigDecimal amountPaid;

    private String transactionId;

    private Instant completedAt;

    private CourseDTO course;

    private AppUserDTO student;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Instant enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public Instant getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(Instant lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public AppUserDTO getStudent() {
        return student;
    }

    public void setStudent(AppUserDTO student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnrollmentDTO)) {
            return false;
        }

        EnrollmentDTO enrollmentDTO = (EnrollmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, enrollmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnrollmentDTO{" +
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
            ", course=" + getCourse() +
            ", student=" + getStudent() +
            "}";
    }
}

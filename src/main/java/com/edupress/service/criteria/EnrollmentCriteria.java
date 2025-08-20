package com.edupress.service.criteria;

import com.edupress.domain.enumeration.EnrollmentStatus;
import com.edupress.domain.enumeration.PaymentStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.edupress.domain.Enrollment} entity. This class is used
 * in {@link com.edupress.web.rest.EnrollmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /enrollments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnrollmentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EnrollmentStatus
     */
    public static class EnrollmentStatusFilter extends Filter<EnrollmentStatus> {

        public EnrollmentStatusFilter() {}

        public EnrollmentStatusFilter(EnrollmentStatusFilter filter) {
            super(filter);
        }

        @Override
        public EnrollmentStatusFilter copy() {
            return new EnrollmentStatusFilter(this);
        }
    }

    /**
     * Class for filtering PaymentStatus
     */
    public static class PaymentStatusFilter extends Filter<PaymentStatus> {

        public PaymentStatusFilter() {}

        public PaymentStatusFilter(PaymentStatusFilter filter) {
            super(filter);
        }

        @Override
        public PaymentStatusFilter copy() {
            return new PaymentStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter enrollmentDate;

    private IntegerFilter progressPercentage;

    private InstantFilter lastAccessedAt;

    private EnrollmentStatusFilter status;

    private PaymentStatusFilter paymentStatus;

    private BigDecimalFilter amountPaid;

    private StringFilter transactionId;

    private InstantFilter completedAt;

    private LongFilter courseId;

    private LongFilter studentId;

    private Boolean distinct;

    public EnrollmentCriteria() {}

    public EnrollmentCriteria(EnrollmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.enrollmentDate = other.optionalEnrollmentDate().map(InstantFilter::copy).orElse(null);
        this.progressPercentage = other.optionalProgressPercentage().map(IntegerFilter::copy).orElse(null);
        this.lastAccessedAt = other.optionalLastAccessedAt().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(EnrollmentStatusFilter::copy).orElse(null);
        this.paymentStatus = other.optionalPaymentStatus().map(PaymentStatusFilter::copy).orElse(null);
        this.amountPaid = other.optionalAmountPaid().map(BigDecimalFilter::copy).orElse(null);
        this.transactionId = other.optionalTransactionId().map(StringFilter::copy).orElse(null);
        this.completedAt = other.optionalCompletedAt().map(InstantFilter::copy).orElse(null);
        this.courseId = other.optionalCourseId().map(LongFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EnrollmentCriteria copy() {
        return new EnrollmentCriteria(this);
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

    public InstantFilter getEnrollmentDate() {
        return enrollmentDate;
    }

    public Optional<InstantFilter> optionalEnrollmentDate() {
        return Optional.ofNullable(enrollmentDate);
    }

    public InstantFilter enrollmentDate() {
        if (enrollmentDate == null) {
            setEnrollmentDate(new InstantFilter());
        }
        return enrollmentDate;
    }

    public void setEnrollmentDate(InstantFilter enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public IntegerFilter getProgressPercentage() {
        return progressPercentage;
    }

    public Optional<IntegerFilter> optionalProgressPercentage() {
        return Optional.ofNullable(progressPercentage);
    }

    public IntegerFilter progressPercentage() {
        if (progressPercentage == null) {
            setProgressPercentage(new IntegerFilter());
        }
        return progressPercentage;
    }

    public void setProgressPercentage(IntegerFilter progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public InstantFilter getLastAccessedAt() {
        return lastAccessedAt;
    }

    public Optional<InstantFilter> optionalLastAccessedAt() {
        return Optional.ofNullable(lastAccessedAt);
    }

    public InstantFilter lastAccessedAt() {
        if (lastAccessedAt == null) {
            setLastAccessedAt(new InstantFilter());
        }
        return lastAccessedAt;
    }

    public void setLastAccessedAt(InstantFilter lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public EnrollmentStatusFilter getStatus() {
        return status;
    }

    public Optional<EnrollmentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public EnrollmentStatusFilter status() {
        if (status == null) {
            setStatus(new EnrollmentStatusFilter());
        }
        return status;
    }

    public void setStatus(EnrollmentStatusFilter status) {
        this.status = status;
    }

    public PaymentStatusFilter getPaymentStatus() {
        return paymentStatus;
    }

    public Optional<PaymentStatusFilter> optionalPaymentStatus() {
        return Optional.ofNullable(paymentStatus);
    }

    public PaymentStatusFilter paymentStatus() {
        if (paymentStatus == null) {
            setPaymentStatus(new PaymentStatusFilter());
        }
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatusFilter paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimalFilter getAmountPaid() {
        return amountPaid;
    }

    public Optional<BigDecimalFilter> optionalAmountPaid() {
        return Optional.ofNullable(amountPaid);
    }

    public BigDecimalFilter amountPaid() {
        if (amountPaid == null) {
            setAmountPaid(new BigDecimalFilter());
        }
        return amountPaid;
    }

    public void setAmountPaid(BigDecimalFilter amountPaid) {
        this.amountPaid = amountPaid;
    }

    public StringFilter getTransactionId() {
        return transactionId;
    }

    public Optional<StringFilter> optionalTransactionId() {
        return Optional.ofNullable(transactionId);
    }

    public StringFilter transactionId() {
        if (transactionId == null) {
            setTransactionId(new StringFilter());
        }
        return transactionId;
    }

    public void setTransactionId(StringFilter transactionId) {
        this.transactionId = transactionId;
    }

    public InstantFilter getCompletedAt() {
        return completedAt;
    }

    public Optional<InstantFilter> optionalCompletedAt() {
        return Optional.ofNullable(completedAt);
    }

    public InstantFilter completedAt() {
        if (completedAt == null) {
            setCompletedAt(new InstantFilter());
        }
        return completedAt;
    }

    public void setCompletedAt(InstantFilter completedAt) {
        this.completedAt = completedAt;
    }

    public LongFilter getCourseId() {
        return courseId;
    }

    public Optional<LongFilter> optionalCourseId() {
        return Optional.ofNullable(courseId);
    }

    public LongFilter courseId() {
        if (courseId == null) {
            setCourseId(new LongFilter());
        }
        return courseId;
    }

    public void setCourseId(LongFilter courseId) {
        this.courseId = courseId;
    }

    public LongFilter getStudentId() {
        return studentId;
    }

    public Optional<LongFilter> optionalStudentId() {
        return Optional.ofNullable(studentId);
    }

    public LongFilter studentId() {
        if (studentId == null) {
            setStudentId(new LongFilter());
        }
        return studentId;
    }

    public void setStudentId(LongFilter studentId) {
        this.studentId = studentId;
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
        final EnrollmentCriteria that = (EnrollmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(enrollmentDate, that.enrollmentDate) &&
            Objects.equals(progressPercentage, that.progressPercentage) &&
            Objects.equals(lastAccessedAt, that.lastAccessedAt) &&
            Objects.equals(status, that.status) &&
            Objects.equals(paymentStatus, that.paymentStatus) &&
            Objects.equals(amountPaid, that.amountPaid) &&
            Objects.equals(transactionId, that.transactionId) &&
            Objects.equals(completedAt, that.completedAt) &&
            Objects.equals(courseId, that.courseId) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            enrollmentDate,
            progressPercentage,
            lastAccessedAt,
            status,
            paymentStatus,
            amountPaid,
            transactionId,
            completedAt,
            courseId,
            studentId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnrollmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEnrollmentDate().map(f -> "enrollmentDate=" + f + ", ").orElse("") +
            optionalProgressPercentage().map(f -> "progressPercentage=" + f + ", ").orElse("") +
            optionalLastAccessedAt().map(f -> "lastAccessedAt=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalPaymentStatus().map(f -> "paymentStatus=" + f + ", ").orElse("") +
            optionalAmountPaid().map(f -> "amountPaid=" + f + ", ").orElse("") +
            optionalTransactionId().map(f -> "transactionId=" + f + ", ").orElse("") +
            optionalCompletedAt().map(f -> "completedAt=" + f + ", ").orElse("") +
            optionalCourseId().map(f -> "courseId=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

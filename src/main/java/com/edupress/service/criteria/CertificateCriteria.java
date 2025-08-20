package com.edupress.service.criteria;

import com.edupress.domain.enumeration.CertificateStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.edupress.domain.Certificate} entity. This class is used
 * in {@link com.edupress.web.rest.CertificateResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /certificates?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CertificateCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CertificateStatus
     */
    public static class CertificateStatusFilter extends Filter<CertificateStatus> {

        public CertificateStatusFilter() {}

        public CertificateStatusFilter(CertificateStatusFilter filter) {
            super(filter);
        }

        @Override
        public CertificateStatusFilter copy() {
            return new CertificateStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter url;

    private InstantFilter issuedAt;

    private CertificateStatusFilter status;

    private LongFilter enrollmentId;

    private Boolean distinct;

    public CertificateCriteria() {}

    public CertificateCriteria(CertificateCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.url = other.optionalUrl().map(StringFilter::copy).orElse(null);
        this.issuedAt = other.optionalIssuedAt().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(CertificateStatusFilter::copy).orElse(null);
        this.enrollmentId = other.optionalEnrollmentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CertificateCriteria copy() {
        return new CertificateCriteria(this);
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

    public StringFilter getUrl() {
        return url;
    }

    public Optional<StringFilter> optionalUrl() {
        return Optional.ofNullable(url);
    }

    public StringFilter url() {
        if (url == null) {
            setUrl(new StringFilter());
        }
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public InstantFilter getIssuedAt() {
        return issuedAt;
    }

    public Optional<InstantFilter> optionalIssuedAt() {
        return Optional.ofNullable(issuedAt);
    }

    public InstantFilter issuedAt() {
        if (issuedAt == null) {
            setIssuedAt(new InstantFilter());
        }
        return issuedAt;
    }

    public void setIssuedAt(InstantFilter issuedAt) {
        this.issuedAt = issuedAt;
    }

    public CertificateStatusFilter getStatus() {
        return status;
    }

    public Optional<CertificateStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public CertificateStatusFilter status() {
        if (status == null) {
            setStatus(new CertificateStatusFilter());
        }
        return status;
    }

    public void setStatus(CertificateStatusFilter status) {
        this.status = status;
    }

    public LongFilter getEnrollmentId() {
        return enrollmentId;
    }

    public Optional<LongFilter> optionalEnrollmentId() {
        return Optional.ofNullable(enrollmentId);
    }

    public LongFilter enrollmentId() {
        if (enrollmentId == null) {
            setEnrollmentId(new LongFilter());
        }
        return enrollmentId;
    }

    public void setEnrollmentId(LongFilter enrollmentId) {
        this.enrollmentId = enrollmentId;
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
        final CertificateCriteria that = (CertificateCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(url, that.url) &&
            Objects.equals(issuedAt, that.issuedAt) &&
            Objects.equals(status, that.status) &&
            Objects.equals(enrollmentId, that.enrollmentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, issuedAt, status, enrollmentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CertificateCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUrl().map(f -> "url=" + f + ", ").orElse("") +
            optionalIssuedAt().map(f -> "issuedAt=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalEnrollmentId().map(f -> "enrollmentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

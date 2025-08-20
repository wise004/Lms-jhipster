package com.edupress.service.dto;

import com.edupress.domain.enumeration.CertificateStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.edupress.domain.Certificate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CertificateDTO implements Serializable {

    private Long id;

    @NotNull
    private String url;

    @NotNull
    private Instant issuedAt;

    @NotNull
    private CertificateStatus status;

    private EnrollmentDTO enrollment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateStatus status) {
        this.status = status;
    }

    public EnrollmentDTO getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(EnrollmentDTO enrollment) {
        this.enrollment = enrollment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertificateDTO)) {
            return false;
        }

        CertificateDTO certificateDTO = (CertificateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, certificateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CertificateDTO{" +
            "id=" + getId() +
            ", url='" + getUrl() + "'" +
            ", issuedAt='" + getIssuedAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", enrollment=" + getEnrollment() +
            "}";
    }
}

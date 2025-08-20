package com.edupress.domain;

import com.edupress.domain.enumeration.CertificateStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Certificate.
 */
@Entity
@Table(name = "certificate")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Certificate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @NotNull
    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CertificateStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "course", "student" }, allowSetters = true)
    private Enrollment enrollment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Certificate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public Certificate url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Instant getIssuedAt() {
        return this.issuedAt;
    }

    public Certificate issuedAt(Instant issuedAt) {
        this.setIssuedAt(issuedAt);
        return this;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public CertificateStatus getStatus() {
        return this.status;
    }

    public Certificate status(CertificateStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CertificateStatus status) {
        this.status = status;
    }

    public Enrollment getEnrollment() {
        return this.enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Certificate enrollment(Enrollment enrollment) {
        this.setEnrollment(enrollment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Certificate)) {
            return false;
        }
        return getId() != null && getId().equals(((Certificate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Certificate{" +
            "id=" + getId() +
            ", url='" + getUrl() + "'" +
            ", issuedAt='" + getIssuedAt() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}

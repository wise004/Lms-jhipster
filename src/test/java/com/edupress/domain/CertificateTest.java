package com.edupress.domain;

import static com.edupress.domain.CertificateTestSamples.*;
import static com.edupress.domain.EnrollmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edupress.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CertificateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Certificate.class);
        Certificate certificate1 = getCertificateSample1();
        Certificate certificate2 = new Certificate();
        assertThat(certificate1).isNotEqualTo(certificate2);

        certificate2.setId(certificate1.getId());
        assertThat(certificate1).isEqualTo(certificate2);

        certificate2 = getCertificateSample2();
        assertThat(certificate1).isNotEqualTo(certificate2);
    }

    @Test
    void enrollmentTest() {
        Certificate certificate = getCertificateRandomSampleGenerator();
        Enrollment enrollmentBack = getEnrollmentRandomSampleGenerator();

        certificate.setEnrollment(enrollmentBack);
        assertThat(certificate.getEnrollment()).isEqualTo(enrollmentBack);

        certificate.enrollment(null);
        assertThat(certificate.getEnrollment()).isNull();
    }
}

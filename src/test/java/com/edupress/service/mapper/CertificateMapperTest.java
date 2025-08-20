package com.edupress.service.mapper;

import static com.edupress.domain.CertificateAsserts.*;
import static com.edupress.domain.CertificateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CertificateMapperTest {

    private CertificateMapper certificateMapper;

    @BeforeEach
    void setUp() {
        certificateMapper = new CertificateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCertificateSample1();
        var actual = certificateMapper.toEntity(certificateMapper.toDto(expected));
        assertCertificateAllPropertiesEquals(expected, actual);
    }
}

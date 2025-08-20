package com.edupress.service.mapper;

import com.edupress.domain.Certificate;
import com.edupress.domain.Enrollment;
import com.edupress.service.dto.CertificateDTO;
import com.edupress.service.dto.EnrollmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Certificate} and its DTO {@link CertificateDTO}.
 */
@Mapper(componentModel = "spring")
public interface CertificateMapper extends EntityMapper<CertificateDTO, Certificate> {
    @Mapping(target = "enrollment", source = "enrollment", qualifiedByName = "enrollmentId")
    CertificateDTO toDto(Certificate s);

    @Named("enrollmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EnrollmentDTO toDtoEnrollmentId(Enrollment enrollment);
}

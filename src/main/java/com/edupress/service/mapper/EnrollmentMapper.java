package com.edupress.service.mapper;

import com.edupress.domain.AppUser;
import com.edupress.domain.Course;
import com.edupress.domain.Enrollment;
import com.edupress.service.dto.AppUserDTO;
import com.edupress.service.dto.CourseDTO;
import com.edupress.service.dto.EnrollmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Enrollment} and its DTO {@link EnrollmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnrollmentMapper extends EntityMapper<EnrollmentDTO, Enrollment> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseId")
    @Mapping(target = "student", source = "student", qualifiedByName = "appUserEmail")
    EnrollmentDTO toDto(Enrollment s);

    @Named("courseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoCourseId(Course course);

    @Named("appUserEmail")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    AppUserDTO toDtoAppUserEmail(AppUser appUser);
}

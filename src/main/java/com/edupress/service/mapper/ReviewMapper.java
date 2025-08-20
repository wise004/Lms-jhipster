package com.edupress.service.mapper;

import com.edupress.domain.AppUser;
import com.edupress.domain.Course;
import com.edupress.domain.Review;
import com.edupress.service.dto.AppUserDTO;
import com.edupress.service.dto.CourseDTO;
import com.edupress.service.dto.ReviewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Review} and its DTO {@link ReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper extends EntityMapper<ReviewDTO, Review> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseId")
    @Mapping(target = "student", source = "student", qualifiedByName = "appUserEmail")
    ReviewDTO toDto(Review s);

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

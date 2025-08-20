package com.edupress.service.mapper;

import com.edupress.domain.AppUser;
import com.edupress.domain.Category;
import com.edupress.domain.Course;
import com.edupress.service.dto.AppUserDTO;
import com.edupress.service.dto.CategoryDTO;
import com.edupress.service.dto.CourseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Course} and its DTO {@link CourseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper extends EntityMapper<CourseDTO, Course> {
    @Mapping(target = "instructor", source = "instructor", qualifiedByName = "appUserEmail")
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryName")
    CourseDTO toDto(Course s);

    @Named("appUserEmail")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    AppUserDTO toDtoAppUserEmail(AppUser appUser);

    @Named("categoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDtoCategoryName(Category category);
}

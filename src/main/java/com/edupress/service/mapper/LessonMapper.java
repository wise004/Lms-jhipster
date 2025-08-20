package com.edupress.service.mapper;

import com.edupress.domain.Course;
import com.edupress.domain.Lesson;
import com.edupress.service.dto.CourseDTO;
import com.edupress.service.dto.LessonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Lesson} and its DTO {@link LessonDTO}.
 */
@Mapper(componentModel = "spring")
public interface LessonMapper extends EntityMapper<LessonDTO, Lesson> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseId")
    LessonDTO toDto(Lesson s);

    @Named("courseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoCourseId(Course course);
}

package com.edupress.service.mapper;

import com.edupress.domain.Assignment;
import com.edupress.domain.Course;
import com.edupress.domain.Lesson;
import com.edupress.service.dto.AssignmentDTO;
import com.edupress.service.dto.CourseDTO;
import com.edupress.service.dto.LessonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Assignment} and its DTO {@link AssignmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssignmentMapper extends EntityMapper<AssignmentDTO, Assignment> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseId")
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonId")
    AssignmentDTO toDto(Assignment s);

    @Named("courseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoCourseId(Course course);

    @Named("lessonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LessonDTO toDtoLessonId(Lesson lesson);
}

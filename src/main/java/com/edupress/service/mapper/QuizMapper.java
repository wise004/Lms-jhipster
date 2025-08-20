package com.edupress.service.mapper;

import com.edupress.domain.Course;
import com.edupress.domain.Lesson;
import com.edupress.domain.Quiz;
import com.edupress.service.dto.CourseDTO;
import com.edupress.service.dto.LessonDTO;
import com.edupress.service.dto.QuizDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Quiz} and its DTO {@link QuizDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuizMapper extends EntityMapper<QuizDTO, Quiz> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseId")
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonId")
    QuizDTO toDto(Quiz s);

    @Named("courseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoCourseId(Course course);

    @Named("lessonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LessonDTO toDtoLessonId(Lesson lesson);
}

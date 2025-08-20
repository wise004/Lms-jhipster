package com.edupress.service.mapper;

import com.edupress.domain.AppUser;
import com.edupress.domain.Comment;
import com.edupress.domain.Lesson;
import com.edupress.service.dto.AppUserDTO;
import com.edupress.service.dto.CommentDTO;
import com.edupress.service.dto.LessonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonId")
    @Mapping(target = "author", source = "author", qualifiedByName = "appUserEmail")
    @Mapping(target = "parent", source = "parent", qualifiedByName = "commentId")
    CommentDTO toDto(Comment s);

    @Named("lessonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LessonDTO toDtoLessonId(Lesson lesson);

    @Named("appUserEmail")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    AppUserDTO toDtoAppUserEmail(AppUser appUser);

    @Named("commentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommentDTO toDtoCommentId(Comment comment);
}

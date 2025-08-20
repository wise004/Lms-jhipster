package com.edupress.service.mapper;

import com.edupress.domain.AppUser;
import com.edupress.domain.Quiz;
import com.edupress.domain.QuizAttempt;
import com.edupress.service.dto.AppUserDTO;
import com.edupress.service.dto.QuizAttemptDTO;
import com.edupress.service.dto.QuizDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link QuizAttempt} and its DTO {@link QuizAttemptDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuizAttemptMapper extends EntityMapper<QuizAttemptDTO, QuizAttempt> {
    @Mapping(target = "quiz", source = "quiz", qualifiedByName = "quizId")
    @Mapping(target = "student", source = "student", qualifiedByName = "appUserEmail")
    QuizAttemptDTO toDto(QuizAttempt s);

    @Named("quizId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    QuizDTO toDtoQuizId(Quiz quiz);

    @Named("appUserEmail")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    AppUserDTO toDtoAppUserEmail(AppUser appUser);
}

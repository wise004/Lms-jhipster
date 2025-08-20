package com.edupress.service.mapper;

import com.edupress.domain.AppUser;
import com.edupress.domain.Assignment;
import com.edupress.domain.AssignmentSubmission;
import com.edupress.service.dto.AppUserDTO;
import com.edupress.service.dto.AssignmentDTO;
import com.edupress.service.dto.AssignmentSubmissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AssignmentSubmission} and its DTO {@link AssignmentSubmissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssignmentSubmissionMapper extends EntityMapper<AssignmentSubmissionDTO, AssignmentSubmission> {
    @Mapping(target = "assignment", source = "assignment", qualifiedByName = "assignmentId")
    @Mapping(target = "student", source = "student", qualifiedByName = "appUserEmail")
    AssignmentSubmissionDTO toDto(AssignmentSubmission s);

    @Named("assignmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AssignmentDTO toDtoAssignmentId(Assignment assignment);

    @Named("appUserEmail")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    AppUserDTO toDtoAppUserEmail(AppUser appUser);
}

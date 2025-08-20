package com.edupress.service.mapper;

import com.edupress.domain.AppUser;
import com.edupress.service.dto.AppUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppUser} and its DTO {@link AppUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppUserMapper extends EntityMapper<AppUserDTO, AppUser> {}

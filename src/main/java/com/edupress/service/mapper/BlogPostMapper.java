package com.edupress.service.mapper;

import com.edupress.domain.AppUser;
import com.edupress.domain.BlogPost;
import com.edupress.service.dto.AppUserDTO;
import com.edupress.service.dto.BlogPostDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BlogPost} and its DTO {@link BlogPostDTO}.
 */
@Mapper(componentModel = "spring")
public interface BlogPostMapper extends EntityMapper<BlogPostDTO, BlogPost> {
    @Mapping(target = "author", source = "author", qualifiedByName = "appUserEmail")
    BlogPostDTO toDto(BlogPost s);

    @Named("appUserEmail")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    AppUserDTO toDtoAppUserEmail(AppUser appUser);
}

package com.edupress.service.mapper;

import static com.edupress.domain.BlogPostAsserts.*;
import static com.edupress.domain.BlogPostTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlogPostMapperTest {

    private BlogPostMapper blogPostMapper;

    @BeforeEach
    void setUp() {
        blogPostMapper = new BlogPostMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBlogPostSample1();
        var actual = blogPostMapper.toEntity(blogPostMapper.toDto(expected));
        assertBlogPostAllPropertiesEquals(expected, actual);
    }
}

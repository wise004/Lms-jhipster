package com.edupress.domain;

import static com.edupress.domain.AppUserTestSamples.*;
import static com.edupress.domain.BlogPostTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edupress.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BlogPostTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BlogPost.class);
        BlogPost blogPost1 = getBlogPostSample1();
        BlogPost blogPost2 = new BlogPost();
        assertThat(blogPost1).isNotEqualTo(blogPost2);

        blogPost2.setId(blogPost1.getId());
        assertThat(blogPost1).isEqualTo(blogPost2);

        blogPost2 = getBlogPostSample2();
        assertThat(blogPost1).isNotEqualTo(blogPost2);
    }

    @Test
    void authorTest() {
        BlogPost blogPost = getBlogPostRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        blogPost.setAuthor(appUserBack);
        assertThat(blogPost.getAuthor()).isEqualTo(appUserBack);

        blogPost.author(null);
        assertThat(blogPost.getAuthor()).isNull();
    }
}

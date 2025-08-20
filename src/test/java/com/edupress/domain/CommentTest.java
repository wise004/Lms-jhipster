package com.edupress.domain;

import static com.edupress.domain.AppUserTestSamples.*;
import static com.edupress.domain.CommentTestSamples.*;
import static com.edupress.domain.CommentTestSamples.*;
import static com.edupress.domain.LessonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.edupress.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comment.class);
        Comment comment1 = getCommentSample1();
        Comment comment2 = new Comment();
        assertThat(comment1).isNotEqualTo(comment2);

        comment2.setId(comment1.getId());
        assertThat(comment1).isEqualTo(comment2);

        comment2 = getCommentSample2();
        assertThat(comment1).isNotEqualTo(comment2);
    }

    @Test
    void lessonTest() {
        Comment comment = getCommentRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        comment.setLesson(lessonBack);
        assertThat(comment.getLesson()).isEqualTo(lessonBack);

        comment.lesson(null);
        assertThat(comment.getLesson()).isNull();
    }

    @Test
    void authorTest() {
        Comment comment = getCommentRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        comment.setAuthor(appUserBack);
        assertThat(comment.getAuthor()).isEqualTo(appUserBack);

        comment.author(null);
        assertThat(comment.getAuthor()).isNull();
    }

    @Test
    void parentTest() {
        Comment comment = getCommentRandomSampleGenerator();
        Comment commentBack = getCommentRandomSampleGenerator();

        comment.setParent(commentBack);
        assertThat(comment.getParent()).isEqualTo(commentBack);

        comment.parent(null);
        assertThat(comment.getParent()).isNull();
    }
}

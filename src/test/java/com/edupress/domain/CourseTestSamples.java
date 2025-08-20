package com.edupress.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Course getCourseSample1() {
        return new Course()
            .id(1L)
            .title("title1")
            .slug("slug1")
            .shortDescription("shortDescription1")
            .thumbnailUrl("thumbnailUrl1")
            .level("level1")
            .language("language1")
            .enrollmentCount(1);
    }

    public static Course getCourseSample2() {
        return new Course()
            .id(2L)
            .title("title2")
            .slug("slug2")
            .shortDescription("shortDescription2")
            .thumbnailUrl("thumbnailUrl2")
            .level("level2")
            .language("language2")
            .enrollmentCount(2);
    }

    public static Course getCourseRandomSampleGenerator() {
        return new Course()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .shortDescription(UUID.randomUUID().toString())
            .thumbnailUrl(UUID.randomUUID().toString())
            .level(UUID.randomUUID().toString())
            .language(UUID.randomUUID().toString())
            .enrollmentCount(intCount.incrementAndGet());
    }
}

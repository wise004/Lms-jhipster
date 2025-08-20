package com.edupress.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AssignmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Assignment getAssignmentSample1() {
        return new Assignment()
            .id(1L)
            .title("title1")
            .maxPoints(1)
            .submissionType("submissionType1")
            .maxFileSize(1)
            .lateSubmissionPenalty(1)
            .sortOrder(1);
    }

    public static Assignment getAssignmentSample2() {
        return new Assignment()
            .id(2L)
            .title("title2")
            .maxPoints(2)
            .submissionType("submissionType2")
            .maxFileSize(2)
            .lateSubmissionPenalty(2)
            .sortOrder(2);
    }

    public static Assignment getAssignmentRandomSampleGenerator() {
        return new Assignment()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .maxPoints(intCount.incrementAndGet())
            .submissionType(UUID.randomUUID().toString())
            .maxFileSize(intCount.incrementAndGet())
            .lateSubmissionPenalty(intCount.incrementAndGet())
            .sortOrder(intCount.incrementAndGet());
    }
}

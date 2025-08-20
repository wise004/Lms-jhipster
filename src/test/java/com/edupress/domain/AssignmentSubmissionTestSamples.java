package com.edupress.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AssignmentSubmissionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static AssignmentSubmission getAssignmentSubmissionSample1() {
        return new AssignmentSubmission().id(1L).grade(1);
    }

    public static AssignmentSubmission getAssignmentSubmissionSample2() {
        return new AssignmentSubmission().id(2L).grade(2);
    }

    public static AssignmentSubmission getAssignmentSubmissionRandomSampleGenerator() {
        return new AssignmentSubmission().id(longCount.incrementAndGet()).grade(intCount.incrementAndGet());
    }
}

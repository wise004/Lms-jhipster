package com.edupress.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EnrollmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Enrollment getEnrollmentSample1() {
        return new Enrollment().id(1L).progressPercentage(1).transactionId("transactionId1");
    }

    public static Enrollment getEnrollmentSample2() {
        return new Enrollment().id(2L).progressPercentage(2).transactionId("transactionId2");
    }

    public static Enrollment getEnrollmentRandomSampleGenerator() {
        return new Enrollment()
            .id(longCount.incrementAndGet())
            .progressPercentage(intCount.incrementAndGet())
            .transactionId(UUID.randomUUID().toString());
    }
}

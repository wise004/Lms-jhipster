package com.edupress.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QuizAttemptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static QuizAttempt getQuizAttemptSample1() {
        return new QuizAttempt().id(1L).score(1).attemptNumber(1);
    }

    public static QuizAttempt getQuizAttemptSample2() {
        return new QuizAttempt().id(2L).score(2).attemptNumber(2);
    }

    public static QuizAttempt getQuizAttemptRandomSampleGenerator() {
        return new QuizAttempt()
            .id(longCount.incrementAndGet())
            .score(intCount.incrementAndGet())
            .attemptNumber(intCount.incrementAndGet());
    }
}

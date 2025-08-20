package com.edupress.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CertificateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Certificate getCertificateSample1() {
        return new Certificate().id(1L).url("url1");
    }

    public static Certificate getCertificateSample2() {
        return new Certificate().id(2L).url("url2");
    }

    public static Certificate getCertificateRandomSampleGenerator() {
        return new Certificate().id(longCount.incrementAndGet()).url(UUID.randomUUID().toString());
    }
}

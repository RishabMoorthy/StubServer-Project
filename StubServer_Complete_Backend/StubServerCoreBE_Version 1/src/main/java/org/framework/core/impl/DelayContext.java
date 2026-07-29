package org.framework.core.impl;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class DelayContext {

    private final AtomicInteger totalCount = new AtomicInteger(0);
    private final AtomicInteger delayedCount = new AtomicInteger(0);

    public int applyDelayIfRequired(int maxDelayed, int delayMs, int delayPercent, int totalTxn) {
        System.out.println("maxDelayed :" + maxDelayed + ":delayMs :" + delayMs + "delayPercent :" + delayPercent);
        System.out.println(totalCount + "   " + delayedCount);
        if (totalCount.incrementAndGet() > totalTxn) {
            totalCount.set(1);
            delayedCount.set(0);
        };

        if (delayedCount.get() >= maxDelayed) {
            return 0;
        }

        int chance = ThreadLocalRandom.current().nextInt(100);
        System.out.println("chance " + chance);
        if (chance < delayPercent) {

            if (delayedCount.incrementAndGet() <= maxDelayed) {
                try {
                    return delayMs;
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        } else if ((totalTxn - totalCount.get()) <= (maxDelayed - delayedCount.get())) {
            if (delayedCount.incrementAndGet() <= maxDelayed) {
                return delayMs;
            }
        }
        return 0;
    }
}

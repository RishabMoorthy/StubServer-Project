package org.framework.utils;

import org.common.db.config.ConfigLoader;
import org.framework.properties.Context;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vs.met.VSMetricsResptoDB;

public class ServiceRespTimeHandler {

    private static final long FLUSH_INTERVAL_MS = 60_000L; // 1 minute

    // Scheduler (single thread to serialize flushes)
    private ScheduledExecutorService scheduler;

    // Shared metrics
    private final AtomicInteger counter = new AtomicInteger(0);
    private final AtomicLong totalRespTime = new AtomicLong(0);
    private final AtomicLong maxRespTime = new AtomicLong(0);
    private final AtomicLong lastFlushedMinuteKey = new AtomicLong(Long.MIN_VALUE);
    private String vsname = "";
    private volatile String insertStartTime;
    private int idleCounter = 0;
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss.SSS");
    private static final ZoneId ZONE = ZoneId.systemDefault();
    // Window start timestamp (set on first request of each window)

    // Guard to ensure only one flush runs
    private final AtomicBoolean isFlushing = new AtomicBoolean(false);

    public void start() {
        if (scheduler != null)
            if (!scheduler.isShutdown())
                return;
        // start timer if schedular is shut down or schedular is null
        // insertStartTime =
        // LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy
        // HH:mm:ss.SSS"));
        // DO NOT set insertStartTime here — we set it when the **first request**
        // arrives
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::flushIfDue, FLUSH_INTERVAL_MS, FLUSH_INTERVAL_MS, TimeUnit.MILLISECONDS);
        System.out.println("Service started. Metrics will flush every 1 minute.");
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                idleCounter = 0;
            } catch (InterruptedException ie) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void onRequest(Context context) {
        long startTime = System.currentTimeMillis();
        String vsname = context.getMockService().getName();
        context.getRespTimeProperties().put(vsname + "startTime", startTime);

        // If this is the first request of the window, stamp the window start now
        /*
         * if (insertStartTime == null) {
         * insertStartTime = LocalDateTime.now().format(DateTimeFormatter.
         * ofPattern("MM-dd-yyyy HH:mm:ss.SSS"));
         * }
         */
    }

    public void afterRequest(Context context) {
        String vsname = context.getMockService().getName();
        Object startObj = context.getRespTimeProperties().get(vsname + "startTime");
        if (startObj == null) {
            // Defensive: missing start time — skip
            return;
        }

        long diff = System.currentTimeMillis() - (long) startObj;

        // Update metrics (atomic, thread-safe)
        counter.incrementAndGet();
        totalRespTime.addAndGet(diff);
        maxRespTime.updateAndGet(prev -> Math.max(prev, diff));
        this.vsname = vsname;

        System.out.println("Service=" + vsname +
                " | ResponseTime=" + diff + " ms" +
                " | Count=" + counter.get() +
                " | WindowStart=" + (insertStartTime != null ? insertStartTime : "n/a"));
    }

    // Timer tick: flush once per minute (no dependence on requests)
    private void flushIfDue() {
        if (!isFlushing.compareAndSet(false, true)) {
            return; // another flush in progress
        }
        try {
            flushMetrics();
        } finally {
            isFlushing.set(false);
        }
    }

    synchronized private void flushMetrics() {
        // Atomically capture & reset
        int count = counter.getAndSet(0);
        long total = totalRespTime.getAndSet(0L);
        long max = maxRespTime.getAndSet(0L);
        System.out.println(count + " " + insertStartTime);
        // No requests in the last minute → skip writing
        if (count <= 0) {
            System.out.println("[FLUSH-SKIP] No data in this interval for service." + this.vsname);
            idleCounter++;
            if (idleCounter >= 1) {
                Logger.getInstance().info("Stopping resp timer for " + vsname);
                stop();
            }
            // Keep insertStartTime as-is (null) so the next request will set a fresh start
            return;
        }

        double avg = (count > 0) ? (total * 1.0 / count) : 0.0;
        // Example: if now = 12:34:10, we flush for 12:33:00.000 .. 12:33:59.999
        long nowMs = System.currentTimeMillis();
        long previousMinuteKey = (nowMs / 60_000L) - 1; // strictly previous minute
        long startMs = previousMinuteKey * 60_000L; // HH:mm:00.000
        long endDisplayMs = (previousMinuteKey + 1) * 60_000L - 1; // HH:mm:59.999

        // NEW: Prevent duplicate flush of the same aligned minute
        long lastKey = lastFlushedMinuteKey.get();
        if (previousMinuteKey == lastKey) {
            // Already flushed this minute; skip
            return;
        }

        String insertStartTime = formatTs(startMs);
        String insertEndTime = formatTs(endDisplayMs);

        System.out.println("\n==== Flushing Metrics for ====" + vsname);
        System.out.println("Total Requests: " + count);
        System.out.println("Max Response Time: " + max + " ms");
        System.out.println("Total Response Time: " + total + " ms");
        System.out.println("Avg Response Time: " + avg + " ms");
        System.out.println("Start Time: " + insertStartTime);
        System.out.println("End Time: " + insertEndTime);
        System.out.println("server : " + CustomMethods.getLocalHostAddress());
        System.out.println("========================\n");

        // Persist
        try {
            com.vs.met.VSMetricsResptoDB vsm = new com.vs.met.VSMetricsResptoDB();
            String msg = vsm.saveRespMetrics(vsname, CustomMethods.getLocalHostAddress(),
                    insertStartTime, insertEndTime, total, count, max);
            System.out.println("DB Save: " + msg);
        } catch (Exception e) {
            System.err.println("DB saveRespMetrics failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Rotate window: the next window should start when the next request arrives
        // insertStartTime =
        // LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy
        // HH:mm:ss.SSS"));
    }

    private static String formatTs(long epochMs) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMs), ZONE).format(TS_FMT);
    }
}

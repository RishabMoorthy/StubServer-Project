package org.framework.core;

import org.framework.db.service.DailyMetricsService;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.*;

public class TransactionCounter {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService dbExecutor = Executors.newFixedThreadPool(8);
    private static String currentDate = getTodayDate();

    public static void schedular() {
        scheduler.scheduleAtFixedRate(() -> dbExecutor.submit(TransactionCounter::flushCountsToDB), 5, 1, TimeUnit.SECONDS);
    }

    private static void flushCountsToDB() {
        /* String today = getTodayDate();
        Map<String, EndpointsStats> logs = RequestTrackerDB.getAllServicesLogs();

        if (logs.isEmpty()) return;

        try (Connection conn = Utility.getInstance().getOracleDBConnection()) {
            conn.setAutoCommit(false);

            String mergeSQL = "MERGE INTO " + ConfigLoader.getProperty("stubserver.metrics") + " m " +
                    "USING (SELECT ? AS vsname, ? AS transdate, ? AS count, ? AS virtservername, ? AS envtype FROM dual) src " +
                    "ON (m.vsname = src.vsname AND m.transdate = src.transdate AND m.virtservername = src.virtservername) " +
                    "WHEN MATCHED THEN UPDATE SET m.count = m.count + src.count, " +
                    " m.envtype = (CASE WHEN (m.count + src.count) > 500 THEN 'PERF' ELSE 'QA' END) " +

                    "WHEN NOT MATCHED THEN INSERT (METRICSID, VSNAME, TRANSDATE, COUNT, VIRTSERVERNAME, ENVTYPE) " +
                    "VALUES (metricsid_seq.NEXTVAL, src.vsname, src.transdate, src.count, src.virtservername, "+ "CASE WHEN src.count > 500 THEN 'PERF' ELSE 'QA' END)";

            try (PreparedStatement stmt = conn.prepareStatement(mergeSQL)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                java.sql.Date sqlDate = new java.sql.Date(sdf.parse(today).getTime());
                String ip = CustomMethods.getLocalHostAddress();

                for (Map.Entry<String, EndpointsStats> entry : logs.entrySet()) {
                    String vsName = entry.getKey();
                    long count = entry.getValue().getRequestCount().get();
                    String envType = (count > 500) ? "PERF" : "QA";

                    stmt.setString(1, vsName);
                    stmt.setDate(2, sqlDate);
                    stmt.setLong(3, count);
                    stmt.setString(4, ip);
                    stmt.setString(5, envType);
                    stmt.addBatch();
                }

                stmt.executeBatch();
                conn.commit();
            }

            logs.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        new DailyMetricsService().flushCountsToDB();
    }

    private static String getTodayDate() {
        return new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH).format(new Date());
    }
}

package org.framework.db.service;

import org.framework.core.EndpointsStats;
import org.framework.core.RequestTrackerDB;

import org.common.db.entity.DailyMetrics;
import org.common.db.repository.DailyMetricsRepository;
import org.common.db.config.ConfigLoader;
import org.framework.utils.CustomMethods;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class DailyMetricsService {

    private final DailyMetricsRepository repo = new DailyMetricsRepository();

    public void flushCountsToDB() {

        Map<String, EndpointsStats> logs = RequestTrackerDB.getAllServicesLogs();

        if (logs.isEmpty()) return;

        try {

            Date today = new java.sql.Date(
                    new java.text.SimpleDateFormat("dd-MMM-yy")
                            .parse(getTodayDate()).getTime()
            );

            String ip = CustomMethods.getLocalHostAddress();

            for (Map.Entry<String, EndpointsStats> entry : logs.entrySet()) {

                String vsName = entry.getKey();
                long count = entry.getValue().getRequestCount().get();
                String envType = (count > 500) ? "PERF" : "QA";

                Optional<DailyMetrics> existing =
                        repo.findByKey(vsName, today, ip);

                if (existing.isPresent()) {

                    // UPDATE
                    DailyMetrics e = existing.get();
                    long newCount = e.getCount() + count;

                    e.setCount(newCount);
                    e.setEnvType(newCount > 500 ? "PERF" : "QA");

                    repo.update(e);

                } else {

                    // INSERT
                    DailyMetrics e = new DailyMetrics();
                    e.setVsName(vsName);
                    e.setTransDate(today);
                    e.setVirtServerName(ip);
                    e.setCount(count);
                    e.setEnvType(envType);

                    repo.save(e);
                }
            }

            logs.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTodayDate() {
        return new java.text.SimpleDateFormat("dd-MMM-yy",
                java.util.Locale.ENGLISH)
                .format(new java.util.Date());
    }
}

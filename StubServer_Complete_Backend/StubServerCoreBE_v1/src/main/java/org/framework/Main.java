package org.framework;

import com.stubio.util.VirtualServiceObject;
import org.framework.config.LogConfigManager;
import org.framework.core.*;
import org.common.db.*;
import org.framework.db.Utility;
import org.framework.services.SystemServices;
import org.framework.utils.Logger;
import org.json.JSONArray;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static final Map<String, AbstractService> services = new ConcurrentHashMap<>();
    static SystemServices ss;
    public static String upandRunning = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a"));

    public static void main(String[] args) {
        try {
            System.out.println("user.dir = " + System.getProperty("user.dir"));

            // create derby db tables
            // DerbyDb.configureDb();
            // Initialize DB pool early
            // Utility.getInstance();
            System.out.println("Database connection pool initiated");

            DataSourceManager.getInstance();
            DbMigrationRunner.migrateIfEnabled();
            HibernateUtil.getSessionFactory();

            // Schedule transaction counter
            TransactionCounter.schedular();

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("JVM shutdown hook activated");
                if (ss != null) {
                    try {
                        ss.stop();
                        System.out.println("System service stopped");
                    } catch (Exception stopEx) {
                        System.err.println("Error during system services shutdown");
                        stopEx.printStackTrace();
                    }
                }
                HibernateUtil.shutdown();
                System.out.println("Database connection pool closed");
            }, "App-Shutdown-Hook-Thread"));

            // Fetch services from DB
            JSONArray serviceList = Utility.getInstance().getServicesFromDb();

            if (serviceList != null && !serviceList.isEmpty()) {
                ExecutorService executor = Executors.newFixedThreadPool(Math.min(serviceList.length(), 8));

                for (int i = 0; i < serviceList.length(); i++) {
                    int index = i;
                    executor.submit(() -> {
                        try {
                            File fileDir = new File("vsfiles");
                            if (!fileDir.exists()) {
                                fileDir.mkdirs();
                            }

                            File file = new File(fileDir, serviceList.getJSONObject(index).get("serviceName") + ".xml");
                            String xmlContent = Files.readString(file.toPath());

                            VirtualServiceObject parsedObj = ServerManager.getInstance().parseXml(file);
                            // NOTE: original parameter list was partially obscured/blurry in the
                            // source photo; verify this against the real source before compiling.
                            ServerManager.getInstance().deployService(parsedObj, false, "default", "", "",
                                    "", false, "");

                            LogConfigManager.setDays(
                                    Integer.parseInt(serviceList.getJSONObject(index).getString("keepReqResLogsDays")));
                            LogConfigManager.getConfig().put(
                                    serviceList.getJSONObject(index).getString("serviceName"),
                                    serviceList.getJSONObject(index).getString("keepReqResLogs"));
                            RespTimeConfigManager.getConfig().put(
                                    serviceList.getJSONObject(index).getString("serviceName"),
                                    serviceList.getJSONObject(index).getString("saveRespTime"));
                        } catch (Exception e) {
                            Logger.getInstance().error(e);
                        }
                    });
                }

                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.MINUTES);
                System.out.println("Initialized map: " + LogConfigManager.getConfig());
            }

            ss = new SystemServices();
            ss.start();

        } catch (Exception e) {
            e.printStackTrace();
            Logger.getInstance().error(e);
            HibernateUtil.shutdown();
            System.exit(1);
        }
    }
}

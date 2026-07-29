package org.framework.utils;

import org.framework.config.LogConfigManager;
import org.framework.core.RequestLog;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReqResLogger {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final String LOG_DIR = "logs/reqreslogs/";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void log(String serviceName, RequestLog request) throws IOException {
        Map<String, String> config = LogConfigManager.getConfig();
        if (!"Yes".equalsIgnoreCase(config.getOrDefault(serviceName, "No")))
            return;

        LocalDate today = LocalDate.now();
        String dateStr = today.format(formatter);
        String baseFileName = LOG_DIR + serviceName + "_" + dateStr;

        File logFile = getLogFile(baseFileName);
        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(formatLogEntry(request));
            bw.newLine();
        }

        cleanupOldLogs(serviceName, LogConfigManager.getDays());
    }

    private File getLogFile(String baseFileName) throws IOException {
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            if (!logDir.mkdirs()) {
                throw new IOException("Failed to create log directory: " + LOG_DIR);
            }
        } else if (!logDir.isDirectory()) {
            throw new IOException("Log path exists but is not a directory: " + LOG_DIR);
        }

        int index = 0;
        File file;

        while (true) {
            String fileName = baseFileName + (index == 0 ? ".log" : "_" + index + ".log");
            file = new File(fileName);
            if (!file.exists() || file.length() < MAX_FILE_SIZE) {
                return file;
            }
            index++;
        }
    }

    private void cleanupOldLogs(String serviceName, int retentionDays) {
        File logDir = new File(LOG_DIR);
        if (!logDir.exists() || !logDir.isDirectory())
            return;

        File[] files = logDir.listFiles((dir, name) -> name.startsWith(serviceName + "_") && name.endsWith(".log"));
        if (files == null)
            return;

        LocalDate cutoffDate = LocalDate.now().minusDays(retentionDays);
        System.out.println("cutoffdate " + cutoffDate + retentionDays);
        Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

        for (File file : files) {
            String name = file.getName();
            Matcher matcher = datePattern.matcher(name);
            if (matcher.find()) {
                String datePart = matcher.group();
                try {
                    LocalDate fileDate = LocalDate.parse(datePart, formatter);
                    if (fileDate.isBefore(cutoffDate)) {
                        file.delete();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse date from file: " + name);
                }
            }
        }
    }

    private String formatLogEntry(RequestLog log) {
        return String.format(
                "[%s] Service: %s | Path: %s | Port: %d | Status: %s | Request: %s | Response: %s",
                log.getTimestamp(),
                log.getServiceName(),
                log.getPath(),
                log.getPort(),
                log.getStatus(),
                log.getRequest().getRequestContent() != null ? log.getRequest().getRequestContent() : "null",
                log.getResponse().getResponseContent() != null ? log.getResponse().getResponseContent() : "null");
    }
}

package org.framework.utils;

import org.framework.properties.Context;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Logger {

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    private LogLevel currentLevel;
    private PrintWriter writer;
    private PrintWriter errorWriter;
    private boolean logToFile;

    private long maxFileSizeBytes = 5 * 1024 * 1024; // 5 MB
    private String logFileName;
    private String errorLogFileName;
    private File logDir = new File("logs");

    private static Logger instance;

    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger("stubserver.log", LogLevel.DEBUG, true);
        }
        return instance;
    }

    public Logger(String logFileName, LogLevel level, boolean logToFile) {
        this.currentLevel = level;
        this.logToFile = logToFile;
        this.logFileName = logFileName;
        this.errorLogFileName = logFileName.replace(".log", "-error.log");

        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        File logFile = new File(logDir, logFileName);
        File errorFile = new File(logDir, errorLogFileName);

        if (logFile.exists() && logFile.length() >= maxFileSizeBytes) {
            rotateFile(logFile);
        }

        if (errorFile.exists() && errorFile.length() >= maxFileSizeBytes) {
            rotateFile(errorFile);
        }

        try {
            if (logToFile) {
                writer = new PrintWriter(new FileWriter(logFile, true), true);
                errorWriter = new PrintWriter(new FileWriter(errorFile, true), true);
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    public void log(LogLevel level, String message) {
        if (level.ordinal() >= currentLevel.ordinal()) {
            String logMessage = formatMessage(level, message);
            System.out.println(logMessage);

            if (logToFile) {
                if (level == LogLevel.ERROR && errorWriter != null) {
                    checkAndRotate(errorLogFileName, true);
                    errorWriter.println(logMessage);
                }

                if (writer != null) {
                    checkAndRotate(logFileName, false);
                    writer.println(logMessage);
                }
            }
        }
    }

    private void checkAndRotate(String fileName, boolean isError) {
        File file = new File(logDir, fileName);
        if (file.exists() && file.length() >= maxFileSizeBytes) {
            if (isError && errorWriter != null) {
                errorWriter.close();
            } else if (!isError && writer != null) {
                writer.close();
            }

            rotateFile(file);

            try {
                if (isError) {
                    errorWriter = new PrintWriter(new FileWriter(file, false), true);
                } else {
                    writer = new PrintWriter(new FileWriter(file, false), true);
                }
            } catch (IOException e) {
                System.err.println("Failed to reopen log file: " + e.getMessage());
            }
        }
    }

    private void rotateFile(File baseFile) {
        String baseName = baseFile.getName().replace(".log", "");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String rotatedName = baseName + "_" + timestamp + ".log";
        File rotatedFile = new File(logDir, rotatedName);
        boolean success = baseFile.renameTo(rotatedFile);
        if (!success) {
            System.err.println("Failed to rotate log file to: " + rotatedFile.getName());
        }
    }

    private String formatMessage(LogLevel level, String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return String.format("[%s] [%s] %s", timestamp, level, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public String exceptionMsg(Exception e) {
        // Exception class name
        String errorType = e.getClass().getSimpleName();

        // Exception message
        String errorMessage = e.getMessage();

        // Where it failed (first stack trace element)
        StackTraceElement element = e.getStackTrace()[0];
        String location = element.getClassName() + "." + element.getMethodName()
                + " (line " + element.getLineNumber() + ")";

        // Build clean error summary
        String errorSummary = String.format("%s at : %s",
                errorType, errorMessage);

        // Log it or return in API response
        return errorSummary;
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public void error(Exception e, Context context) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        Logger.getInstance().error("Error occurred in " + context.mockService.getName() + "\n" + sw.toString());
    }

    public void info(Object... args) {
        String message = formatArgs(args);
        System.out.println(message);
        log(LogLevel.INFO, message);
    }

    private String formatArgs(Object... args) {
        return Arrays.stream(args)
                .map(arg -> arg != null ? arg.toString() : "null")
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }

    public void info(Object o) {
        System.out.println(o);
        log(LogLevel.INFO, String.valueOf(o));
    }

    public void warn(Object... args) {
        String message = formatArgs(args);
        System.out.println("[WARN] " + message);
        log(LogLevel.WARN, message);
    }

    public void warn(Object o) {
        System.out.println("[WARN] " + o);
        log(LogLevel.WARN, String.valueOf(o));
    }

    public void error(Object... args) {
        String message = formatArgs(args);
        System.out.println("[ERROR] " + message);
        log(LogLevel.ERROR, message);
    }

    public void error(Object o) {
        System.out.println("[ERROR] " + o);
        log(LogLevel.ERROR, String.valueOf(o));
    }

    public void error(String name, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        Logger.getInstance().error("Error occurred in " + name + "\n" + sw.toString());
    }

    public void close() {
        if (logToFile) {
            if (writer != null)
                writer.close();
            if (errorWriter != null)
                errorWriter.close();
        }
    }
}

package org.framework.utils;

import org.framework.db.Utility;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

public class FolderDeleteService {

    private static final String BASE_PATH = "vsfiles/dataset";

    public static void deleteServiceFolder(String serviceName) {
        try {
            // find data source flag from db . if false then return
            JSONObject obj = Utility.getInstance().getVSData(serviceName);

            Object val = obj.get("datasourceenabled");

            if (val == null || val.toString().trim().isEmpty()) {
                return;
            }

            boolean datasourceflag_db = Boolean.parseBoolean(val.toString());

            if (!datasourceflag_db) {
                return;
            }

            if (serviceName.contains("..") || serviceName.contains("/") || serviceName.contains("\\")) {
                throw new IllegalArgumentException("Invalid service name");
            }

            Path targetPath = Paths.get(BASE_PATH, serviceName).normalize();

            if (Files.exists(targetPath) && Files.isDirectory(targetPath)) {

                // Delete everything including the main folder
                Files.walk(targetPath)
                        .sorted(Comparator.reverseOrder()) // delete children first
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to delete: " + path, e);
                            }
                        });

                Logger.getInstance().info("Deleted folder: " + targetPath);

            } else {
                Logger.getInstance().info("Folder not found: " + targetPath);
            }

        } catch (Exception e) {
            System.err.println("Error deleting folder: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        deleteServiceFolder("serviceA");
    }
}

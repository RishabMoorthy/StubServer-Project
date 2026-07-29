package org.framework.db.service;

import org.common.db.entity.MasterCatalog;
import org.common.db.repository.MasterCatalogRepository;

import java.util.List;
import java.util.Optional;

public class MasterCatalogService {
    private final MasterCatalogRepository repository = new MasterCatalogRepository();

    public boolean storeServiceInMasterCatalog(
            String serviceName,
            int port,
            String backendApplication,
            String group,
            String backendType,
            String envType)
    {

        // Step 1: Check if any record exists for port
        Optional<MasterCatalog> existingByPort =
                repository.findAnyByPort(port);

        if (existingByPort.isEmpty()) {

            // No record for port -> FIRST ENTRY -> ACTIVE
            MasterCatalog entity = buildEntity(serviceName, backendApplication, group, backendType, envType, port, "ACTIVE");

            repository.save(entity);
            return true;
        }

        // Step 2: Check if THIS service exists on that port
        Optional<MasterCatalog> existingService =
                repository.findByPortAndServiceName(port, serviceName);

        // Step 3: Check ACTIVE service on that port
        Optional<MasterCatalog> activeService =
                repository.findByPortAndStatus(port, "ACTIVE");

        if (existingService.isEmpty()) {

            // New service for this port
            String status = activeService.isEmpty() ? "ACTIVE" : "INACTIVE";

            MasterCatalog entity = buildEntity(serviceName, backendApplication, group, backendType, envType, port, status);

            repository.save(entity);
            return true;

        } else {

            // Existing service -> update
            MasterCatalog dbObj = existingService.get();

            String status = activeService.isEmpty() ? "ACTIVE" : "INACTIVE";

            dbObj.setStatus(status);
            dbObj.setBackendApplication(backendApplication);
            dbObj.setBackendType(backendType);
            dbObj.setGroup(group);
            dbObj.setEnvType(envType);
            dbObj.setUpdateTime(new java.util.Date());

            repository.update(dbObj);
            return true;
        }
    }

    private MasterCatalog buildEntity(
            String serviceName,
            String backendApplication,
            String group,
            String backendType,
            String envType,
            int port,
            String status) {

        MasterCatalog entity = new MasterCatalog();

        Long nextMasterId = repository.findMaxMasterId() + 1;
        entity.setMasterId(nextMasterId);

        entity.setVsName(serviceName);
        entity.setBackendApplication(backendApplication);
        entity.setBackendType(backendType);
        entity.setGroup(group);
        entity.setEnvType(envType);
        entity.setPort(port);
        entity.setStatus(status);
        entity.setUpdateTime(new java.util.Date());

        return entity;
    }

    public boolean updateServiceFromCatalogDb(
            String vsName,
            String status,
            int port) {

        Optional<MasterCatalog> existingService =
                repository.findByPortAndServiceName(port, vsName);

        if (existingService.isEmpty()) {
            return false;
        }

        MasterCatalog entity = existingService.get();

        // ONLY update status (as per utility)
        entity.setStatus(status);
        entity.setUpdateTime(new java.util.Date());

        repository.update(entity);

        return true;
    }

    public boolean isExistingService(String serviceName, int port) {
        return repository.findByPortAndName(port, serviceName).isPresent();
    }

    public boolean deleteServiceFromCatalogDb(String vsName) {

        // Find ALL entries with this vsName (since original query does same)
        List<MasterCatalog> services =
                repository.findByServiceName(vsName);

        if (services.isEmpty()) {
            return false;
        }

        for (MasterCatalog entity : services) {

            // Soft delete
            entity.setStatus("INACTIVE");
            entity.setUpdateTime(new java.util.Date());

            repository.update(entity);
        }

        return true;
    }
}

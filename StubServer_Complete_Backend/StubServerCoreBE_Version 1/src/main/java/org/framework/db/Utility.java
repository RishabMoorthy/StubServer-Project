package org.framework.db;

import org.framework.config.ServiceConfig;
import org.framework.core.AbstractService;
import org.framework.db.service.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class Utility {

    private static Utility instance = new Utility();

    // ✅ Inject services
    private final VsService vsService = new VsService();
    private final MasterCatalogService masterService = new MasterCatalogService();
    private final ExecutionDetailsService execService = new ExecutionDetailsService();

    public static Utility getInstance() {
        return instance;
    }

    private Utility() {
        // No DB init here anymore (optional: keep if needed globally)
    }

    // ================================
    // ✅ VS METHODS
    // ================================

    public JSONObject getVSData(String vsName) {
        return vsService.getVSData(vsName);
    }

    public boolean storeServiceInDataBase(String serviceName,
            AbstractService service,
            boolean isDeployedFromUI) {
        return vsService.storeServiceInDataBase(serviceName, service, isDeployedFromUI);
    }

    public JSONArray getServicesFromDb() {
        return vsService.getServicesFromDb();
    }

    public void updateServiceStatusInDb(AbstractService service, String status) {
        vsService.updateServiceStatusInDb(service.getName(), status);
    }

    public void deleteServiceFromDb(String vsName) {
        vsService.deleteServiceFromDb(vsName);
    }

    public void updateServiceLogInDB(String serviceName,
            String keepReqresLogs,
            int keepLogsdays) {
        vsService.updateServiceLogInDB(serviceName, keepReqresLogs, keepLogsdays);
    }

    public void updateServiceRespTimeConfigInDB(String serviceName, String saveRespTime) {
        vsService.updateServiceRespTimeConfigInDB(serviceName, saveRespTime);
    }

    public void updateCustomDelayConfigInDB(String serviceName,
            Integer delay, String delayMode,
            Integer upperMs, Integer lowerMs,
            Double Median, Double sigma,
            int totalTxn, int delayPercent) {
        vsService.updateCustomDelayConfigInDB(
                serviceName, delay, delayMode, upperMs, lowerMs, Median, sigma, totalTxn, delayPercent);
    }

    public void setCustomDelayConfig(ServiceConfig config, String virtServer) {
        vsService.setCustomDelayConfig(config);
    }

    // ================================
    // ✅ MASTER CATALOG METHODS
    // ================================

    public boolean isExistingService(String serviceName, int port) {
        return masterService.isExistingService(serviceName, port);
    }

    public boolean storeServiceInMasterCatalog(String serviceName,
            AbstractService service,
            String backendApplication,
            String group,
            String backendType,
            String envType) {

        return masterService.storeServiceInMasterCatalog(
                serviceName, service.getConfig().getPort(), backendApplication, group, backendType, envType);
    }

    public void updateServiceFromCatalogDb(String vsName, String status, int port) {

        masterService.updateServiceFromCatalogDb(vsName, status, port);
    }

    public void deleteServiceFromCatalogDb(String vsName) {
        masterService.deleteServiceFromCatalogDb(vsName);
    }

    // ================================
    // ✅ EXECUTION MODE METHODS
    // ================================

    public void setExecutionModeAndHost(String vsName, String virtServer) {
        execService.setExecutionModeAndHost(vsName, virtServer);
    }
}

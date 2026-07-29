package org.framework.db.service;

import org.framework.config.ServiceConfig;
import org.framework.core.AbstractService;
import org.common.db.entity.VSDetails;
import org.common.db.repository.VsRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VsService {

    private final VsRepository repo;

    // Default constructor for normal application usage
    public VsService() {
        this.repo = new VsRepository();
    }

    // Constructor for unit testing
    public VsService(VsRepository repo) {
        this.repo = repo;
    }

    public JSONObject getVSData(String vsName) {
        Optional<VSDetails> entity = repo.findByName(vsName);

        JSONObject resp = new JSONObject();
        resp.put("serviceName", vsName);

        entity.ifPresent(e -> {
            resp.put("id", e.getVsid());
            resp.put("port", e.getPort());
            resp.put("status", e.getStatus());
            resp.put("datasourceenabled", e.getDatasourceEnabled());
        });

        return resp;
    }

    public JSONArray getServicesFromDb() {
        List<VSDetails> list = repo.findAll();

        JSONArray array = new JSONArray();
        for (VSDetails e : list) {
            JSONObject o = new JSONObject();
            o.put("serviceName", e.getVsName());
            o.put("status", e.getStatus());
            o.put("port", e.getPort());
            o.put("keepReqResLogs", e.getKeepReqResLogs());
            o.put("keepReqResLogsDays", e.getKeepReqResLogsDays());
            o.put("lastupdated", e.getLastUpdated());
            o.put("user", e.getUsername());
            o.put("saveRespTime", e.getSaveRespTime());
            o.put("group",
                    e.getGroup() != null ? e.getGroup() : "");
            // tags - split by comma
            String tags = e.getTags();
            if (tags != null && !tags.trim().isEmpty()) {
                o.put("tags",
                        Arrays.asList(tags.split("\\s*,\\s*")));
            } else {
                o.put("tags", new JSONArray());
            }
            array.put(o);
        }
        return array;
    }

    public void updateServiceStatusInDb(String vsName, String status) {

        Optional<VSDetails> entityOpt = repo.findByName(vsName);

        if (entityOpt.isEmpty()) {
            return;
        }

        VSDetails entity = entityOpt.get();
        entity.setStatus(status);

        repo.update(entity);
    }

    public boolean storeServiceInDataBase(String vsName,
            AbstractService service,
            boolean isDeployedFromUI) {

        VSDetails entity = repo.findByName(vsName)
                .orElse(new VSDetails());

        Optional<VSDetails> existing = repo.findByName(vsName);
        boolean isNew = existing.isEmpty();
        if (isNew || isDeployedFromUI) {
            entity.setLastUpdated(service.getTimestamp());
        }

        entity.setVsName(vsName);
        entity.setPort(service.getConfig().getPort());
        entity.setUsername(service.getConfig().getUserName());
        entity.setStatus(service.isRunning() ? "Running" : "Stopped");
        entity.setDatasourceEnabled(String.valueOf(service.getConfig().isDatasourceEnabled()));

        repo.save(entity);
        return true;
    }

    public void updateServiceLogInDB(String vsName,
            String keepReqResLogs,
            Integer keepLogsDays) {

        // Case 1: keepLogsDays applies to ALL records (no WHERE in original query)
        if (keepLogsDays != null) {
            List<VSDetails> all = repo.findAll();

            for (VSDetails e : all) {
                e.setKeepReqResLogsDays(String.valueOf(keepLogsDays));
                repo.update(e);
            }
        }

        // Case 2: keepReqResLogs
        if (keepReqResLogs != null) {

            if ("All".equalsIgnoreCase(vsName)) {
                // Update all
                List<VSDetails> all = repo.findAll();

                for (VSDetails e : all) {
                    e.setKeepReqResLogs(keepReqResLogs);
                    repo.update(e);
                }

            } else {
                // Update specific
                Optional<VSDetails> entityOpt = repo.findByName(vsName);

                if (entityOpt.isPresent()) {
                    VSDetails e = entityOpt.get();
                    e.setKeepReqResLogs(keepReqResLogs);
                    repo.update(e);
                }
            }
        }
    }

    public void deleteServiceFromDb(String vsName) {
        repo.deleteByName(vsName);
    }

    public void updateServiceRespTimeConfigInDB(String vsName, String saveRespTime) {
        Optional<VSDetails> entityOpt = repo.findByName(vsName);

        if (entityOpt.isEmpty())
            return;

        VSDetails entity = entityOpt.get();
        entity.setSaveRespTime(saveRespTime);

        repo.update(entity);
    }

    public void updateCustomDelayConfigInDB(String vsName,
            Integer delay, String delayMode,
            Integer upperMs, Integer lowerMs,
            Double median, Double sigma,
            int totalTxn, int delayPercent) {

        Optional<VSDetails> entityOpt = repo.findByName(vsName);

        if (entityOpt.isEmpty())
            return;

        VSDetails e = entityOpt.get();

        e.setDelay(delay);
        e.setDelayMode(delayMode);
        e.setUpperMs(upperMs);
        e.setLowerMs(lowerMs);

        if (median != null) {
            e.setMedianMs(BigDecimal.valueOf(median));
        } else {
            e.setMedianMs(null);
        }

        if (sigma != null) {
            e.setSigma(BigDecimal.valueOf(sigma));
        } else {
            e.setSigma(null);
        }

        e.setTotalTxn(totalTxn);
        e.setDelayPercent(delayPercent);

        repo.update(e);
    }

    /*
     * //
     * public void setExecutionModeAndHost(String vsName, String virtServer) {
     * List<Object[]> results =
     * execRepo.findExecutionDetails(vsName, virtServer);
     *
     * for (Object[] row : results) {
     * String host = (String) row[0];
     * String mode = (String) row[1];
     *
     * GlobalProperties.setPropertyValue(vsName + "_ExecutionMode", mode);
     * GlobalProperties.setPropertyValue(vsName + "_HostDetail", host);
     * }
     * }
     */

    public void setCustomDelayConfig(ServiceConfig config) {

        Optional<VSDetails> row = repo.findByName(config.getServiceName());

        if (row.isEmpty())
            return;

        VSDetails e = row.get();

        config.setDelayMs(e.getDelay() != null ? e.getDelay() : 0);
        config.setDelayMode(e.getDelayMode());
        config.setUpperMs(e.getUpperMs() != null ? e.getUpperMs() : 0);
        config.setLowerMs(e.getLowerMs() != null ? e.getLowerMs() : 0);

        config.setStandardDeviation(
                e.getSigma() != null ? e.getSigma().doubleValue() : 0.0);

        config.setMedianMs(
                e.getMedianMs() != null ? e.getMedianMs().doubleValue() : 0.0);

        config.setTotalTxn(
                e.getTotalTxn() != null ? e.getTotalTxn() : 0);

        config.setDelayPercent(
                e.getDelayPercent() != null ? e.getDelayPercent() : 0);

        System.out.println("delay: " + config.getDelayMs()
                + " mode " + config.getDelayMode()
                + " " + config.getLowerMs()
                + " " + config.getUpperMs());
    }
}

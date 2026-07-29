package org.framework.db.service;

import org.common.db.entity.ExecutionDetailsDTO;
import org.common.db.repository.ExecutionDetailsRepository;
import org.common.db.config.ConfigLoader;
import org.framework.utils.CustomMethods;
import org.framework.utils.GlobalProperties;
import org.framework.utils.Logger;

import java.util.Optional;

public class ExecutionDetailsService {

    private final ExecutionDetailsRepository repository = new ExecutionDetailsRepository();

    public void setExecutionModeAndHost(String vsName, String virtServer) {
        try {
            Optional<ExecutionDetailsDTO> result =
                    repository.findExecutionDetails(vsName, virtServer);
            if (result.isPresent()) {
                ExecutionDetailsDTO data = result.get();

                String host = data.getHost();
                String executionMode = data.getExecutionMode();

                Logger.getInstance().info("host: " + host + " execution mode " + executionMode);
                GlobalProperties.setPropertyValue(
                        "virtserverip",
                        CustomMethods.getLocalHostAddress()
                );
                GlobalProperties.setPropertyValue(vsName + "_ExecutionMode", executionMode);
                GlobalProperties.setPropertyValue(vsName + "_HostDetail", host);

            } else {
                Logger.getInstance().info("No data found for vsName=" + vsName
                        + ", virtServer=" + virtServer);
            }

        } catch (Exception e) {
            Logger.getInstance().info("[ExecutionDetailsService] Failed to load execution details for vsName="
                    + vsName + ", virtServer=" + virtServer
                    + ", Continuing service startup.");
            e.printStackTrace();
        }
    }
}

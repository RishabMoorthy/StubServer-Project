package org.framework.core;

import org.framework.config.LogConfigManager;
import org.framework.config.ServiceConfig;
import org.framework.config.XmlParser;
import org.framework.datasource.DataSourceFactory;
import org.framework.datasource.DataSourceService;
import org.framework.db.Utility;
import org.framework.utils.CustomMethods;
import org.framework.utils.FolderDeleteService;
import org.framework.utils.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ServerManager {

    private static ServerManager instance = new ServerManager();

    public Map<String, AbstractService> getServices() {
        return services;
    }

    private Map<String, AbstractService> services = new HashMap<>();

    private ServerManager(){
    }

    public static ServerManager getInstance() {
        return instance;
    }

    public AbstractService getService(String serviceName) {
        return services.get(serviceName);
    }

    public ParsedXMLObject parseXml(File file) throws Exception {
        String content = new String (Files.readAllBytes(file.toPath()));
        content = content.replace("com.eviware.soapui.SoapUI.globalProperties", "globalProperties");
        content = content.replace("com.eviware.soapui.support.GroovyUtils", "org.framework.utils.GroovyUtils");
        content = content.replace("groovy.util.XmlSlurper", "groovy.xml.XmlSlurper");
        content = content.replace("com.eviware.soapui.support.types.StringToStringsMap", "org.framework.types.StringToStringsMap");
        //System.out.println(content);
        Files.write(file.toPath(),content.getBytes() );
        XmlParser parser = new XmlParser();
        ParsedXMLObject parsedObj = parser.parseXml(file);
        return parsedObj;
    }

    public boolean deployService(ParsedXMLObject parsedObj, boolean isDeployedFromUI, String user, String backendApplication, String group, String backendType, boolean storeToMasterCatalog, String envType) throws Exception {
        ServiceConfig config = new ServiceConfig();
        boolean isStoredInDb = false;
        try{
            config.setRoutes(parsedObj.getRoutes());
            config.setPort(parsedObj.getPort());
            config.setUserName(user);
            config.setDelay(parsedObj.getDelay());
            config.setAutoStart(parsedObj.getAutoStart());
            config.setXmlFileContent(parsedObj.getXmlFileContent());
            config.setServiceName(parsedObj.getName());
            config.setAfterRequestScript(parsedObj.getAfterRequestScript());
            config.setOnRequestScript(parsedObj.getOnRequestScript());
            config.setStartScript(parsedObj.getStartScript());
            config.setStopScript(parsedObj.getStopScript());
            config.setHttpSecure(parsedObj.getHttpSecure());
            config.setType(parsedObj.getType());
            AbstractService restService = AbstractServiceFactory.getInstance(config);
            config.setRequestHandler(RequestHandlerFactory.getInstance(config));
            config.setProperties(parsedObj.getProperties());
            config.setWsdlContent(parsedObj.getWsdlcontent());
            config.setProtocol(parsedObj.getProtocol());
            config.setRouteEndpoint(parsedObj.getRouteEndpoint());
            config.setRouteModeEnabled(parsedObj.isRouteModeEnabled());
            config.setDatasourceEnabled(parsedObj.isDatasource());
            config.setDataSourceService(new DataSourceService(parsedObj.getdataSourceRegistry(), new DataSourceFactory()));
            restService.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a")));

            services.put(restService.getName(), restService);
            RespTimeConfigManager.getConfig().put(restService.getName(), "No");
            LogConfigManager.getConfig().put(restService.getName(), "No");

            //delete if any data source present when data source flag is false
            if(!config.isDatasourceEnabled()){
                FolderDeleteService.deleteServiceFolder(config.getServiceName());
            }

            boolean started = false;
            try{
                if (config.getAutoStart().equalsIgnoreCase("true")) {
                    restService.start();
                    started = true;
                }
                else{
                    restService.start();
                    restService.stop();
                    started = true;
                }
                boolean isStoredInMasterCatalog = false;
                if(storeToMasterCatalog)
                    isStoredInMasterCatalog = Utility.getInstance().storeServiceInMasterCatalog(restService.getName(), restService.backendApplication, group, backendType, envType);
                isStoredInDb =  Utility.getInstance().storeServiceInDataBase(restService.getName(), restService, isDeployedFromUI);
                if(!(isStoredInDb)){
                    try{
                        restService.stop();
                        services.remove(restService.getName());
                    }
                    catch(Exception e){
                        Logger.getInstance().error(config.getServiceName(), e);
                        e.printStackTrace();
                        return false;
                    }
                }
                return true;

            } catch(Exception e){
                e.printStackTrace();
                Logger.getInstance().error(config.getServiceName(), e);
                if(started)
                    restService.stop();
                return false;
            }

            //store into db
        }
        catch(Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Logger.getInstance().error("Error occurred in "+ config.getServiceName() + "\n" + sw.toString());
            return false;
        }
    }

    public boolean startService(String serviceName){
        try{
            AbstractService service = this.getService(serviceName);
            if(service == null){
                Logger.getInstance().error("Service doesn't exist :"+serviceName);
                return false;
            }
            if(!service.isRunning()){
                service.start();
                service.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a")));
                Logger.getInstance().info("Service started successfully :"+ serviceName);
                Utility.getInstance().updateServiceStatusInDb(service, "Running");
                Utility.getInstance().updateServiceFromCatalogDb(serviceName,"ACTIVE", service.getConfig().getPort());

            }
            return true;

        }catch(Exception e){
            Logger.getInstance().error(serviceName, e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteService(String serviceName){
        try{
            AbstractService service = this.getService(serviceName);
            if(service == null){
                Logger.getInstance().error("Service doesn't exist :"+serviceName);
                return false;
            }

            service.stop();
            services.remove(service.getName(), service);
            //remove from db
            Utility.getInstance().deleteServiceFromDb(service.getName());
            //remove from directory
            CustomMethods.getInstance().removeXmlFile(service.getName());
            //update status incatalog db
            Utility.getInstance().deleteServiceFromCatalogDb(serviceName);
            Logger.getInstance().info("Service deleted successfully :"+ serviceName);
            return true;

        }catch(Exception e){
            Logger.getInstance().error(serviceName, e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean stopService(String serviceName){
        try{
            AbstractService service = this.getService(serviceName);
            if (service == null){
                Logger.getInstance().error("Service doesn't exists :"+serviceName);
                return false;
            }
            if (service.isRunning()){
                service.stop();
                Logger.getInstance().info("Service stopped successfully :"+ serviceName);
                service.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a")));
                Utility.getInstance().updateServiceStatusInDb(service, "Stopped");
                Utility.getInstance().updateServiceFromCatalogDb(serviceName,"INACTIVE", service.getConfig().getPort());
                return true;
            }
        }catch(Exception e){
            Logger.getInstance().error(serviceName, e);
            e.printStackTrace();
        }

        Logger.getInstance().error("Service is not running :"+ serviceName);
        return false;
    }
}

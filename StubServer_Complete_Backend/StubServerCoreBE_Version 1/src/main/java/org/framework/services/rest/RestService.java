package org.framework.services.rest;

import org.framework.core.*;

import org.framework.core.impl.GroovyScriptExecutor;
import org.framework.config.ServiceConfig;
import org.common.db.config.ConfigLoader;
import org.framework.db.Utility;
import org.framework.properties.*;
import org.framework.utils.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import com.sun.net.httpserver.Headers; // reused to keep MockResponse headers handling intact

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Jetty-backed RestService that replaces com.sun.net.httpserver.HttpServer
 * usage.
 * Supports HTTP and HTTPS, optional client-auth. Mirrors existing request flow.
 */
public class RestService extends AbstractService {
    private Server jettyServer;
    private String timestamp;
    private boolean isRunning;
    private ExecutorService executor; // still available for your own tasks; Jetty has its own threadpool
    public ServiceRespTimeHandler respTimeHandler = new ServiceRespTimeHandler();

    public RestService(ServiceConfig config) throws IOException {
        super(config);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void start() throws IOException {
        // Your async pool (optional); Jetty uses QueuedThreadPool internally
        if (executor == null) {
            executor = Executors.newCachedThreadPool();
        }

        // Jetty thread pool
        int maxThreads = parseIntOrDefault(ConfigLoader.getProperty("jetty.maxThreads"), 500);
        int minThreads = parseIntOrDefault(ConfigLoader.getProperty("jetty.minThreads"), 8);
        int idleTimeoutMs = parseIntOrDefault(ConfigLoader.getProperty("jetty.idleTimeoutMs"), 30000);
        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeoutMs);

        jettyServer = new Server(threadPool);

        // If config.getProtocol() returns "https", treat as HTTPS enabled
        boolean httpsEnabled = Boolean.parseBoolean(config.getHttpSecure());

        String host = CustomMethods.getLocalHostAddress();
        System.out.println("host : " + host);
        int httpPort = config.getPort(); // use your service port for http if needed
        int httpsPort = parseIntOrDefault(ConfigLoader.getProperty("jetty.https.port"), config.getPort());

        List<ServerConnector> connectors = new ArrayList<>();

        // HTTP connector (optional: if both desired)
        boolean httpEnabled = parseBooleanOrDefault(ConfigLoader.getProperty("jetty.http.enabled"), !httpsEnabled);
        if (httpEnabled) {
            HttpConfiguration httpConfig = new HttpConfiguration();
            ServerConnector httpConnector = new ServerConnector(jettyServer, new HttpConnectionFactory(httpConfig));
            if (host != null && !host.isEmpty())
                httpConnector.setHost("0.0.0.0");
            httpConnector.setPort(httpPort);
            connectors.add(httpConnector);
            Logger.getInstance().info("Starting HTTP on " + (host == null ? "0.0.0.0" : host) + ":" + httpPort);
        }

        // HTTPS connector
        if (httpsEnabled) {
            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

            // Keystore settings — externalize these!
            String ksPath = firstNonNull(ConfigLoader.getProperty("jetty.keystore.path"), config.getKeystorePath());
            String ksPassword = firstNonNull(ConfigLoader.getProperty("jetty.keystore.password"),
                    config.getKeystorePassword());
            String ksType = firstNonNull(ConfigLoader.getProperty("jetty.keystore.type"), "JKS");

            sslContextFactory.setKeyStorePath(ksPath);
            sslContextFactory.setKeyStorePassword(ksPassword);
            sslContextFactory.setKeyStoreType(ksType);

            /*
             * boolean needClientAuth =
             * parseBooleanOrDefault(ConfigLoader.getProperty("jetty.clientAuth.required"),
             * false);
             * if (needClientAuth) {
             * String tsPath =
             * firstNonNull(ConfigLoader.getProperty("jetty.truststore.path"),
             * config.getTruststorePath());
             * String tsPassword =
             * firstNonNull(ConfigLoader.getProperty("jetty.truststore.password"),
             * config.getTruststorePassword());
             * String tsType =
             * firstNonNull(ConfigLoader.getProperty("jetty.truststore.type"), "JKS");
             * sslContextFactory.setTrustStorePath(tsPath);
             * sslContextFactory.setTrustStorePassword(tsPassword);
             * sslContextFactory.setTrustStoreType(tsType);
             * sslContextFactory.setNeedClientAuth(true);
             * }
             */

            HttpConfiguration httpsConfig = new HttpConfiguration();
            httpsConfig.addCustomizer(new SecureRequestCustomizer()); // add SSL info

            ServerConnector httpsConnector = new ServerConnector(
                    jettyServer,
                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(httpsConfig));
            if (host != null && !host.isEmpty())
                httpsConnector.setHost("0.0.0.0");
            httpsConnector.setPort(httpsPort);
            connectors.add(httpsConnector);

            Logger.getInstance().info("Starting HTTPS on " + (host == null ? "0.0.0.0" : host) + ":" + httpsPort);
        }

        jettyServer.setConnectors(connectors.toArray(new ServerConnector[0]));

        // ---- Execute start script & init delay config ----
        AbstractService that = this;
        Context globalContext = new Context(that);

        GroovyScriptExecutor groovyExecutor = new GroovyScriptExecutor();
        HashMap<String, Object> startScriptProperties = new HashMap<>();
        startScriptProperties.put("context", globalContext);

        if (config.getStartScript() != null && !config.getStartScript().isEmpty()) {
            config.setStartScript(config.getStartScript()
                    .replaceAll("com\\.vs\\.met\\.(\\*|VSMetricstoDB)", "org.framework.utils.VSMetricstoDB"));
        }

        Utility.getInstance().setExecutionModeAndHost(config.getServiceName(), host);
        groovyExecutor.executeScript(config.getStartScript(), startScriptProperties, globalContext);
        Utility.getInstance().setCustomDelayConfig(config, host);

        // ---- Set unified Jetty handler (bridges to your current flow) ----
        jettyServer.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target,
                    org.eclipse.jetty.server.Request baseRequest,
                    HttpServletRequest request,
                    HttpServletResponse response) throws IOException, ServletException {

                try {

                    if ("Yes".equals(RespTimeConfigManager.getConfig().get(config.getServiceName()))) {
                        respTimeHandler.start();
                    }

                    Context context = new Context(globalContext);
                    MockRequest mockRequest = getRequestFromServlet(request);
                    onRequestScriptHandler(context, mockRequest);

                    String sanityHeader = getSourceHeader(mockRequest);

                    RequestTracker.incrementRequest(config.getServiceName());
                    if (sanityHeader == null) {
                        RequestTrackerDB.incrementRequest(config.getServiceName());
                    }

                    MockResponse mockResponse = config.getRequestHandler().handleRequest(context, mockRequest);

                    // delay
                    int delay = CustomMethods.getInstance().calculateDelay(config);
                    System.out.println("delay " + delay);
                    Thread.sleep(Math.max(delay, 0));

                    // set headers
                    Headers headers = mockResponse.getHeaders();
                    if (headers != null) {
                        for (String key : headers.keySet()) {
                            List<String> values = headers.get(key);
                            if (values != null) {
                                for (String value : values) {
                                    System.out.print("key " + key + " value " + value);
                                    response.addHeader(key, value);
                                }
                            }
                        }
                    }
                    if (response.getHeader("Content-Type") == null || response.getHeader("Content-Type").isBlank()) {
                        response.setHeader("Content-Type", request.getHeader("Content-Type"));
                    }
                    // status + body
                    response.setStatus(mockResponse.getStatusCode());
                    byte[] bytes = mockResponse.getResponseBytes();
                    if (bytes != null) {
                        response.setContentLength(bytes.length);
                        OutputStream os = response.getOutputStream();
                        os.write(bytes);
                        os.flush();
                        Logger.getInstance().info("response returned : " + mockResponse.getResponseContent());
                        os.close();
                    }

                    // log request
                    String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    RequestLog requestlog = new RequestLog(
                            mockRequest, mockResponse, mockRequest.getPath(), ts,
                            config.getServiceName(), config.getPort(), isRunning, config.getType());

                    RequestTracker.logRequest(config.getServiceName(), requestlog);
                    if (sanityHeader == null) {
                        RequestTrackerDB.logRequest(config.getServiceName(), requestlog);
                    }

                    new ReqResLogger().log(config.getServiceName(), requestlog);

                    afterRequestScriptHandler(context, mockRequest, mockResponse);

                    baseRequest.setHandled(true);
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    Logger.getInstance().error("Error occurred in " + config.getServiceName() + "\n" + sw.toString());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    baseRequest.setHandled(true);
                }
            }
        });

        try {
            jettyServer.start();
            isRunning = true;
            Logger.getInstance().info("Jetty server started for service " + config.getServiceName());
        } catch (Exception e) {
            throw new IOException("Failed to start Jetty", e);
        }
    }

    private void onRequestScriptHandler(Context context, MockRequest request) {
        if ("Yes".equals(RespTimeConfigManager.getConfig().get(config.getServiceName()))) {
            respTimeHandler.onRequest(context);
        }
        GroovyScriptExecutor groovyExecutor = new GroovyScriptExecutor();
        HashMap<String, Object> onRequestProperties = new HashMap<>();
        onRequestProperties.put("context", context);
        onRequestProperties.put("mockRequest", request);
        if (config.getOnRequestScript() != null && !config.getOnRequestScript().isEmpty()) {
            config.setOnRequestScript(config.getOnRequestScript()
                    .replaceAll("com\\.vs\\.met\\.(\\*|VSMetricstoDB)", "org.framework.utils.VSMetricstoDB"));
        }
        groovyExecutor.executeScript(config.getOnRequestScript(), onRequestProperties, context);
    }

    private void afterRequestScriptHandler(Context context, MockRequest request, MockResponse response) {
        MockResult mockResult = new MockResult(request, response);
        HashMap<String, Object> afterRequestProperties = new HashMap<>();
        afterRequestProperties.put("context", context);
        afterRequestProperties.put("mockRequest", request);
        afterRequestProperties.put("mockResult", mockResult);
        if (config.getAfterRequestScript() != null && !config.getAfterRequestScript().isEmpty()) {
            config.setAfterRequestScript(config.getAfterRequestScript()
                    .replaceAll("com\\.vs\\.met\\.(\\*|VSMetricsResptoDB)",
                            "org.framework.utils.VSMetricsResptoDB"));
        }
        new GroovyScriptExecutor().executeScript(config.getAfterRequestScript(), afterRequestProperties,
                context);
        if ("Yes".equals(RespTimeConfigManager.getConfig().get(config.getServiceName()))) {
            respTimeHandler.afterRequest(context);
        }
    }

    @Override
    public void stop() {
        // Execute stop script
        AbstractService that = this;
        Context globalContext = new Context(that);

        GroovyScriptExecutor groovyExecutor = new GroovyScriptExecutor();
        HashMap<String, Object> stopScriptProperties = new HashMap<>();
        stopScriptProperties.put("context", globalContext);

        if (config.getStopScript() != null && !config.getStopScript().isEmpty()) {
            config.setStopScript(config.getStopScript()
                    .replaceAll("com\\.vs\\.met\\.(\\*|VSMetricstoDB)", "org.framework.utils.VSMetricstoDB"));
        }
        groovyExecutor.executeScript(config.getStopScript(), stopScriptProperties, globalContext);

        if (jettyServer != null) {
            try {
                jettyServer.stop();
                jettyServer.destroy();
                isRunning = false;
                Logger.getInstance().info("Jetty server stopped for service " + config.getServiceName());
            } catch (Exception e) {
                Logger.getInstance().error("Error while stopping Jetty server: " + e.getMessage());
            }
        }
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void executeScript(String scriptType) {
        /* reserved */ }

    // ---- Helpers ----

    /** Build MockRequest from HttpServletRequest (Jetty). */
    private MockRequest getRequestFromServlet(HttpServletRequest req) throws IOException {
        MockRequest mockRequest = new MockRequest();
        mockRequest.setMockRequest(mockRequest);
        mockRequest.setMethod(req.getMethod());
        mockRequest.setPath(req.getRequestURI());
        mockRequest.setQueryString(req.getQueryString());
        mockRequest.setRequestHeaders(toSunHeaders(req)); // keep existing Headers usage
        mockRequest.setRequestContent(getRequestBody(req));

        HttpRequest httpRequest = new HttpRequest(mockRequest.getPath(), mockRequest.getQueryString(),
                mockRequest.getRequestHeaders());
        mockRequest.setHttpRequest(httpRequest);
        return mockRequest;
    }

    /**
     * Convert servlet headers to com.sun.net.httpserver.Headers to avoid touching
     * downstream code.
     */
    private Headers toSunHeaders(HttpServletRequest req) {
        Headers headers = new Headers();
        Enumeration<String> names = req.getHeaderNames();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            Enumeration<String> values = req.getHeaders(name);
            if (values != null) {
                while (values.hasMoreElements()) {
                    headers.add(name, values.nextElement());
                }
            }
        }
        return headers;
    }

    /** Read request body from servlet request. */
    public static String getRequestBody(HttpServletRequest req) throws IOException {

        // Do not read body for GET or HEAD
        if ("GET".equalsIgnoreCase(req.getMethod()) ||
                "HEAD".equalsIgnoreCase(req.getMethod())) {
            return "";
        }

        StringBuilder body = new StringBuilder();
        char[] buffer = new char[4096];
        int bytesRead = -1;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(req.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {

            while ((bytesRead = reader.read(buffer)) != -1) {
                body.append(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            // Jetty throws EofException when client aborts upload
            Logger.getInstance().info("Client disconnected early while reading request body. Ignoring.");
            return "";
        }

        return body.toString();
    }

    /**
     * Extract "X-Test-Source" header and log sanity run message (same behavior).
     */
    String getSourceHeader(MockRequest request) {
        String header = null;
        if (request.getRequestHeaders().get("X-Test-Source") != null
                && !(request.getRequestHeaders().get("X-Test-Source")).isEmpty()) {
            header = (request.getRequestHeaders().get("X-Test-Source")).getFirst();
            Logger.getInstance().info("header :" + header);
            if ("sanity-suite".equals(header)) {
                Logger.getInstance().info("Sanity run detected. Skipping transcation metrics update for stubserver");
            }
        }
        return header;
    }

    /**
     * Return first active port (http or https) from configured connectors for
     * logging.
     */
    private int activePort(List<ServerConnector> connectors) {
        for (ServerConnector c : connectors) {
            if (c.isOpen())
                return c.getPort();
        }
        // fallback: prefer https port if enabled else config port
        return parseIntOrDefault(ConfigLoader.getProperty("jetty.https.port"), config.getPort());
    }

    // small util helpers
    private static int parseIntOrDefault(String s, int def) {
        try {
            return (s == null || s.isEmpty()) ? def : Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private static boolean parseBooleanOrDefault(String s, boolean def) {
        if (s == null)
            return def;
        String v = s.trim().toLowerCase(Locale.ROOT);
        if (v.equals("true") || v.equals("yes") || v.equals("1"))
            return true;
        if (v.equals("false") || v.equals("no") || v.equals("0"))
            return false;
        return def;
    }

    private static String firstNonNull(String a, String b) {
        return (a != null && !a.isEmpty()) ? a : ((b != null && !b.isEmpty()) ? b : null);
    }
}

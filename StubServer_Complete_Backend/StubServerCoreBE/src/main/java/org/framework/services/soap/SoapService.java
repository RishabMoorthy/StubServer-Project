package org.framework.services.soap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.common.db.config.ConfigLoader;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.framework.core.*;
import org.framework.db.Utility;
import org.framework.properties.*;
import org.framework.utils.*;
import org.w3c.dom.*;

import com.sun.net.httpserver.Headers;
import org.framework.config.ServiceConfig;
import org.framework.core.impl.GroovyScriptExecutor;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoapService extends AbstractService {
    private Server jettyServer;
    private boolean isRunning;
    private ExecutorService executor;
    public ServiceRespTimeHandler respTimeHandler = new ServiceRespTimeHandler();

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public SoapService(ServiceConfig config) throws IOException {
        super(config);
    }

    @Override
    public void start() throws Exception {
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
        AbstractService that = this;
        Context globalContext = new Context(that);

        // respTimeHandler.start();

        GroovyScriptExecutor groovyExecutor = new GroovyScriptExecutor();
        HashMap<String, Object> startScriptProperties = new HashMap<>();
        startScriptProperties.put("context", globalContext);
        // startScriptProperties.put("globalProperties",
        // GlobalProperties.getInstance());
        if (config.getStartScript() != null && !config.getStartScript().isEmpty())
            config.setStartScript(config.getStartScript().replaceAll("com\\.vs\\.met\\.(\\*|VSMetricstoDB)",
                    "org.framework.utils.VSMetricstoDB"));
        // System.out.println("start request script "+config.getOnRequestScript());
        Utility.getInstance().setExecutionModeAndHost(config.getServiceName(), CustomMethods.getLocalHostAddress());
        groovyExecutor.executeScript(config.getStartScript(), startScriptProperties, globalContext);
        Utility.getInstance().setCustomDelayConfig(config, CustomMethods.getLocalHostAddress());

        // ---- Set unified Jetty handler (bridges to your current flow) ----
        jettyServer.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target,
                    org.eclipse.jetty.server.Request baseRequest,
                    HttpServletRequest request,
                    HttpServletResponse response) throws IOException, ServletException {
                System.out.println("i am in soap service");
                if (RespTimeConfigManager.getConfig().get(config.getServiceName()).equals("Yes"))
                    respTimeHandler.start();
                // executeScript("onRequestScript");
                // System.out.println("handle");
                Context context = new Context(globalContext);
                MockRequest mockrequest = null;
                MockResponse mockresponse = null;
                try {
                    mockrequest = getRequestFromServlet(request);
                    Logger.getInstance().info("soap request " + request);
                    this.onRequestScriptHandler(context, mockrequest);
                    String sanityHeader = getSourceHeader(mockrequest);
                    RequestTracker.incrementRequest(config.getServiceName());
                    if (sanityHeader == null)
                        RequestTrackerDB.incrementRequest(config.getServiceName());
                    // validate schema

                    /*
                     * SchemaValidator loader = new SchemaValidator();
                     * Schema schema = loader.loadSchemaFromWsdl(config.getWsdlContent());
                     * loader.validateSoapBody(request.getRequestContent(), schema);
                     */
                    /*
                     * RelaxedSchemaValidator validator = new
                     * RelaxedSchemaValidator(config.getWsdlContent());
                     * boolean valid = validator.validate(request.getRequestContent());
                     * System.out.println("Is request valid? " + valid);
                     */
                    //
                    mockresponse = config.getRequestHandler().handleRequest(context, mockrequest);
                    // get the delay
                    int delay = CustomMethods.getInstance().calculateDelay(config);
                    Logger.getInstance().info("delay " + delay);
                    Thread.sleep(delay);
                    // System.out.println("response content "+response.getResponseContent());
                    // set headers
                    Headers headers = mockresponse.getHeaders();
                    if (headers != null) {
                        for (String key : headers.keySet()) {
                            List<String> values = headers.get(key);
                            if (values != null) {
                                for (String value : values) {
                                    response.addHeader(key, value);
                                }
                            }
                        }
                    }
                    // status + body
                    response.setStatus(mockresponse.getStatusCode());

                    if (response.getHeader("Content-Type") == null || response.getHeader("Content-Type").isEmpty()) {
                        // If you know your service/endpoint is SOAP 1.2, use application/soap+xml
                        // instead.
                        response.setContentType("application/xml");
                        System.out.println("I am setting content type");
                    }

                    byte[] bytes = mockresponse.getResponseBytes();
                    if (bytes != null) {
                        response.setContentLength(bytes.length);
                        OutputStream os = response.getOutputStream();
                        os.write(bytes);
                        os.flush();
                        Logger.getInstance().info("response returned : " + mockresponse.getResponseContent());
                        os.close();
                    }
                    RequestLog requestlog = new RequestLog(mockrequest, mockresponse, mockrequest.getPath(),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                            config.getServiceName(),
                            config.getPort(), isRunning, config.getType());
                    RequestTracker.logRequest(config.getServiceName(), requestlog);
                    // CHECK IF NOT SANITY

                    if (sanityHeader == null)
                        RequestTrackerDB.logRequest(config.getServiceName(), requestlog);
                    ReqResLogger reqRes = new ReqResLogger();
                    reqRes.log(config.getServiceName(), requestlog);
                    this.afterRequestScriptHandler(context, mockrequest, mockresponse);
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    Logger.getInstance().error("Error occurred in " + config.getServiceName() + "\n" + sw.toString());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    baseRequest.setHandled(true);
                }

                // setHeaders
                /*
                 * Headers headers = response.getHeaders();
                 * for (String key : headers.keySet()){
                 * List<String> values = headers.get(key);
                 * for (String value : values) {
                 * exchange.getResponseHeaders().add(key, value);
                 * }
                 * }
                 */
            }

            String getSourceHeader(MockRequest request) {
                String header = null;
                if (request.getRequestHeaders().get("X-Test-Source") != null
                        && !(request.getRequestHeaders().get("X-Test-Source")).isEmpty()) {
                    header = (request.getRequestHeaders().get("X-Test-Source")).getFirst();
                    Logger.getInstance().info("header :" + header);
                    if (header.equals("sanity-suite")) {
                        Logger.getInstance()
                                .info("Sanity run detected. Skipping transcation metrics update for stubserver");
                    }
                }
                return header;
            }

            public void onRequestScriptHandler(Context context, MockRequest request) {
                if (RespTimeConfigManager.getConfig().get(config.getServiceName()).equals("Yes"))
                    respTimeHandler.onRequest(context);
                GroovyScriptExecutor groovyExecutor = new GroovyScriptExecutor();
                HashMap<String, Object> onRequestProperties = new HashMap<>();
                onRequestProperties.put("context", context);
                onRequestProperties.put("mockRequest", request);
                if (config.getOnRequestScript() != null && !config.getOnRequestScript().isEmpty())
                    config.setOnRequestScript(
                            config.getOnRequestScript().replaceAll("com\\.vs\\.met\\.(\\*|VSMetricstoDB)",
                                    "org.framework.utils.VSMetricstoDB"));
                // System.out.println("on request script "+config.getOnRequestScript());
                groovyExecutor.executeScript(config.getOnRequestScript(), onRequestProperties, context);
            }

            public void afterRequestScriptHandler(Context context, MockRequest request, MockResponse response) {
                MockResult mockResult = new MockResult(request, response);
                HashMap<String, Object> afterRequestProperties = new HashMap<>();
                afterRequestProperties.put("context", context);
                afterRequestProperties.put("mockRequest", request);
                afterRequestProperties.put("mockResult", mockResult);
                if (config.getAfterRequestScript() != null && !config.getAfterRequestScript().isEmpty())
                    config.setAfterRequestScript(config.getAfterRequestScript().replaceAll(
                            "com\\.vs\\.met\\.(\\*|VSMetricsResptoDB)", "org.framework.utils.VSMetricsResptoDB"));
                // System.out.println("after request script "+config.getOnRequestScript());
                groovyExecutor.executeScript(config.getAfterRequestScript(), afterRequestProperties, context);
                if (RespTimeConfigManager.getConfig().get(config.getServiceName()).equals("Yes"))
                    respTimeHandler.afterRequest(context);
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

    HashMap<String, String> extractSoapRequestWithLogging(byte[] request)
            throws ParserConfigurationException, IOException, SAXException {
        // Log the first few bytes to see what we're dealing with
        System.out.println("Request length: " + request.length);
        System.out.println("First 10 bytes: ");
        for (int i = 0; i < Math.min(10, request.length); i++) {
            System.out.printf("%02X ", request[i]);
        }
        System.out.println();

        // Try to detect encoding
        String detectedEncoding = detectEncoding(request);
        System.out.println("Detected encoding: " + detectedEncoding);
        String content = "";
        // Try to convert to string to see content
        try {
            content = new String(request, detectedEncoding);
            System.out.println("First 200 chars: " + content.substring(0, Math.min(200, content.length())));
        } catch (Exception e) {
            System.out.println("Could not convert to string: " + e.getMessage());
        }
        content = sanitizeXml(content);
        // Now parse
        // ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        InputSource inputsource = new InputSource(new StringReader(content));
        inputsource.setEncoding(detectedEncoding);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = factory.newDocumentBuilder();

        // Custom error handler to see exact error
        builder.setErrorHandler(new ErrorHandler() {
            public void warning(SAXParseException e) {
                System.out.println("Warning: " + e.getMessage());
            }

            public void error(SAXParseException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Line: " + e.getLineNumber() + ", Column: " + e.getColumnNumber());
            }

            public void fatalError(SAXParseException e) throws SAXException {
                System.out.println("Fatal Error: " + e.getMessage());
                System.out.println("Line: " + e.getLineNumber() + ", Column: " + e.getColumnNumber());
                throw e;
            }
        });

        Document document = builder.parse(inputsource);
        document.getDocumentElement().normalize();

        // Extract soap envelope
        Element element = document.getDocumentElement();

        NodeList nodelist = element.getElementsByTagNameNS("*", "Body");
        if (nodelist.getLength() == 0) {
            Logger.getInstance().info("No Soap Body Found");
            return null;
        }

        Element bodyElement = (Element) nodelist.item(0);
        Node operationNode = bodyElement.getFirstChild();
        while (operationNode != null && operationNode.getNodeType() != Node.ELEMENT_NODE) {
            operationNode = operationNode.getNextSibling();
        }

        String operationName = null;
        String serviceNameSpace = null;

        if (operationNode instanceof Element) {
            Element elementOperation = (Element) operationNode;
            operationName = elementOperation.getLocalName();
            serviceNameSpace = elementOperation.getNamespaceURI();
        }

        HashMap<String, String> soapElements = new HashMap<>();
        soapElements.put("operation", operationName);
        soapElements.put("interface", serviceNameSpace);
        return soapElements;
    }

    private String sanitizeXml(String xml) {
        if (xml == null)
            return null;
        xml = xml.replace("\uFEFF", ""); // Remove BOM
        xml = xml.trim(); // Remove leading/trailing whitespace/newlines
        return xml;
    }

    /**
     * Detect encoding from byte array
     */
    private String detectEncoding(byte[] bytes) {
        if (bytes.length < 2)
            return "UTF-8";

        // UTF-16 BE BOM
        if (bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF) {
            return "UTF-16BE";
        }

        // UTF-16 LE BOM
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE) {
            return "UTF-16LE";
        }

        // UTF-8 BOM
        if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
            return "UTF-8";
        }

        // Check if it looks like UTF-16 without BOM (every other byte is 0x00)
        if (bytes.length >= 4) {
            // UTF-16LE pattern: non-zero, zero, non-zero, zero
            if (bytes[1] == 0 && bytes[3] == 0 && bytes[0] != 0 && bytes[2] != 0) {
                return "UTF-16LE";
            }
            // UTF-16BE pattern: zero, non-zero, zero, non-zero
            if (bytes[0] == 0 && bytes[2] == 0 && bytes[1] != 0 && bytes[3] != 0) {
                return "UTF-16BE";
            }
        }

        return "UTF-8";
    }

    /** Read request body from servlet request. */
    public static byte[] getRequestBody(HttpServletRequest req) throws IOException {

        // Force UTF-8 decoding to match old HttpServer behavior

        try (InputStream is = req.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int read;

            while ((read = is.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }

            return bos.toByteArray();
        }
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
        }

        return buffer.toByteArray();
    }

    @Override
    public void stop() {
        // executeScript("stopScript");
        AbstractService that = this;
        Context globalContext = new Context(that);

        GroovyScriptExecutor groovyExecutor = new GroovyScriptExecutor();
        HashMap<String, Object> stopScriptProperties = new HashMap<>();
        stopScriptProperties.put("context", globalContext);
        // startScriptProperties.put("globalProperties",
        // GlobalProperties.getInstance());
        if (config.getStopScript() != null && !config.getStopScript().isEmpty())
            config.setStopScript(config.getStopScript().replaceAll("com\\.vs\\.met\\.(\\*|VSMetricstoDB)",
                    "org.framework.utils.VSMetricstoDB"));
        // System.out.println("stop script "+config.getOnRequestScript());
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
        // System.out.println("i was stopped");
    }

    /** Build MockRequest from HttpServletRequest (Jetty). */
    private MockRequest getRequestFromServlet(HttpServletRequest req)
            throws IOException, ParserConfigurationException, SAXException {
        MockRequest mockRequest = new MockRequest();
        mockRequest.setMockRequest(mockRequest);
        byte[] content = getRequestBody(req);

        // Detect encoding
        String detectedEncoding = detectEncoding(content);

        // Decode for logging/display
        String str = new String(content, Charset.forName(detectedEncoding));
        String updatedRequest = str.replaceAll("encoding=\"[^\"]+\"", "encoding=\"UTF-8\"");
        // System.out.println("after update "+updatedRequest);
        updatedRequest = sanitizeXml(updatedRequest);
        mockRequest.setRequestContent(updatedRequest);

        // mockRequest.setRequestContent(str);
        HashMap<String, String> soapElements = extractSoapRequestWithLogging(content);
        mockRequest.setOperation(soapElements.get("operation"));
        mockRequest.setSoap_interface(soapElements.get("interface"));
        mockRequest.setRequestHeaders(toSunHeaders(req)); // keep existing Headers usage
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

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void executeScript(String scriptType) {
    }
}

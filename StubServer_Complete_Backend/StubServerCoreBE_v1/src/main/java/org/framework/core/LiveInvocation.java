package org.framework.core;

import com.sun.net.httpserver.Headers;
import org.framework.properties.Context;
import org.framework.properties.MockRequest;
import org.framework.properties.MockResponse;
import org.framework.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LiveInvocation {

    public MockResponse getLiveResponse(Context context, MockRequest mockRequest, MockResponse resp) {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream(); // for partial data

        try {
            String host = context.getMockService().getConfig().getRouteEndpoint();

            String requestURI = mockRequest.getHttpRequest().getRequestURI();
            String queryString = mockRequest.getHttpRequest().getQueryString();

            String endpoint = host + requestURI;
            if (queryString != null && !queryString.isEmpty()) {
                endpoint += "?" + queryString;
            }

            Logger.getInstance().info("Endpoint: " + endpoint);

            // HTTP CLIENT (force HTTP/1.1)
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();

            // BUILD REQUEST
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(120));

            String method = mockRequest.getMethod();
            String content = mockRequest.getRequestContent();

            if ("POST".equalsIgnoreCase(method) ||
                    "PUT".equalsIgnoreCase(method) ||
                    "PATCH".equalsIgnoreCase(method)) {

                requestBuilder.method(method,
                        HttpRequest.BodyPublishers.ofString(
                                content != null ? content : "",
                                StandardCharsets.UTF_8
                        ));
            } else {
                requestBuilder.GET();
            }

            //COPY SAFE HEADERS
            for (Map.Entry<String, List<String>> entry : mockRequest.getRequestHeaders().entrySet()) {
                String key = entry.getKey();

                if (key == null) continue;

                if (key.equalsIgnoreCase("host") ||
                        key.equalsIgnoreCase("content-length") ||
                        key.equalsIgnoreCase("connection") ||
                        key.equalsIgnoreCase("accept-encoding")) {
                    continue;
                }

                for (String value : entry.getValue()) {
                    requestBuilder.header(key, value);
                }
            }

            long start = System.currentTimeMillis();

            // STREAMING RESPONSE (IMPORTANT CHANGE)
            HttpResponse<InputStream> response = client.send(
                    requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofInputStream()
            );

            long end = System.currentTimeMillis();
            Logger.getInstance().info("Response Time: " + (end - start) + " ms");
            resp.setStatusCode(response.statusCode());

            // READ RESPONSE IN CHUNKS
            InputStream is = response.body();
            byte[] chunk = new byte[8192];
            int bytesRead;

            while ((bytesRead = is.read(chunk)) != -1) {
                buffer.write(chunk, 0, bytesRead);

                // optional progress log
                if (buffer.size() % (50 * 1024) < 8192) {
                    Logger.getInstance().info("Downloaded bytes: " + buffer.size());
                }
            }

            byte[] responseBytes = buffer.toByteArray();

            // IMPORTANT: set partial/full response
            resp.setResponseBytes(responseBytes);

            // HEADERS
            Headers respHeaders = resp.getHeaders();
            if (respHeaders == null) {
                respHeaders = new Headers();
                resp.setHeaders(respHeaders);
            } else {
                respHeaders.clear();
            }

            for (Map.Entry<String, List<String>> entry : response.headers().map().entrySet()) {
                respHeaders.put(entry.getKey(), entry.getValue());
            }

            return resp;

        } catch (Exception e) {

            Logger.getInstance().error("Live call failed", e);

            // RETURN PARTIAL RESPONSE IF AVAILABLE
            if (buffer.size() > 0) {
                Logger.getInstance().info("Returning partial response. Bytes received: " + buffer.size());
                resp.setResponseBytes(buffer.toByteArray());
                return resp;
            }

            return new MockResponse(
                    "live_error",
                    500,
                    ("Error invoking live service: " + e.getMessage()).getBytes(StandardCharsets.UTF_8),
                    null
            );
        }
    }
}

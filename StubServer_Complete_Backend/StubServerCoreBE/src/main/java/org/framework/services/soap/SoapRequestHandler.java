package org.framework.services.soap;

import com.stubio.util.StubOperation;

import com.sun.net.httpserver.Headers;

import org.framework.core.RequestHandler;
import org.framework.core.ResponseResolver;
import org.framework.properties.Context;
import org.framework.properties.MockRequest;
import org.framework.properties.MockResponse;
import org.framework.utils.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * SOAP dispatch over the com.stubio model.
 *
 * <p>
 * Operation matching moved here from SoapRoute. The interface/namespace match
 * is gone: SoapRoute.matchRequest() already ignored it, and the new XML format
 * no longer carries an interface attribute.
 */
public class SoapRequestHandler implements RequestHandler {

    private final List<StubOperation> operations;
    private final ResponseResolver resolver;

    public SoapRequestHandler(List<StubOperation> operations, ResponseResolver resolver) {
        this.operations = operations;
        this.resolver = resolver;
    }

    @Override
    public MockResponse handleRequest(Context context, MockRequest request) throws Exception {
        String operation = request.getOperation();

        Logger.getInstance().info("operation : " + operation);

        for (StubOperation stubOperation : operations) {
            if (matchOperation(stubOperation, request)) {
                System.out.println("request matched");
                return resolver.getResponse(stubOperation, context, request);
            }
        }

        Headers headers = new Headers();
        headers.add("content-type", "plain/text");
        String out = "Page not found";

        // if no operation matched
        return new MockResponse("defaultResponse", headers, 404,
                out.getBytes(StandardCharsets.UTF_8), "", "", "");
    }

    /** Was SoapRoute.matchOperation(). */
    private boolean matchOperation(StubOperation stubOperation, MockRequest request) {
        return request.getOperation() != null
                && request.getOperation().equals(stubOperation.getName());
    }
}

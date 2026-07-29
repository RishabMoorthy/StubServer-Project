package org.framework.core;

import org.framework.properties.Buffer;
import org.framework.properties.MockResponse;
import org.framework.properties.Context;
import org.framework.properties.MockRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface RequestHandler {
    MockResponse handleRequest(Context context, MockRequest request) throws Exception;
    // MockResponse handleRequest(Context context, Buffer) throws Exception; // Generalized request object
}

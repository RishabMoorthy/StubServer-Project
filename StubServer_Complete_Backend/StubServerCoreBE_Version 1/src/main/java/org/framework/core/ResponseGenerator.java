package org.framework.core;

import org.framework.properties.Context;
import org.framework.properties.MockRequest;

public interface ResponseGenerator {
    String GenerateResponse(Context context, MockRequest request);
}

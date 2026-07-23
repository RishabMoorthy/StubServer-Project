package org.framework.core.impl;

import org.framework.core.ResponseGenerator;
import org.framework.properties.Context;
import org.framework.properties.MockRequest;

public class SequenceResponseGenerator implements ResponseGenerator {
    private boolean routeModeEnabled;

    public SequenceResponseGenerator(boolean routeModeEnabled) {
        this.routeModeEnabled = routeModeEnabled;
    }
    public SequenceResponseGenerator() {

    }
    @Override
    public String GenerateResponse(Context context, MockRequest request) {
        if (routeModeEnabled) {
            return "LIVE";
        }

        return null;
    }
}

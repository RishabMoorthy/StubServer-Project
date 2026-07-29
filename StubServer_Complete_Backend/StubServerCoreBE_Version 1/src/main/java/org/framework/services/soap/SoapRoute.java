package org.framework.services.soap;

import org.codehaus.jackson.map.Serializers;
import org.framework.core.BaseRoute;
import org.framework.core.ResponseGenerator;
import org.framework.properties.Context;
import org.framework.properties.MockRequest;
import org.framework.properties.MockResponse;

import java.util.List;

public class SoapRoute extends BaseRoute {
    String soap_interface;
    String opeartion;

    public SoapRoute(String serviceName, ResponseGenerator generator, List<MockResponse> responses,
            String defaultResponse, String soap_interface, String operation, String name, String dispatchStyle,
            String contentType) {
        super(serviceName, generator, responses, defaultResponse, name, dispatchStyle, contentType);
        this.soap_interface = soap_interface;
        this.opeartion = operation;
    }

    public MockResponse getMockResponseByName(String mockResponseName) {
        List<MockResponse> mockResponses = super.getResponses();
        for (int i = 0; i < mockResponses.size(); i++) {
            if (mockResponses.get(i).getName().equals(mockResponseName)) {
                return mockResponses.get(i);
            }
        }

        return null;
    }

    @Override
    public boolean matchRequest(Context context, MockRequest request) {
        // boolean matchedInterface = this.matchInterface(request);
        boolean matchedOperation = this.matchOperation(request);
        // boolean hasMatch = matchedInterface && matchedOperation;
        return matchedOperation;
    }

    public boolean matchInterface(MockRequest request) {
        return request.getSoap_interface().equals(soap_interface);
    }

    public boolean matchOperation(MockRequest request) {
        return request.getOperation().equals(opeartion);
    }
}

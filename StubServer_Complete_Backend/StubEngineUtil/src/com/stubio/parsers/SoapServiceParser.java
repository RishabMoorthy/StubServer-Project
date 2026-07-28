package com.stubio.parsers;

import com.stubio.util.SOAPService;
import com.stubio.util.StubOperation;
import com.stubio.util.VirtualServiceObject;
import org.w3c.dom.Element;
import java.util.LinkedList;

public final class SoapServiceParser {
    private SoapServiceParser() {
    }

    public static SOAPService parse(
            Element soapNode,
            VirtualServiceObject vso) {
        SOAPService service = new SOAPService();
        LinkedList<StubOperation> operations = new LinkedList<>();
        for (Element child : XmlUtils.childElements(soapNode)) {

            switch (XmlUtils.local(child)) {

                case "SecuritySettings" ->

                        service.setSecuritySettings(
                                SecuritySettingsParser.parse(
                                        child));

                case "DefaultError" ->

                        service.setDefaultErrorResponse(
                                DefaultErrorParser.parse(
                                        child));

                case "StubOperations" ->

                        operations.addAll(
                                StubOperationParser.parse(
                                        child,
                                        vso));
            }
        }

        service.setStubOperations(operations);

        return service;
    }
}

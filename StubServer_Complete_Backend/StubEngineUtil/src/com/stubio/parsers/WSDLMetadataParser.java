package com.stubio.parsers;

import com.stubio.util.WSDLMetadata;
import org.w3c.dom.Element;

public final class WSDLMetadataParser {

    private WSDLMetadataParser() {
    }

    public static WSDLMetadata parse(
            Element serviceDefinitionNode) {

        WSDLMetadata metadata =
                new WSDLMetadata();

        for (Element child :
                XmlUtils.childElements(serviceDefinitionNode)) {

            if (!"WSDLMetadata".equals(
                    XmlUtils.local(child))) {
                continue;
            }

            metadata.setType(
                    child.getAttribute("type"));

            metadata.setRootPart(
                    child.getAttribute("rootPart"));

            for (Element wsdlPart :
                    XmlUtils.childElements(child)) {

                if (!"WSDLPart".equals(
                        XmlUtils.local(wsdlPart))) {
                    continue;
                }

                for (Element field :
                        XmlUtils.childElements(wsdlPart)) {

                    switch (XmlUtils.local(field)) {

                        case "WSDLURL" ->
                                metadata.setUrl(
                                        XmlUtils.text(field));

                        case "WSDLText" ->
                                metadata.setWsdlText(
                                        field.getTextContent());
                    }
                }
            }
        }

        return metadata;
    }
}

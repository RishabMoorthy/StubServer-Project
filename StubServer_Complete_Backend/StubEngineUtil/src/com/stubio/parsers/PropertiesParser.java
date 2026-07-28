package com.stubio.parsers;

import com.stubio.util.GenericProperty;
import org.w3c.dom.Element;

import java.util.LinkedList;

public final class PropertiesParser {

    private PropertiesParser() {}

    public static LinkedList<GenericProperty> parse(Element node) {

        LinkedList<GenericProperty> properties =
                new LinkedList<>();

        for (Element variable : XmlUtils.childElements(node)) {

            if (!"Variable".equals(XmlUtils.local(variable))) {
                continue;
            }

            GenericProperty property =
                    new GenericProperty();

            for (Element child :
                    XmlUtils.childElements(variable)) {

                switch (XmlUtils.local(child)) {

                    case "Key" ->
                            property.setKey(XmlUtils.text(child));

                    case "Value" ->
                            property.setValue(XmlUtils.text(child));
                }
            }

            properties.add(property);
        }

        return properties;
    }
}

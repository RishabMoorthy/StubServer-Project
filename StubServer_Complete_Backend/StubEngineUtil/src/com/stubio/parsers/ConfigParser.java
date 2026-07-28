package com.stubio.parsers;

import com.stubio.util.VirtualServiceObject;
import org.w3c.dom.Element;

public final class ConfigParser {
    private ConfigParser() {
    }

    public static void parse(
            Element configNode,
            VirtualServiceObject vso) {
        for (Element child : XmlUtils.childElements(configNode)) {
            switch (XmlUtils.local(child)) {
                case "ThreadPool" -> {

                    String maxThreads = child.getAttribute(
                            "maxThreads");

                    String coreThreads = child.getAttribute(
                            "coreThreads");

                    if (!maxThreads.isBlank()) {

                        vso.setMaxThreads(
                                Integer.parseInt(
                                        maxThreads));
                    }
                    if (!coreThreads.isBlank()) {

                        vso.setCoreThreads(
                                Integer.parseInt(
                                        coreThreads));
                    }
                }
                case "ResponseDelay" -> {

                    String delay = XmlUtils.text(child);

                    if (!delay.isBlank()) {

                        vso.setResponseDelay(
                                Integer.parseInt(delay));
                    }
                }
            }
        }
    }
}

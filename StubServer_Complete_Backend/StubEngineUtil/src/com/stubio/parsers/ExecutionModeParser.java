package com.stubio.parsers;

import com.stubio.util.ExecutionMode;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public final class ExecutionModeParser {

    private ExecutionModeParser() {}

    public static ExecutionMode parse(Element node) {

        ExecutionMode mode = new ExecutionMode();

        for (Element child : XmlUtils.childElements(node)) {

            switch (XmlUtils.local(child)) {

                case "ExecutionModeValue" ->
                        mode.setExeModeValue(
                                XmlUtils.text(child));

                case "LiveURLs" ->
                        mode.setLiveURLs(
                                parseLiveUrls(child));
            }
        }

        return mode;
    }

    private static List<ExecutionMode.LiveURL>
    parseLiveUrls(Element liveUrlsNode) {

        List<ExecutionMode.LiveURL> urls =
                new ArrayList<>();

        for (Element urlNode :
                XmlUtils.childElements(liveUrlsNode)) {

            ExecutionMode.LiveURL liveURL =
                    new ExecutionMode.LiveURL();

            liveURL.setActive(
                    Boolean.parseBoolean(
                            urlNode.getAttribute("active")));

            for (Element child :
                    XmlUtils.childElements(urlNode)) {

                switch (XmlUtils.local(child)) {

                    case "EnvType" ->
                            liveURL.setEnvType(
                                    XmlUtils.text(child));

                    case "TransType" ->
                            liveURL.setTransportType(
                                    XmlUtils.text(child));

                    case "Host" ->
                            liveURL.setHost(
                                    XmlUtils.text(child));

                    case "Port" ->
                            liveURL.setPort(
                                    XmlUtils.text(child));

                    case "BasePath" ->
                            liveURL.setBasePath(
                                    XmlUtils.text(child));
                }
            }

            urls.add(liveURL);
        }

        return urls;
    }
}

package com.stubio.parsers;

import com.stubio.util.*;
import org.w3c.dom.Element;

import java.util.LinkedList;

public final class FilterParser {

    private FilterParser() {
    }

    public static LinkedList<Filter> parse(
            Element filtersNode) {

        LinkedList<Filter> filters =
                new LinkedList<>();

        for (Element variable :
                XmlUtils.childElements(filtersNode)) {

            if (!"Variable".equals(
                    XmlUtils.local(variable))) {
                continue;
            }

            Filter filter =
                    new Filter();

            for (Element child :
                    XmlUtils.childElements(variable)) {

                switch (XmlUtils.local(child)) {

                    case "Name" ->
                            filter.setFilterName(
                                    XmlUtils.text(child));

                    case "Type" ->
                            filter.setFilterType(
                                    XmlUtils.text(child));

                    case "Path" ->
                            filter.setPath(
                                    XmlUtils.text(child));

                    case "RequestName" ->
                            filter.setRequestName(
                                    XmlUtils.text(child));

                    case "DateFormat" ->
                            filter.setDateFormat(
                                    XmlUtils.text(child));

                    case "Offset" ->
                            filter.setOffSet(
                                    XmlUtils.text(child));

                    case "StartText" ->
                            filter.setStartText(
                                    XmlUtils.text(child));

                    case "EndText" ->
                            filter.setEndText(
                                    XmlUtils.text(child));

                    case "FilePath" ->
                            filter.setFilePath(
                                    XmlUtils.text(child));

                    case "PropertyName" ->
                            filter.setPropertyName(
                                    XmlUtils.text(child));

                    case "AppendMode" ->
                            filter.setAppendMode(
                                    Boolean.parseBoolean(
                                            XmlUtils.text(child)));
                }
            }

            filters.add(filter);
        }

        return filters;
    }
}

package com.stubio.parsers;

import com.stubio.util.*;
import org.w3c.dom.Element;

import java.util.LinkedList;

public final class StubOperationParser {

    private StubOperationParser() {
    }

    public static LinkedList<StubOperation> parse(
            Element operationsNode,
            VirtualServiceObject vso) {

        LinkedList<StubOperation> operations =
                new LinkedList<>();

        for (Element opNode :
                XmlUtils.childElements(operationsNode)) {

            if (!"StubOperation".equals(
                    XmlUtils.local(opNode))) {
                continue;
            }

            StubOperation operation =
                    new StubOperation();

            operation.setName(
                    opNode.getAttribute("name"));

            operation.setBindingstubOperationName(
                    opNode.getAttribute(
                            "bindingstubOperationName"));

            operation.setDefaultRR(
                    opNode.getAttribute(
                            "defaultRR"));

            LinkedList<RRPair> rrPairs =
                    new LinkedList<>();

            for (Element child :
                    XmlUtils.childElements(opNode)) {

                switch (XmlUtils.local(child)) {

                    case "RequestData" ->
                            operation.setRequestList(
                                    RequestDataParser.parse(
                                            child));

                    case "Filters" ->
                            operation.setFilterList(
                                    FilterParser.parse(
                                            child));

                    case "DataGenerators" ->
                            operation.setDataGenerators(
                                    DataGeneratorParser.parse(
                                            child));

                    case "DataSourceSelect" ->
                            operation.setDataSourceSelect(
                                    DataSourceSelectParser.parse(
                                            child,
                                            operation,
                                            vso));

                    case "RRPair" ->
                            rrPairs.add(
                                    RRPairParser.parse(
                                            child));
                }
            }

            operation.setRrList(rrPairs);

            operations.add(operation);
        }

        return operations;
    }
}

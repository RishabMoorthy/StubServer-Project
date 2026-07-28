package com.stubio.parsers;

import com.stubio.util.DataGenerator;
import org.w3c.dom.Element;

import java.util.LinkedList;

public final class DataGeneratorParser {

    private DataGeneratorParser() {
    }

    public static LinkedList<DataGenerator> parse(
            Element generatorsNode) {

        LinkedList<DataGenerator> generators =
                new LinkedList<>();

        for (Element dgNode :
                XmlUtils.childElements(generatorsNode)) {

            if (!"DataGenerator".equals(
                    XmlUtils.local(dgNode))) {
                continue;
            }

            DataGenerator generator =
                    new DataGenerator();

            for (Element child :
                    XmlUtils.childElements(dgNode)) {

                switch (XmlUtils.local(child)) {

                    case "Type" ->
                            generator.setType(
                                    XmlUtils.text(child));

                    case "StartNumber" ->
                            generator.setStartNumber(
                                    Long.parseLong(
                                            XmlUtils.text(child)));

                    case "EndNumber" ->
                            generator.setEndNumber(
                                    Long.parseLong(
                                            XmlUtils.text(child)));

                    case "Variable" ->
                            generator.setVariable(
                                    XmlUtils.text(child));

                    case "Increment" ->
                            generator.setIncrement(
                                    Integer.parseInt(
                                            XmlUtils.text(child)));

                    case "Length" ->
                            generator.setLength(
                                    Integer.parseInt(
                                            XmlUtils.text(child)));

                    case "prefix" ->
                            generator.setPrefix(
                                    XmlUtils.text(child));
                }
            }

            generators.add(generator);
        }

        return generators;
    }
}

package com.stubio.testUtil;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.File;

public class StubTestWriter {

    public static void writeToXml(StubTest stubTest, File outputFile) {
        try {
            JAXBContext context = JAXBContext.newInstance(StubTest.class);

            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            marshaller.marshal(stubTest, outputFile);

        } catch (Exception e) {
            throw new RuntimeException("Failed to write StubTest XML", e);
        }
    }
}

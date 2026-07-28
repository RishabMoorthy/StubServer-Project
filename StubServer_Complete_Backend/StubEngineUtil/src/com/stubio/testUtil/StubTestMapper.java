package com.stubio.testUtil;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class StubTestMapper {

    public String path;
    public StubTest stubTest = new StubTest();

    public StubTestMapper(){}

    public StubTestMapper(String path){

        try {
            JAXBContext context = JAXBContext.newInstance(StubTest.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            stubTest =
                    (StubTest) unmarshaller.unmarshal(new File(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

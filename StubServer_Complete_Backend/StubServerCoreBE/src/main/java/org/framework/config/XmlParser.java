package org.framework.config;

import org.framework.core.ParsedXMLObject;

import java.io.File;
import java.nio.file.Files;

public class XmlParser {
    RestXmlParser restXmlParser;
    SoapXmlParser soapXmlParser;
    TcpXmlParser tcpXmlParser;
    public ParsedXMLObject parseXml(File xmlFile) throws Exception {
        String fileContent = Files.readString(xmlFile.toPath());
        ParsedXMLObject parsedObj = null;
        if(fileContent.contains("restMockService")){
            restXmlParser = new RestXmlParser();
            parsedObj = restXmlParser.parseRestRoutes(xmlFile);
        }
        else if(fileContent.contains("customMockService")){
            tcpXmlParser = new TcpXmlParser();
            parsedObj = tcpXmlParser.parseRestRoutes(xmlFile);
        }
        else if(fileContent.contains("mockService")){
            soapXmlParser = new SoapXmlParser();
            parsedObj = soapXmlParser.parseSoapRoutes(xmlFile);
        }

        return parsedObj;
    }
}

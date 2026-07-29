package org.framework.core;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Element;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RelaxedSchemaValidator {

    private javax.xml.validation.Schema compiledSchema;

    public RelaxedSchemaValidator(String wsdlContent) throws Exception {
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose", false);

        // Convert WSDL string to InputStream
        InputStream wsdlStream = new ByteArrayInputStream(wsdlContent.getBytes());

        // Read WSDL from InputStream
        Definition definition = wsdlReader.readWSDL(null, new InputSource(wsdlStream));

        Types types = definition.getTypes();
        List<Source> schemaSources = new ArrayList<>();

        if (types != null) {
            for (Object ext : types.getExtensibilityElements()) {
                if (ext instanceof Schema) {
                    Schema schemaExt = (Schema) ext;
                    Element schemaElement = schemaExt.getElement();
                    schemaSources.add(elementToSource(schemaElement));
                }
            }
        }

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setErrorHandler(new LenientErrorHandler());
        compiledSchema = factory.newSchema(schemaSources.toArray(new Source[0]));
    }

    private Source elementToSource(Element element) throws Exception {
        DOMSource domSource = new DOMSource(element);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(domSource, new StreamResult(outputStream));
        return new StreamSource(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    public boolean validate(String soapXml) {
        try {
            Validator validator = compiledSchema.newValidator();
            validator.setErrorHandler(new LenientErrorHandler());
            InputStream xmlStream = new ByteArrayInputStream(soapXml.getBytes());
            validator.validate(new StreamSource(xmlStream));
            return true;
        } catch (Exception e) {
            System.out.println("Validation failed: " + e.getMessage());
            return false;
        }
    }

    private static class LenientErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException exception) {
            System.out.println("Warning: " + exception.getMessage());
        }

        @Override
        public void error(SAXParseException exception) {
            System.out.println("Error (ignored): " + exception.getMessage());
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception; // Only fatal errors will stop validation
        }
    }

    public static void main(String[] args) throws Exception {
        RelaxedSchemaValidator validator = new RelaxedSchemaValidator("src/main/resources/service.wsdl");

        String soapRequest =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "xmlns:ws=\"http://ws.mfsafrica.com\" xmlns:xsd=\"http://mfs/xsd\">" +
                        "<soap:Header/>" +
                        "<soap:Body>" +
                        "<ws:account_request>" +
                        "<ws:login>" +
                        "<xsd:corporate_code>?</xsd:corporate_code>" +
                        "<xsd:password>?</xsd:password>" +
                        "</ws:login>" +
                        "<ws:to_country>?</ws:to_country>" +
                        "<ws:msisdn>26133123457199\"" +
                        "</ws:account_request>" +
                        "</soap:Body>" +
                        "</soap:Envelope>";

        boolean valid = validator.validate(soapRequest);
        System.out.println("Is request valid? " + valid);
    }
}

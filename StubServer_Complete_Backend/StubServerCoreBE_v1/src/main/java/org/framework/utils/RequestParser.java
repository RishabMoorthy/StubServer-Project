package org.framework.utils;

import java.io.StringReader;
import java.text.DecimalFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class RequestParser {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        RequestParser reqp = new RequestParser();
        String request = "{\r\n" +
                "   \"header\": {\r\n" +
                "       \"stx\": \"{\",\r\n" +
                "       \"proSeqNum\": \"0\",\r\n" +
                "       \"termId\": \"        \",\r\n" +
                "       \"formatVer\": \"00\",\r\n" +
                "       \"dateIndex\": \"000\",\r\n" +
                "       \"dataLen\": \"0113\",\r\n" +
                "       \"transType\": \"001\",\r\n" +
                "       \"msgType\": \"R\",\r\n" +
                "       \"etx\": \"}\"\r\n" +
                "   },\r\n" +
                "   \"body\": {\r\n" +
                "       \"txnType\": \"09\",\r\n" +
                "       \"efps\": [\r\n" +
                "           {\r\n" +
                "               \"tag\": \"001\",\r\n" +
                "               \"length\": 2,\r\n" +
                "               \"value\": \"09\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"002\",\r\n" +
                "               \"length\": 4,\r\n" +
                "               \"value\": \"0736\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"003\",\r\n" +
                "               \"length\": 6,\r\n" +
                "               \"value\": \"141132\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"004\",\r\n" +
                "               \"length\": 9,\r\n" +
                "               \"value\": \"WUPOSJUST\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"005\",\r\n" +
                "               \"length\": 3,\r\n" +
                "               \"value\": \"100\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"006\",\r\n" +
                "               \"length\": 4,\r\n" +
                "               \"value\": \"ADA5\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"018\",\r\n" +
                "               \"length\": 1,\r\n" +
                "               \"value\": \"D\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"019\",\r\n" +
                "               \"length\": 6,\r\n" +
                "               \"value\": \"KAURAV\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"020\",\r\n" +
                "               \"length\": 7,\r\n" +
                "               \"value\": \"RASTOGI\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"023\",\r\n" +
                "               \"length\": 6,\r\n" +
                "               \"value\": \"DALLAS\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"024\",\r\n" +
                "               \"length\": 2,\r\n" +
                "               \"value\": \"TX\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"027\",\r\n" +
                "               \"length\": 2,\r\n" +
                "               \"value\": \"US\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"046\",\r\n" +
                "               \"length\": 3,\r\n" +
                "               \"value\": \"USD\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"059\",\r\n" +
                "               \"length\": 3,\r\n" +
                "               \"value\": \"001\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"061\",\r\n" +
                "               \"length\": 9,\r\n" +
                "               \"value\": \"244Y00799\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"153\",\r\n" +
                "               \"length\": 3,\r\n" +
                "               \"value\": \"001\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"154\",\r\n" +
                "               \"length\": 1,\r\n" +
                "               \"value\": \"D\"\r\n" +
                "           },\r\n" +
                "           {\r\n" +
                "               \"tag\": \"219\",\r\n" +
                "               \"length\": 1,\r\n" +
                "               \"value\": \"D\"\r\n" +
                "           }\r\n" +
                "       ]\r\n" +
                "   },\r\n" +
                "   \"replyCode\": {\r\n" +
                "       \"tag\": \"059\",\r\n" +
                "       \"length\": 3,\r\n" +
                "       \"value\": \"001\"\r\n" +
                "   },\r\n" +
                "   \"replyText\": {\r\n" +
                "       \"tag\": \"TLV\",\r\n" +
                "       \"length\": 162,\r\n" +
                "       \"value\": \"00102090020407360030614113200409WUPOSJUST0050310000604ADA501801D01906KAURAV02007RASTOGI02306DALLAS02402TX02702US04603USD0590300106109244Y007991530300115401D21901D\"\r\n" +
                "   }\r\n" +
                "}";

        /*String request = "{\r\n" +
                "   \"len\": 0,\r\n" +
                "   \"transactionType\": 2,\r\n" +
                "   \"transactionSubType\": 9,\r\n" +
                "   \"termId\": \"W2VD\",\r\n" +
                "   \"operId\": \"TRA\",\r\n" +
                "   \"account\": \"AKC634506\",\r\n" +
                "   \"payeeName\": \"ERIK\\\\DOE\",\r\n" +
                "   \"senderPhone\": \"\",\r\n" +
                "   \"senderName\": \"JOHN\\\\DOE\",\r\n" +
                "   \"mocn\": \"\",\r\n" +
                "   \"mtcn\": null,\r\n" +
                "   \"principalLo\": 0,\r\n" +
                "   \"principalHi\": 0,\r\n" +
                "   \"dateLo\": \"\",\r\n" +
                "   \"dateHi\": \"\",\r\n" +
                "   \"senderAccount\": \"\",\r\n" +
                "   \"restrict\": \"\",\r\n" +
                "   \"localRemote\": \"\",\r\n" +
                "   \"pcVersion\": \"9303\",\r\n" +
                "   \"site\": \"MOM6\",\r\n" +
                "   \"reqStatus\": \"EG\",\r\n" +
                "   \"country\": \"\",\r\n" +
                "   \"creditCardNum\": \"\",\r\n" +
                "   \"queueType\": \"\",\r\n" +
                "   \"btalOpId\": \"\",\r\n" +
                "   \"payoutPin\": \"\",\r\n" +
                "   \"deviceId\": \"\",\r\n" +
                "   \"wuCardRcvr\": \"\",\r\n" +
                "   \"wuCardPromo\": \"\",\r\n" +
                "   \"wuCardLevel\": \"\",\r\n" +
                "   \"micrFlag\": \"\",\r\n" +
                "   \"wuCardPoints\": 0,\r\n" +
                "   \"searchCriteria\": \"\",\r\n" +
                "   \"tran\": \"SRCHWC\",\r\n" +
                "   \"payoutControl\": \"\",\r\n" +
                "   \"traceAuditNum\": \"\",\r\n" +
                "   \"hldTransFilter\": \"\",\r\n" +
                "   \"holdRelModel\": \"\",\r\n" +
                "   \"adntlPayInfoBuffer\": null,\r\n" +
                "   \"fxUpdateBuffer\": null,\r\n" +
                "   \"pageNumber\": null\r\n" +
                "}";*/

        String req = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soapenv:Body>" +
                "<ns:account_requestResponse xmlns:ns=\"http://ws.mfsafrica.com\">" +
                "   <ns:return xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns:Wallet\">" +
                "<ns:msisdn>${#MockService#msisdn#$}</ns:msisdn>" +
                "<ns:partner_code>OMG</ns:partner_code>" +
                "<ns:status xsi:type=\"ns:Status\">" +
                "<ns:status_code>Active</ns:status_code>" +
                "</ns:status>" +
                "</ns:return>" +
                "   </ns:account_requestResponse>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        String re = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<CardinalMPI>"
                + "<UCAFIndicator>2</UCAFIndicator>"
                + "<Xid>ajZiSTVEQlRyVlgxMVJoRWtQZjA=</Xid>"
                + "<PAResStatus>R</PAResStatus>"
                + "<EciFlag>00</EciFlag>"
                + "<Enrolled>Y</Enrolled>"
                + "<TransactionId>j6bI5DBTrbX11RhEkPf0</TransactionId>"
                + "<ThreeDSVersion>2.0.1</ThreeDSVersion>"
                + "<SignatureVerification>Y</SignatureVerification>"
                + "<CavvAlgorithm />"
                + "<CardBin>517274</CardBin>"
                + "<ErrorDesc />"
                + "<ErrorNo>0</ErrorNo>"
                + "<DSTransactionId>d1867ea2-41f6-8b7e-05d85af898a8</DSTransactionId>"
                + "<CardBrand>MASTERCARD</CardBrand>"
                + "<Cavv />"
                + "</CardinalMPI>";
        String field = "${#MockResponse#Request#$['body']['efps'][8]['value']}";
        String fiel = "${#MockResponse#Request#//CardinalMPI[1]/TransactionId[1]}";
        String fie = "//ns:account_requestResponse[1]/ns:return[1]/ax21:partner_code[1]";
        //reqp.parseJSON(request);
        try {
            reqp.getRequestValue(request, field);
        } catch (Exception e) {
            System.out.println("Exception " + e);
        }
    }

    public Object getRequestValue(String request, String field) {
        Object value = "";
        String type = "json";

        try {
            JSONObject reqObj = new JSONObject(request);
        } catch (Exception e) {
            type = "xml";
        }
        //System.out.println("Type :"+type);
        if (type.equals("json"))
            value = getJSONvalue(request, field);
        else
            value = getXMLvalue(request, field);
        return value;
    }

    private Object getXMLvalue(String request, String field) {
        // TODO Auto-generated method stub
        System.out.println("in xml");
        Object value = "";
        if (field.contains("MockResponse")) {
            field = field.substring(field.indexOf("#Request") + 9, field.indexOf("}"));
        }

        try {
            GroovyUtils utils = new GroovyUtils(null);
            value = utils.getXmlHolder(request).getNodeValue(field);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        System.out.println("value = " + value);
        return value;
    }

    public String getJSONvalue(String request, String field) {
        try {
            String value = "";
            if (field.contains("MockResponse")) {
                field = field.substring(field.indexOf("["), field.indexOf("}"));
            }
            field = "$" + field;
            // System.out.println("Field : "+field);
            DocumentContext jsonContext = JsonPath.parse(request);
            value = jsonContext.read(field, String.class);
            if (isDouble(value)) {
                // System.out.println("true");
                double input = Double.parseDouble(value);
                DecimalFormat df = new DecimalFormat("0.00");
                // System.out.println("true"+df.format(input));
                value = df.format(input).toString();
            }
            //List<String> jsonpathCreatorLocation = jsonContext.read(jsonpathCreatorLocationPath);
            // System.out.println("Value : "+value);
            return value;
        } catch (Exception e) {
            Logger.getInstance().error("Error in getJsonValue ", e);
            return "";
        }
    }

    boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return value.contains(",");
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

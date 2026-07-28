<?xml version="1.0" encoding="UTF-8"?>
<vs:stubVirtualService xmlns:vs="http://wu.com/stubio/config" name="CustomSOAPVirtualService"
    projectId="ghfdgf-75658-jhgjhgf-8476875" createdVersion="1.0"
    lastUpdated="2025-06-17T10:42:49Z" encryption="None" environment="DefaultEnv">
    <vs:ServiceDefinition>
        <!-- Can be WSDL/req-rsp pairs based on mode of creation-->
        <vs:WSDLMetadata type="TEXT" rootPart="file:/E:/hello_service.wsdl">
            <vs:WSDLPart>
                <vs:WSDLURL>file:/E:/hello_service.wsdl</vs:WSDLURL>
                <vs:WSDLText>WSDL CONTENT</vs:WSDLText>
            </vs:WSDLPart>
        </vs:WSDLMetadata>
    </vs:ServiceDefinition>
    <vs:Configuration>
        <vs:Variables>
            <vs:Variable>
                <vs:type>global</vs:type>
                <vs:name>varName</vs:name>
                <vs:value>value</vs:value>
                <vs:pathType/>
                <vs:path/>
            </vs:Variable>
            <vs:Variable>
                <vs:type>local</vs:type>
                <vs:name>varName</vs:name>
                <vs:value>value</vs:value>
                <vs:pathType/>
                <vs:path/>
            </vs:Variable>
            <vs:Variable>
                <vs:type>request</vs:type>
                <vs:name>varName</vs:name>
                <vs:value>value</vs:value>
                <vs:pathType>XPath</vs:pathType>
                <vs:path>/request/xml/abc</vs:path>
            </vs:Variable>
        </vs:Variables>
    </vs:Configuration>
    <vs:DataSources>
        <vs:DataSource>
            <vs:ConnectionName>SV DB</vs:ConnectionName>
            <vs:Driver>JDBC</vs:Driver>
            <vs:Host>10.44.16.44</vs:Host>
            <vs:Port>1152</vs:Port>
            <vs:SID>CATDM</vs:SID>
            <vs:User>SV_STAGING</vs:User>
            <vs:Password>Welcome123</vs:Password>
            <vs:ConProperty>
                <vs:Key/>
                <vs:Value/>
            </vs:ConProperty>
        </vs:DataSource>
    </vs:DataSources>
    <vs:SOAPService host="localhost" port="8080" contextPath="/" autoRestart="true" allowFallback="false" isSecured="true">
        <vs:ExecutionMode>
            <vs:ExecutionModeValue/>
            <vs:LiveURLs>
                <vs:LiveURL>
                    <vs:EnvType>QA</vs:EnvType>
                    <vs:TransType>http</vs:TransType>
                    <vs:Host>localhost</vs:Host>
                    <vs:Port>8099</vs:Port>
                    <vs:BasePath>/service</vs:BasePath>
                </vs:LiveURL>
            </vs:LiveURLs>
        </vs:ExecutionMode>
        <vs:SecuritySettings>
            <vs:Keystore>
                <vs:Path>/opt/certs/server-keystore.jks</vs:Path>
                <vs:Type>JKS</vs:Type>
                <vs:Password>changeit</vs:Password>
                <vs:KeyAlias>server-cert</vs:KeyAlias>
            </vs:Keystore>
            <vs:TrustStore>
                <vs:Path>/opt/certs/client-truststore.jks</vs:Path>
                <vs:Type>JKS</vs:Type>
                <vs:Password>changeit</vs:Password>
            </vs:TrustStore>
            <vs:ClientAuth>true</vs:ClientAuth>
        </vs:SecuritySettings>
        <vs:DefaultError>
            <vs:Response statusCode="200" statusMsg="OK" contentType="text/xml" responseDelay="0">
                <vs:Headers>
                    <vs:Header>
                        <vs:Key>Custom</vs:Key>
                        <vs:Value>value</vs:Value>
                    </vs:Header>
                </vs:Headers>
                <vs:body>Error Message</vs:body>
            </vs:Response>
        </vs:DefaultError>
        <vs:headerValidator type="NONE"/>
        <!--WSDL driven operation -->
        <vs:StubOperations>
            <vs:StubOperation name="sayHello" bindingstubOperationName="sayHello" defaultRR="43657843">
                <vs:DataSourceSelect>
                    <vs:Database/>
                    <vs:File>
                        <vs:RequestColumnMappings/>
                    </vs:File>
                </vs:DataSourceSelect>
                <vs:DataGenerators>
                    <vs:DataGenerator>
                        <vs:Type>Random Number</vs:Type>
                        <vs:StartNumber>1000000000</vs:StartNumber>
                        <vs:EndNumber>9999999999</vs:EndNumber>
                        <vs:Variable>GenNumber</vs:Variable>
                    </vs:DataGenerator>
                    <vs:DataGenerator>
                        <vs:Type>Sequential Number</vs:Type>
                        <vs:StartNumber>1000000000</vs:StartNumber>
                        <vs:EndNumber>99999999999999</vs:EndNumber>
                        <vs:Increment>2</vs:Increment>
                        <vs:Variable>GenNumber</vs:Variable>
                    </vs:DataGenerator>
                    <vs:DataGenerator>
                        <vs:Type>Random String</vs:Type>
                        <vs:Length>10</vs:Length>
                        <vs:Variable>GenString</vs:Variable>
                        <vs:prefix>test-</vs:prefix>
                    </vs:DataGenerator>
                </vs:DataGenerators>
                <vs:Filters/>
                <vs:RequestData>
                    <vs:Request>
                        <vs:ContentType>XML</vs:ContentType>
                        <vs:Body/>
                        <vs:Name>Request1</vs:Name>
                    </vs:Request>
                    <vs:Request>
                        <vs:ContentType>JSON</vs:ContentType>
                        <vs:Body/>
                        <vs:Name>Request2</vs:Name>
                    </vs:Request>
                </vs:RequestData>
                <vs:RRPair id="43657843" defaultResponse="DefaultSuccess">
                    <vs:Request contentType="application/xml">
                        <vs:RequestData>Sample Request</vs:RequestData>
                        <vs:RequestParameters>
                            <vs:arg name="account" matchType="=" case="true" echoValue="true">1234</vs:arg>
                            <vs:arg name="amount" matchType="&gt;" case="false" echoValue="false">100</vs:arg>
                        </vs:RequestParameters>
                    </vs:Request>
                    <vs:ResponseSelection>
                        <vs:MatchStyle>Script</vs:MatchStyle>
                        <vs:MatchScript>
                            <vs:ScriptLang>Groovy</vs:ScriptLang>
                            <vs:Script>return "DefaultSuccess"</vs:Script>
                        </vs:MatchScript>
                    </vs:ResponseSelection>
                    <vs:ResponseSet>
                        <vs:Response name="DefaultSuccess" statusCode="200" httpMsg="OK" contentType="text/xml" responseDelay="0">
                            <vs:Body>Hello, this is a sample response</vs:Body>
                            <vs:ResponseScript>
                                <vs:ScriptLang>Groovy</vs:ScriptLang>
                                <vs:Script>//Script</vs:Script>
                            </vs:ResponseScript>
                            <vs:CustomHeaders>
                                <vs:Header>
                                    <vs:key>Message</vs:key>
                                    <vs:Value>Success</vs:Value>
                                </vs:Header>
                            </vs:CustomHeaders>
                        </vs:Response>
                        <vs:Response name="ErrorResponse" statusCode="200" httpMsg="OK" contentType="text/xml" responseDelay="0">
                            <vs:Body>Hello, this is a sample response</vs:Body>
                            <vs:ResponseScript>
                                <vs:ScriptLang>Groovy</vs:ScriptLang>
                                <vs:Script>//Script</vs:Script>
                            </vs:ResponseScript>
                            <vs:CustomHeaders>
                                <vs:Header>
                                    <vs:key>Message</vs:key>
                                    <vs:Value>Success</vs:Value>
                                </vs:Header>
                            </vs:CustomHeaders>
                        </vs:Response>
                    </vs:ResponseSet>
                </vs:RRPair>
                <vs:RRPair id="74657435">
                    <vs:Request contentType="json">
                        <vs:RequestData>Sample Request</vs:RequestData>
                        <vs:RequestParameters>
                            <vs:arg name="account" matchType="=" case="true" echoValue="true">1235</vs:arg>
                            <vs:arg name="amount" matchType="&gt;" case="false" echoValue="false">100</vs:arg>
                        </vs:RequestParameters>
                    </vs:Request>
                    <vs:ResponseSelection>
                        <vs:MatchStyle>Operation</vs:MatchStyle>
                        <vs:MatchScript/>
                    </vs:ResponseSelection>
                    <vs:ResponseSet>
                        <vs:Response name="ErrorResponse" statusCode="200" httpMsg="OK" contentType="text/xml" responseDelay="0">
                            <vs:Body>Hello, this is a sample response</vs:Body>
                            <vs:ResponseScript>
                                <vs:ScriptLang>Groovy</vs:ScriptLang>
                                <vs:Script>//Script</vs:Script>
                            </vs:ResponseScript>
                            <vs:CustomHeaders>
                                <vs:Header>
                                    <vs:key>Message</vs:key>
                                    <vs:Value>Success</vs:Value>
                                </vs:Header>
                            </vs:CustomHeaders>
                        </vs:Response>
                    </vs:ResponseSet>
                </vs:RRPair>
            </vs:StubOperation>
        </vs:StubOperations>
        <vs:CustomScripts>
            <vs:CustomScript>
                <vs:ScriptLang>Groovy</vs:ScriptLang>
                <vs:ExecutionType>VS Start</vs:ExecutionType>
                <vs:Script>//Custom Script</vs:Script>
            </vs:CustomScript>
            <vs:CustomScript>
                <vs:ScriptLang>Groovy</vs:ScriptLang>
                <vs:ExecutionType>VS Stop</vs:ExecutionType>
                <vs:Script>//Custom Script</vs:Script>
            </vs:CustomScript>
            <vs:CustomScript>
                <vs:ScriptLang>Groovy</vs:ScriptLang>
                <vs:ExecutionType>On Request</vs:ExecutionType>
                <vs:Script>//Custom Script</vs:Script>
            </vs:CustomScript>
            <vs:CustomScript>
                <vs:ScriptLang>Groovy</vs:ScriptLang>
                <vs:ExecutionType>On Response</vs:ExecutionType>
                <vs:Script>//Custom Script</vs:Script>
            </vs:CustomScript>
        </vs:CustomScripts>
        <vs:Config>
            <vs:ThreadPool maxThreads="20" coreThreads="5"/>
            <vs:ResponseDelay>0</vs:ResponseDelay>
        </vs:Config>
    </vs:SOAPService>
    <vs:Metadata/>
</vs:stubVirtualService>

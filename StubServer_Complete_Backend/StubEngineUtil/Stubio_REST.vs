<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<vs:stubVirtualService xmlns:vs="http://wu.com/stubio/config" environment="QA" lastUpdated="2026-04-02" name="CustomVirtualService">
    <vs:Properties>
        <vs:Variable>
            <vs:Key>Message</vs:Key>
            <vs:Value>Success</vs:Value>
        </vs:Variable>
        <vs:Variable>
            <vs:Key>Key</vs:Key>
            <vs:Value>Val</vs:Value>
        </vs:Variable>
    </vs:Properties>
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
    <vs:ExecutionMode>
        <vs:ExecutionModeValue>Live Invocation</vs:ExecutionModeValue>
        <vs:LiveURLs>
            <vs:LiveURL active="true">
                <vs:EnvType>QA</vs:EnvType>
                <vs:TransType>http</vs:TransType>
                <vs:Host>localhost</vs:Host>
                <vs:Port>8099</vs:Port>
                <vs:BasePath>/service</vs:BasePath>
            </vs:LiveURL>
        </vs:LiveURLs>
    </vs:ExecutionMode>
    <vs:DefaultError>
        <vs:Response contentType="application/json" responseDelay="10" statusCode="200" statusMsg="OK">
            <vs:Headers>
                <vs:Header>
                    <vs:Key>Custom</vs:Key>
                    <vs:Value>value</vs:Value>
                </vs:Header>
            </vs:Headers>
            <vs:body><![CDATA[{
"error": "Error Message"
}]]></vs:body>
        </vs:Response>
    </vs:DefaultError>
    <vs:RestService allowFallback="false" autoRestart="true" contextPath="/" host="localhost" isSecured="false" port="8089">
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
            <vs:ServerAuth>true</vs:ServerAuth>
        </vs:SecuritySettings>
        <vs:Endpoints>
            <vs:Endpoint method="GET" name="RootGetEndpoint" path="/admin/service" defaultRR="43657843">
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
                        <vs:IsAlphabet>true</vs:IsAlphabet>
                        <vs:IsNumeric>false</vs:IsNumeric>
                        <vs:IsSpecial>true</vs:IsSpecial>
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
                <vs:RRPair defaultResponse="DefaultSuccess" id="43657843">
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
                            <vs:Script/>
                        </vs:MatchScript>
                    </vs:ResponseSelection>
                    <vs:ResponseSet>
                        <vs:Response contentType="text/xml" httpMsg="OK" name="DefaultSuccess" responseDelay="0" statusCode="200">
                            <vs:Body><![CDATA[Hello, this is a sample response]]></vs:Body>
                            <vs:ResponseScript>
                                <vs:ScriptLang>Groovy</vs:ScriptLang>
                                <vs:Script/>
                            </vs:ResponseScript>
                            <vs:CustomHeaders>
                                <vs:Header>
                                    <vs:Key>Message</vs:Key>
                                    <vs:Value>Success</vs:Value>
                                </vs:Header>
                            </vs:CustomHeaders>
                        </vs:Response>
                        <vs:Response contentType="text/xml" httpMsg="OK" name="ErrorResponse" responseDelay="0" statusCode="200">
                            <vs:Body><![CDATA[Hello, this is a sample response]]></vs:Body>
                            <vs:ResponseScript>
                                <vs:ScriptLang>Groovy</vs:ScriptLang>
                                <vs:Script/>
                            </vs:ResponseScript>
                            <vs:CustomHeaders>
                                <vs:Header>
                                    <vs:Key>Message</vs:Key>
                                    <vs:Value>Success</vs:Value>
                                </vs:Header>
                            </vs:CustomHeaders>
                        </vs:Response>
                    </vs:ResponseSet>
                </vs:RRPair>
                <vs:RRPair id="74657435">
                    <vs:Request/>
                    <vs:ResponseSelection>
                        <vs:MatchStyle>Operation</vs:MatchStyle>
                        <vs:MatchScript>
                            <vs:ScriptLang>Groovy</vs:ScriptLang>
                            <vs:Script/>
                        </vs:MatchScript>
                    </vs:ResponseSelection>
                    <vs:ResponseSet>
                        <vs:Response contentType="text/xml" httpMsg="OK" name="ErrorResponse" responseDelay="0" statusCode="200">
                            <vs:Body><![CDATA[Hello, this is a sample response]]></vs:Body>
                            <vs:ResponseScript>
                                <vs:ScriptLang>Groovy</vs:ScriptLang>
                                <vs:Script/>
                            </vs:ResponseScript>
                            <vs:CustomHeaders>
                                <vs:Header>
                                    <vs:Key>Message</vs:Key>
                                    <vs:Value>Success</vs:Value>
                                </vs:Header>
                            </vs:CustomHeaders>
                        </vs:Response>
                    </vs:ResponseSet>
                </vs:RRPair>
            </vs:Endpoint>
            <vs:Endpoint method="POST" name="RootGetEndpoint" path="/admin/service">
                <vs:DataSourceSelect>
                    <vs:Database>
                        <vs:ConnectionName>SV DB</vs:ConnectionName>
                        <vs:DataSourceName>SV DataSource Name</vs:DataSourceName>
                        <vs:Query><![CDATA[Query]]></vs:Query>
                        <vs:ResultProperties>
                            <vs:ResultProperty>
                                <vs:Name>DataSource.METID</vs:Name>
                                <vs:ColumnName/>
                            </vs:ResultProperty>
                        </vs:ResultProperties>
                    </vs:Database>
                    <vs:File>
                        <vs:DataSourceName>SV File</vs:DataSourceName>
                        <vs:FileLocation>C://Users//Sample.xlsx</vs:FileLocation>
                        <vs:FileType>Excel</vs:FileType>
                        <vs:Sheet>Sheet 1</vs:Sheet>
                        <vs:MappingType>Request Parameters</vs:MappingType>
                        <vs:RequestName>Request1</vs:RequestName>
                        <vs:RequestColumnMappings>
                            <vs:RequestColumnMapping>
                                <vs:RequestMapping>Sample.Request.field1</vs:RequestMapping>
                                <vs:ComparisonType>Equals</vs:ComparisonType>
                                <vs:ColumnName>Account</vs:ColumnName>
                            </vs:RequestColumnMapping>
                            <vs:RequestColumnMapping>
                                <vs:RequestMapping>Sample.Request.field2</vs:RequestMapping>
                                <vs:ComparisonType>Equals</vs:ComparisonType>
                                <vs:ColumnName>Name</vs:ColumnName>
                            </vs:RequestColumnMapping>
                        </vs:RequestColumnMappings>
                    </vs:File>
                    <vs:File>
                        <vs:DataSourceName>SV File1</vs:DataSourceName>
                        <vs:FileLocation>C://Users//Sample.xlsx</vs:FileLocation>
                        <vs:FileType>Excel</vs:FileType>
                        <vs:Sheet>Sheet 1</vs:Sheet>
                        <vs:MappingType>Random</vs:MappingType>
                        <vs:RandomCount>10</vs:RandomCount>

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
                <vs:Filters>
                    <vs:Variable>
                        <vs:Type>XPath</vs:Type>
                        <vs:Name>varName</vs:Name>
                        <vs:Path>/xml/val</vs:Path>
                        <vs:RequestName>Request1</vs:RequestName>
                    </vs:Variable>
                    <vs:Variable>
                        <vs:Type>JsonPath</vs:Type>
                        <vs:Name>varName1</vs:Name>
                        <vs:Path>json.val</vs:Path>
                        <vs:RequestName>Request2</vs:RequestName>
                    </vs:Variable>
                    <vs:Variable>
                        <vs:Type>Timestamp Filter</vs:Type>
                        <vs:Name>currentTime</vs:Name>
                        <vs:DateFormat>mm/dd/yyyy</vs:DateFormat>
                        <vs:Offset>1</vs:Offset>
                    </vs:Variable>
                    <vs:Variable>
                        <vs:Type>Boundary Condition Filter</vs:Type>
                        <vs:Name>part1</vs:Name>
                        <vs:StartText>abc</vs:StartText>
                        <vs:EndText>xyz</vs:EndText>
                    </vs:Variable>
                    <vs:Variable>
                        <vs:Type>Save Property Value to File</vs:Type>
                        <vs:Name>file1</vs:Name>
                        <vs:FilePath>/xml/val</vs:FilePath>
                        <vs:PropertyName>Request1</vs:PropertyName>
                        <vs:AppendMode>true</vs:AppendMode>
                    </vs:Variable>
                </vs:Filters>
                <vs:RequestData>
                    <vs:Request>
                        <vs:ContentType>application/xml</vs:ContentType>
                        <vs:Body>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;user id="12345"&gt;
    &lt;personalInfo&gt;
        &lt;firstName&gt;Elara&lt;/firstName&gt;
        &lt;lastName&gt;Quinn&lt;/lastName&gt;
        &lt;email verified="true"&gt;elara.quinn@example.com&lt;/email&gt;
        &lt;phone type="mobile"&gt;+1-555-0123&lt;/phone&gt;
    &lt;/personalInfo&gt;
    &lt;address&gt;
        &lt;city&gt;San Francisco&lt;/city&gt;
        &lt;state&gt;CA&lt;/state&gt;
    &lt;/address&gt;
&lt;/user&gt;</vs:Body>
                        <vs:Name>Request1</vs:Name>
                    </vs:Request>
                    <vs:Request>
                        <vs:ContentType>application/json</vs:ContentType>
                        <vs:Body>{
  "firstName": "Jane",
  "lastName": "Doe",
  "age": 25,
  "isStudent": false,
  "hobbies": ["reading", "cycling"]
}</vs:Body>
                        <vs:Name>Request2</vs:Name>
                    </vs:Request>
                </vs:RequestData>
                <vs:RRPair defaultResponse="DefaultSuccess" id="43657847">
                    <vs:Request contentType="application/json">
                        <vs:RequestData><![CDATA[Sample Request]]></vs:RequestData>
                        <vs:RequestParameters>
                            <vs:arg case="true" echoValue="true" matchType="Equals" name="account">1234</vs:arg>
                            <vs:arg case="false" echoValue="false" matchType="Greater Than" name="amount">100</vs:arg>
                        </vs:RequestParameters>
                        <vs:Headers>
                            <vs:arg case="true" echoValue="true" matchType="Equals" name="account">1234</vs:arg>
                        </vs:Headers>
                        <vs:QueryParams>
                            <vs:arg case="true" echoValue="true" matchType="Equals" name="account">1234</vs:arg>
                        </vs:QueryParams>
                    </vs:Request>
                    <vs:ResponseSelection>
                        <vs:MatchStyle>Script</vs:MatchStyle>
                        <vs:MatchScript>
                            <vs:ScriptLang>Groovy</vs:ScriptLang>
                            <vs:Script/>
                        </vs:MatchScript>
                    </vs:ResponseSelection>
                    <vs:ResponseSet>
                        <vs:Response contentType="text/xml" httpMsg="OK" name="DefaultSuccess" responseDelay="0" statusCode="200">
                            <vs:Body><![CDATA[Hello, this is a sample response]]></vs:Body>
                            <vs:ResponseScript>
                                <vs:ScriptLang>Groovy</vs:ScriptLang>
                                <vs:Script/>
                            </vs:ResponseScript>
                            <vs:CustomHeaders>
                                <vs:Header>
                                    <vs:Key>Message</vs:Key>
                                    <vs:Value>Success</vs:Value>
                                </vs:Header>
                            </vs:CustomHeaders>
                        </vs:Response>
                        <vs:Response contentType="text/xml" httpMsg="OK" name="ErrorResponse" responseDelay="0" statusCode="200">
                            <vs:Body><![CDATA[Hello, this is a sample response]]></vs:Body>
                            <vs:ResponseScript>
                                <vs:ScriptLang>Groovy</vs:ScriptLang>
                                <vs:Script/>
                            </vs:ResponseScript>
                            <vs:CustomHeaders>
                                <vs:Header>
                                    <vs:Key>Message</vs:Key>
                                    <vs:Value>Success</vs:Value>
                                </vs:Header>
                            </vs:CustomHeaders>
                        </vs:Response>
                    </vs:ResponseSet>
                </vs:RRPair>
                <vs:RRPair id="74657435">
                    <vs:Request>
                        <vs:RequestData><![CDATA[Sample Request]]></vs:RequestData>
                        <vs:RequestParameters>
                            <vs:arg case="true" echoValue="true" matchType="=" name="account">1235</vs:arg>
                            <vs:arg case="false" echoValue="false" matchType="&lt;" name="amount">100</vs:arg>
                        </vs:RequestParameters>
                    </vs:Request>
                    <vs:ResponseSelection>
                        <vs:MatchStyle>Operation</vs:MatchStyle>
                        <vs:MatchScript>
                            <vs:ScriptLang>Groovy</vs:ScriptLang>
                            <vs:Script/>
                        </vs:MatchScript>
                    </vs:ResponseSelection>
                    <vs:ResponseSet>
                        <vs:Response contentType="text/xml" httpMsg="OK" name="ErrorResponse" responseDelay="0" statusCode="200">
                            <vs:Body><![CDATA[Hello, this is a sample response]]></vs:Body>
                            <vs:ResponseScript>
                                <vs:ScriptLang>Groovy</vs:ScriptLang>
                                <vs:Script/>
                            </vs:ResponseScript>
                            <vs:CustomHeaders>
                                <vs:Header>
                                    <vs:Key>Message</vs:Key>
                                    <vs:Value>Success</vs:Value>
                                </vs:Header>
                            </vs:CustomHeaders>
                        </vs:Response>
                    </vs:ResponseSet>
                </vs:RRPair>
            </vs:Endpoint>
        </vs:Endpoints>
    </vs:RestService>
    <vs:CustomScripts>
        <vs:CustomScript>
            <vs:ScriptLang>Groovy</vs:ScriptLang>
            <vs:ExecutionType>VS Start</vs:ExecutionType>
            <vs:Script><![CDATA[//Custom Script

String abc;]]></vs:Script>
        </vs:CustomScript>
        <vs:CustomScript>
            <vs:ScriptLang>Groovy</vs:ScriptLang>
            <vs:ExecutionType>VS Stop</vs:ExecutionType>
            <vs:Script><![CDATA[//Custom Script]]></vs:Script>
        </vs:CustomScript>
        <vs:CustomScript>
            <vs:ScriptLang>Groovy</vs:ScriptLang>
            <vs:ExecutionType>On Request</vs:ExecutionType>
            <vs:Script><![CDATA[//Custom Script]]></vs:Script>
        </vs:CustomScript>
        <vs:CustomScript>
            <vs:ScriptLang>Groovy</vs:ScriptLang>
            <vs:ExecutionType>On Response</vs:ExecutionType>
            <vs:Script><![CDATA[//Custom Script]]></vs:Script>
        </vs:CustomScript>
    </vs:CustomScripts>
    <vs:Config>
        <vs:ThreadPool coreThreads="2" maxThreads="2"/>
        <vs:ResponseDelay>0</vs:ResponseDelay>
    </vs:Config>
    <vs:Metadata/>
</vs:stubVirtualService>

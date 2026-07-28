# StubEngineUtil

Java project reconstructed from IDE screenshots (com.stubio packages).

## Structure

```
src/com/stubio/
├── parsers/    (21 files) — XML parsers for virtual-service config
├── test/       (1 file)   — StubEngineUtilTest (main class)
├── testUtil/   (16 files) — JAXB model classes for StubTest XML
└── util/       (21 files) — core model classes
```

## Dependency

Requires `jakarta.xml.bind-api-4.1.0-M1.jar` (plus a JAXB runtime
implementation such as `org.glassfish.jaxb:jaxb-runtime`) on the
classpath — place it in a `lib/` folder as in the original project.

Language level: Java 17+ (uses arrow-style switch and pattern
matching for instanceof).

## ⚠️ Missing files (not yet extracted — project will NOT compile until added)

The following classes in `com.stubio.util` are referenced by the code
but their source was not shared yet:

- SecuritySettings.java
- SOAPService.java
- StubOperation.java
- VirtualServiceMapper.java
- VirtualServiceObject.java
- WSDLMetadata.java

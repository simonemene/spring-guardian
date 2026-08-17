package com.example.guardian.core.rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecureXmlParserTest {

    @Test
    void parsesOrdinaryPomLikeXml() throws Exception {
        var document = SecureXmlParser.parse("<project><artifactId>guardian-demo</artifactId></project>");

        assertEquals("guardian-demo", document.getElementsByTagName("artifactId").item(0).getTextContent());
    }

    @Test
    void rejectsDoctypeAndExternalEntityDeclarations() {
        String xml = """
                <?xml version="1.0"?>
                <!DOCTYPE project [<!ENTITY xxe SYSTEM "file:///etc/passwd">]>
                <project><artifactId>&xxe;</artifactId></project>
                """;

        assertThrows(Exception.class, () -> SecureXmlParser.parse(xml));
    }
}

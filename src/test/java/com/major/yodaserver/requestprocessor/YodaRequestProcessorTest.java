package com.major.yodaserver.requestprocessor;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.major.yodaserver.helper.VerifierSocket;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class YodaRequestProcessorTest {
    private static final String TEST_RESOURCES = "src/test/resources";

    @Test
    public void imageResourceRequested_imageResourceReturned() throws Exception {
        // given
        URL resource = getClass().getClassLoader().getResource("test/image01.png");
        byte[] expectedFileContent = Files.readAllBytes(Paths.get(resource.toURI()));
        VerifierSocket verifierSocket = new VerifierSocket();
        verifierSocket.addRequestLine("GET /test/image01.png HTTP/1.1");
        YodaRequestProcessor requestProcessor = new YodaRequestProcessor(new File(TEST_RESOURCES), verifierSocket);
        // when
        requestProcessor.run();
        // then
        List<String> responseHeaderLines = verifierSocket.getHeaderLines();
        assertEquals(5, responseHeaderLines.size());
        Iterator<String> itHeaderLines = responseHeaderLines.iterator();
        assertEquals("HTTP/1.1 200 OK", itHeaderLines.next());
        assertEquals("Server: YodaServer 0.0.1", itHeaderLines.next());
        assertEquals("Content-Length: " + expectedFileContent.length, itHeaderLines.next());
        assertEquals("Content-Type: image/png", itHeaderLines.next());
        assertEquals("", itHeaderLines.next());
        assertArrayEquals(expectedFileContent, verifierSocket.bodyAsBytes());
    }

    @Test
    public void nonSupportedMethod_generatesErrorResponse() {
        // given
        VerifierSocket verifierSocket = new VerifierSocket();
        verifierSocket.addRequestLine("PUT /a/file.html HTTP/1.1");
        YodaRequestProcessor requestProcessor = new YodaRequestProcessor(null, verifierSocket);
        // when
        requestProcessor.run();
        // then
        List<String> responseHeaderLines = verifierSocket.getHeaderLines();
        assertEquals(5, responseHeaderLines.size());
        Iterator<String> itHeaderLines = responseHeaderLines.iterator();
        assertEquals("HTTP/1.1 501 Not Implemented", itHeaderLines.next());
        assertEquals("Server: YodaServer 0.0.1", itHeaderLines.next());
        assertEquals("Content-Length: 94", itHeaderLines.next());
        assertEquals("Content-type: text/html", itHeaderLines.next());
        assertEquals("", itHeaderLines.next());
        assertEquals("<HTML><HEAD><TITLE>Not yet supported</TITLE></HEAD><BODY>501 - Not yet supported</BODY></HTML>",
                     verifierSocket.bodyAsString());
    }

}
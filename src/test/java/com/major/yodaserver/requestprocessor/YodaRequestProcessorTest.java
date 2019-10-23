package com.major.yodaserver.requestprocessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.major.yodaserver.helper.VerifierSocket;

import static com.major.yodaserver.requestprocessor.YodaRequestProcessor.NOT_FOUND_MESSAGE_BODY;
import static com.major.yodaserver.requestprocessor.YodaRequestProcessor.NOT_SUPPORTED_MESSAGE_BODY;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class YodaRequestProcessorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void requestedResourceDoesNotExist_notFoundReturned() {
        // given
        VerifierSocket verifierSocket = new VerifierSocket();
        verifierSocket.addRequestLine("GET /does-not-exists HTTP/1.1");
        YodaRequestProcessor requestProcessor = new YodaRequestProcessor(tempFolder.getRoot(), verifierSocket, null);
        // when
        requestProcessor.run();
        // then
        List<String> responseHeaderLines = verifierSocket.getHeaderLines();
        assertEquals(5, responseHeaderLines.size());
        Iterator<String> itHeaderLines = responseHeaderLines.iterator();
        assertEquals("HTTP/1.1 404 File Not Found", itHeaderLines.next());
        assertEquals("Server: YodaServer 0.0.1", itHeaderLines.next());
        assertEquals("Content-Length: " + NOT_FOUND_MESSAGE_BODY.getBytes().length, itHeaderLines.next());
        assertEquals("Content-type: text/html", itHeaderLines.next());
        assertEquals("", itHeaderLines.next());
        assertEquals(NOT_FOUND_MESSAGE_BODY, verifierSocket.bodyAsString());
    }

    @Test
    public void requestedResourceExistButAboveRootInHierarchy_notFoundReturned() throws IOException {
        // given
        tempFolder.newFolder("above-root-2", "root");
        File root = new File(tempFolder.getRoot() + "/above-root-2/root");
        VerifierSocket verifierSocket = new VerifierSocket();
        verifierSocket.addRequestLine("GET /../ HTTP/1.1");
        YodaRequestProcessor requestProcessor = new YodaRequestProcessor(root, verifierSocket, null);
        // when
        requestProcessor.run();
        // then
        List<String> responseHeaderLines = verifierSocket.getHeaderLines();
        assertEquals(5, responseHeaderLines.size());
        Iterator<String> itHeaderLines = responseHeaderLines.iterator();
        assertEquals("HTTP/1.1 404 File Not Found", itHeaderLines.next());
        assertEquals("Server: YodaServer 0.0.1", itHeaderLines.next());
        assertEquals("Content-Length: " + NOT_FOUND_MESSAGE_BODY.getBytes().length, itHeaderLines.next());
        assertEquals("Content-type: text/html", itHeaderLines.next());
        assertEquals("", itHeaderLines.next());
        assertEquals(NOT_FOUND_MESSAGE_BODY, verifierSocket.bodyAsString());
    }
    
    @Test
    public void imageResourceRequested_imageResourceReturned() throws IOException {
        // given
        byte[] expectedFileContent = Files.readAllBytes(Paths.get("src", "test", "resources", "test", "image01.png"));
        File testFile = new File(tempFolder.newFolder("test").getPath() + "/image01.png");
        testFile.createNewFile();
        Files.write(testFile.toPath(), expectedFileContent);
        VerifierSocket verifierSocket = new VerifierSocket();
        verifierSocket.addRequestLine("GET /test/image01.png HTTP/1.1");
        YodaRequestProcessor requestProcessor = new YodaRequestProcessor(tempFolder.getRoot(), verifierSocket, null);
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
    public void resourceRequestedWithSpecialCharsURLEncoded_usesDecodedRequestUrl() throws IOException {
        // given
        tempFolder.newFile("File with space and spéciál.pdf");
        VerifierSocket verifierSocket = new VerifierSocket();
        verifierSocket.addRequestLine("GET /File%20with%20space%20and%20sp%C3%A9ci%C3%A1l.pdf HTTP/1.1");
        YodaRequestProcessor requestProcessor = new YodaRequestProcessor(tempFolder.getRoot(), verifierSocket, null);
        // when
        requestProcessor.run();
        // then
        List<String> responseHeaderLines = verifierSocket.getHeaderLines();
        assertEquals(5, responseHeaderLines.size());
        Iterator<String> itHeaderLines = responseHeaderLines.iterator();
        assertEquals("HTTP/1.1 200 OK", itHeaderLines.next());
        assertEquals("Server: YodaServer 0.0.1", itHeaderLines.next());
        assertEquals("Content-Length: 0", itHeaderLines.next());
        assertEquals("Content-Type: application/pdf", itHeaderLines.next());
    }

    @Test
    public void nonSupportedMethod_generatesErrorResponse() {
        // given
        VerifierSocket verifierSocket = new VerifierSocket();
        verifierSocket.addRequestLine("PUT /a/file.html HTTP/1.1");
        YodaRequestProcessor requestProcessor = new YodaRequestProcessor(null, verifierSocket, null);
        // when
        requestProcessor.run();
        // then
        List<String> responseHeaderLines = verifierSocket.getHeaderLines();
        assertEquals(5, responseHeaderLines.size());
        Iterator<String> itHeaderLines = responseHeaderLines.iterator();
        assertEquals("HTTP/1.1 501 Not Implemented", itHeaderLines.next());
        assertEquals("Server: YodaServer 0.0.1", itHeaderLines.next());
        assertEquals("Content-Length: " + NOT_SUPPORTED_MESSAGE_BODY.getBytes().length, itHeaderLines.next());
        assertEquals("Content-type: text/html", itHeaderLines.next());
        assertEquals("", itHeaderLines.next());
        assertEquals(NOT_SUPPORTED_MESSAGE_BODY, verifierSocket.bodyAsString());
    }

}
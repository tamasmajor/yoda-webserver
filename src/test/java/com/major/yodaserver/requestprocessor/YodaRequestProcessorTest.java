package com.major.yodaserver.requestprocessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.major.yodaserver.common.CommonHtmlPage;
import com.major.yodaserver.common.Header;
import com.major.yodaserver.common.MimeType;
import com.major.yodaserver.helper.VerifierSocket;

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
        YodaRequestProcessor requestProcessor = YodaRequestProcessor.newInstance(tempFolder.getRoot(), verifierSocket, null);
        // when
        requestProcessor.process();
        // then
        verifierSocket.assertResponseMessageHeaderHasLines(6);
        verifierSocket.assertStatusLineEquals("HTTP/1.1 404 Not Found");
        verifierSocket.assertContainsHeader(Header.SERVER, "YodaServer 0.0.1");
        verifierSocket.assertContainsHeader(Header.CONTENT_LENGTH, CommonHtmlPage.NOT_FOUND.getHtml().getBytes().length);
        verifierSocket.assertContainsHeader(Header.CONTENT_TYPE, MimeType.HTML.getMimeType());
        verifierSocket.assertContainsHeader(Header.DATE);
        verifierSocket.assertHasEmptyTrailingLine();
        assertEquals(CommonHtmlPage.NOT_FOUND.getHtml(), verifierSocket.bodyAsString());
    }

    @Test
    public void requestedResourceExistButAboveRootInHierarchy_notFoundReturned() throws IOException {
        // given
        tempFolder.newFolder("above-root-2", "root");
        File root = new File(tempFolder.getRoot() + "/above-root-2/root");
        VerifierSocket verifierSocket = new VerifierSocket();
        verifierSocket.addRequestLine("GET /../ HTTP/1.1");
        YodaRequestProcessor requestProcessor = YodaRequestProcessor.newInstance(root, verifierSocket, null);
        // when
        requestProcessor.process();
        // then
        verifierSocket.assertResponseMessageHeaderHasLines(6);
        verifierSocket.assertStatusLineEquals("HTTP/1.1 404 Not Found");
        verifierSocket.assertContainsHeader(Header.SERVER, "YodaServer 0.0.1");
        verifierSocket.assertContainsHeader(Header.CONTENT_LENGTH, CommonHtmlPage.NOT_FOUND.getHtml().getBytes().length);
        verifierSocket.assertContainsHeader(Header.CONTENT_TYPE, MimeType.HTML.getMimeType());
        verifierSocket.assertContainsHeader(Header.DATE);
        verifierSocket.assertHasEmptyTrailingLine();
        assertEquals(CommonHtmlPage.NOT_FOUND.getHtml(), verifierSocket.bodyAsString());
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
        YodaRequestProcessor requestProcessor = YodaRequestProcessor.newInstance(tempFolder.getRoot(), verifierSocket, null);
        // when
        requestProcessor.process();
        // then
        verifierSocket.assertResponseMessageHeaderHasLines(6);
        verifierSocket.assertStatusLineEquals("HTTP/1.1 200 OK");
        verifierSocket.assertContainsHeader(Header.SERVER, "YodaServer 0.0.1");
        verifierSocket.assertContainsHeader(Header.CONTENT_LENGTH, expectedFileContent.length);
        verifierSocket.assertContainsHeader(Header.CONTENT_TYPE, "image/png");
        verifierSocket.assertContainsHeader(Header.DATE);
        verifierSocket.assertHasEmptyTrailingLine();
        assertArrayEquals(expectedFileContent, verifierSocket.bodyAsBytes());
    }

    @Test
    public void resourceRequestedWithSpecialCharsURLEncoded_usesDecodedRequestUrl() throws IOException {
        // given
        tempFolder.newFile("File with space and spéciál.pdf");
        VerifierSocket verifierSocket = new VerifierSocket();
        verifierSocket.addRequestLine("GET /File%20with%20space%20and%20sp%C3%A9ci%C3%A1l.pdf HTTP/1.1");
        YodaRequestProcessor requestProcessor = YodaRequestProcessor.newInstance(tempFolder.getRoot(), verifierSocket, null);
        // when
        requestProcessor.process();
        // then
        verifierSocket.assertResponseMessageHeaderHasLines(6);
        verifierSocket.assertStatusLineEquals("HTTP/1.1 200 OK");
        verifierSocket.assertContainsHeader(Header.SERVER, "YodaServer 0.0.1");
        verifierSocket.assertContainsHeader(Header.CONTENT_LENGTH, 0);
        verifierSocket.assertContainsHeader(Header.CONTENT_TYPE, "application/pdf");
        verifierSocket.assertContainsHeader(Header.DATE);
        verifierSocket.assertHasEmptyTrailingLine();
    }

    @Test
    public void nonSupportedMethod_generatesErrorResponse() {
        // given
        VerifierSocket verifierSocket = new VerifierSocket();
        verifierSocket.addRequestLine("PUT /a/file.html HTTP/1.1");
        YodaRequestProcessor requestProcessor = YodaRequestProcessor.newInstance(null, verifierSocket, null);
        // when
        requestProcessor.process();
        // then
        verifierSocket.assertResponseMessageHeaderHasLines(6);
        verifierSocket.assertStatusLineEquals("HTTP/1.1 501 Not Implemented");
        verifierSocket.assertContainsHeader(Header.SERVER, "YodaServer 0.0.1");
        verifierSocket.assertContainsHeader(Header.CONTENT_LENGTH, CommonHtmlPage.NOT_IMPLEMENTED.getHtml().getBytes().length);
        verifierSocket.assertContainsHeader(Header.CONTENT_TYPE, MimeType.HTML.getMimeType());
        verifierSocket.assertContainsHeader(Header.DATE);
        verifierSocket.assertHasEmptyTrailingLine();
        assertEquals(CommonHtmlPage.NOT_IMPLEMENTED.getHtml(), verifierSocket.bodyAsString());
    }

}
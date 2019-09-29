package com.major.yodaserver.requestprocessor;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.major.yodaserver.helper.VerifierSocket;

import static org.junit.Assert.assertEquals;

public class YodaRequestProcessorTest {

    @Test
    public void supportedMethod_acknowledges() {
        // given
        VerifierSocket verifierSocket = new VerifierSocket();
        verifierSocket.addRequestLine("GET /a/file.html HTTP/1.1");
        YodaRequestProcessor requestProcessor = new YodaRequestProcessor(null, verifierSocket);
        // when
        requestProcessor.run();
        // then
        List<String> responseLines = verifierSocket.getResponseLines();
        assertEquals(4, responseLines.size());
        Iterator<String> responseIterator = responseLines.iterator();
        assertEquals("HTTP/1.1 200 OK\r\n", responseIterator.next());
        assertEquals("Server: YodaServer 0.0.1\r\n", responseIterator.next());
        assertEquals("Content-Length: 0\r\n", responseIterator.next());
        assertEquals("\r\n", responseIterator.next());
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
        List<String> responseLines = verifierSocket.getResponseLines();
        assertEquals(6, responseLines.size());
        Iterator<String> responseIterator = responseLines.iterator();
        assertEquals("HTTP/1.1 501 Not Implemented\r\n", responseIterator.next());
        assertEquals("Server: YodaServer 0.0.1\r\n", responseIterator.next());
        assertEquals("Content-Length: 94\r\n", responseIterator.next());
        assertEquals("Content-type: text/html\r\n", responseIterator.next());
        assertEquals("\r\n", responseIterator.next());
        assertEquals("<HTML><HEAD><TITLE>Not yet supported</TITLE></HEAD><BODY>501 - Not yet supported</BODY></HTML>", responseIterator.next());
    }

}
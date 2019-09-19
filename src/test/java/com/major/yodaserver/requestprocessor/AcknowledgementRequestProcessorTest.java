package com.major.yodaserver.requestprocessor;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.major.yodaserver.helper.VerifierSocket;

import static org.junit.Assert.assertEquals;

public class AcknowledgementRequestProcessorTest {

    @Test
    public void acknowledges() {
        // given
        VerifierSocket verifierSocket = new VerifierSocket();
        AcknowledgementRequestProcessor acknowledgementProcessor = new AcknowledgementRequestProcessor(null, verifierSocket);
        // when
        acknowledgementProcessor.run();
        // then
        List<String> responseLines = verifierSocket.getWrittenLines();
        assertEquals(4, responseLines.size());
        Iterator<String> responseIterator = responseLines.iterator();
        assertEquals("HTTP/1.1 200 OK\r\n", responseIterator.next());
        assertEquals("Server: YodaServer 0.0.1\r\n", responseIterator.next());
        assertEquals("Content-Length: 0\r\n", responseIterator.next());
        assertEquals("\r\n", responseIterator.next());
    }


}
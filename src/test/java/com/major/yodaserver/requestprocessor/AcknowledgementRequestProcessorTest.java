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
        List<String> responseHeaderLines = verifierSocket.getHeaderLines();
        assertEquals(4, responseHeaderLines.size());
        Iterator<String> itResponseLine = responseHeaderLines.iterator();
        assertEquals("HTTP/1.1 200 OK", itResponseLine.next());
        assertEquals("Server: YodaServer 0.0.1", itResponseLine.next());
        assertEquals("Content-Length: 0", itResponseLine.next());
        assertEquals("", itResponseLine.next());
    }


}
package com.major.yodaserver.requestprocessor;

import org.junit.Test;

import com.major.yodaserver.common.Header;
import com.major.yodaserver.helper.VerifierSocket;

public class AcknowledgementRequestProcessorTest {

    @Test
    public void acknowledges() {
        // given
        VerifierSocket verifierSocket = new VerifierSocket();
        AcknowledgementRequestProcessor acknowledgementProcessor = AcknowledgementRequestProcessor.newInstance(verifierSocket);
        // when
        acknowledgementProcessor.process();
        // then
        verifierSocket.assertResponseMessageHeaderHasLines(5);
        verifierSocket.assertStatusLineEquals("HTTP/1.1 200 OK");
        verifierSocket.assertContainsHeader(Header.SERVER, "YodaServer 0.0.1");
        verifierSocket.assertContainsHeader(Header.CONTENT_LENGTH, "0");
        verifierSocket.assertContainsHeader(Header.DATE);
        verifierSocket.assertHasEmptyTrailingLine();
    }


}
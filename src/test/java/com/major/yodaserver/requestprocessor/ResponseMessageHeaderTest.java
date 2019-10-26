package com.major.yodaserver.requestprocessor;

import org.junit.Test;

import static com.major.yodaserver.requestprocessor.ResponseMessageHeader.LINE_TERMINATOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResponseMessageHeaderTest {

    @Test
    public void asHttpResponse_addsStatusLine() {
        // given
        StatusCode status = StatusCode.OK;
        ResponseMessageHeader responseMessage = new ResponseMessageHeader.Builder(status).build();
        // when
        String httpResponse = responseMessage.asHttpResponse();
        // then
        assertEquals(String.join(" ", "HTTP/1.1", String.valueOf(status.getStatusCode()), status.getReasonPhrase()),
                     httpResponse.split(LINE_TERMINATOR)[0]);
    }

    @Test
    public void asHttpResponse_addsHeadersClosingCRLF() {
        // given
        ResponseMessageHeader responseMessage = new ResponseMessageHeader.Builder(StatusCode.OK).build();
        // when
        String httpResponse = responseMessage.asHttpResponse();
        // then
        String expectedHeadersClosing = LINE_TERMINATOR + LINE_TERMINATOR;
        String actualClosing = httpResponse.substring(httpResponse.length() - LINE_TERMINATOR.length() * 2);
        assertEquals(expectedHeadersClosing, actualClosing);
    }

    @Test
    public void asHttpResponse_withoutAdditionalHeaders_addsServerHeader() {
        // given
        ResponseMessageHeader responseMessage = new ResponseMessageHeader.Builder(StatusCode.OK).build();
        // when
        String httpResponse = responseMessage.asHttpResponse();
        // then
        String[] responseLines = httpResponse.split(LINE_TERMINATOR);
        assertTrue(responseLines[1].contains("Server: "));
    }

    @Test
    public void asHttpResponse_withoutDateHeader_addsDateHeader() {
        // given
        ResponseMessageHeader responseMessage = new ResponseMessageHeader.Builder(StatusCode.OK).build();
        // when
        String httpResponse = responseMessage.asHttpResponse();
        // then
        String[] responseLines = httpResponse.split(LINE_TERMINATOR);
        assertTrue(responseLines[2].contains("Date: "));
    }

    @Test
    public void asHttpResponse_withDateHeader_doesNotOverride() {
        // given
        ResponseMessageHeader responseMessage = new ResponseMessageHeader.Builder(StatusCode.OK)
                                                        .addHeader("Date", "Sat Oct 26 10:11:12 CEST 2019")
                                                        .build();
        // when
        String httpResponse = responseMessage.asHttpResponse();
        // then
        String[] responseLines = httpResponse.split(LINE_TERMINATOR);
        assertEquals(responseLines[2], "Date: Sat Oct 26 10:11:12 CEST 2019");
    }


}
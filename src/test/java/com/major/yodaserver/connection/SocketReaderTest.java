package com.major.yodaserver.connection;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.major.yodaserver.helper.VerifierSocket;

import static org.junit.Assert.assertEquals;

public class SocketReaderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getRequestMethod_requestLineWitGet_returnsGet() throws IOException {
        // given
        VerifierSocket socket = new VerifierSocket();
        socket.addRequestLine("GET /hello.png HTTP/1.1");
        // when
        SocketReader socketReader = new SocketReader(socket);
        // then
        assertEquals("GET", socketReader.getRequestMethod());
    }

    @Test
    public void getRequestMethod_requestLineWithPost_returnsPost() throws IOException {
        // given
        VerifierSocket socket = new VerifierSocket();
        socket.addRequestLine("POST /hello.png HTTP/1.1");
        // when
        SocketReader socketReader = new SocketReader(socket);
        // then
        assertEquals("POST", socketReader.getRequestMethod());
    }

    @Test
    public void getRequestUri_requestLineWithSimpleUri_returnsRequestUri() throws IOException {
        // given
        VerifierSocket socket = new VerifierSocket();
        socket.addRequestLine("GET /hello.png HTTP/1.1");
        // when
        SocketReader socketReader = new SocketReader(socket);
        // then
        assertEquals("/hello.png", socketReader.getRequestUri());
    }

    @Test
    public void getRequestUri_requestLineWithEncodedWhitespaceUri_returnsRequestUri() throws IOException {
        // given
        VerifierSocket socket = new VerifierSocket();
        socket.addRequestLine("GET /hello%20world.html HTTP/1.1");
        // when
        SocketReader socketReader = new SocketReader(socket);
        // then
        assertEquals("/hello%20world.html", socketReader.getRequestUri());
    }

    @Test
    public void getHttpVersion_requestLineWithHttpVersion_returnsHttpVersion() throws IOException {
        // given
        VerifierSocket socket = new VerifierSocket();
        socket.addRequestLine("GET /hello%20world.html HTTP/1.1");
        // when
        SocketReader socketReader = new SocketReader(socket);
        // then
        assertEquals("HTTP/1.1", socketReader.getHttpVersion());
    }
    
    @Test
    public void getRequestLineInformationMultipleTimes_returnsRequestedInformation() throws IOException {
        // given
        VerifierSocket socket = new VerifierSocket();
        socket.addRequestLine("GET /hello.png HTTP/1.1");
        // when
        SocketReader socketReader = new SocketReader(socket);
        // then
        assertEquals("/hello.png", socketReader.getRequestUri());
        assertEquals("/hello.png", socketReader.getRequestUri());
        assertEquals("/hello.png", socketReader.getRequestUri());
    }
    
    @Test
    public void requestLineHasInvalidNumberOfTokens_thrownException() throws IOException {
        // expected exception
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Request-Line contains invalid number of tokens");
        // given
        VerifierSocket socket = new VerifierSocket();
        socket.addRequestLine("GET /hello.png");
        // when
        SocketReader socketReader = new SocketReader(socket);
        socketReader.getRequestMethod();
        // throws expected exception
    }
}
package com.major.yodaserver.connection;

import java.io.IOException;

import org.junit.Test;

import com.major.yodaserver.helper.VerifierSocket;

import static org.junit.Assert.assertEquals;

public class SocketReaderTest {

    @Test
    public void getMethod_requestLineWitGet_returnsGet() throws IOException {
        // given
        VerifierSocket socket = new VerifierSocket();
        socket.addRequestLine("GET /hello.png HTTP/1.1");
        // when
        SocketReader socketReader = new SocketReader(socket);
        // then
        assertEquals("GET", socketReader.getMethod());
    }

    @Test
    public void getMethod_requestLineWithPost_returnsPost() throws IOException {
        // given
        VerifierSocket socket = new VerifierSocket();
        socket.addRequestLine("POST /hello.png HTTP/1.1");
        // when
        SocketReader socketReader = new SocketReader(socket);
        // then
        assertEquals("POST", socketReader.getMethod());
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
}
package com.major.yodaserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.major.yodaserver.interrupter.ServerInterrupter;
import com.major.yodaserver.requestprocessor.factory.RequestProcessorFactory;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class YodaServerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ServerSocketFactory serverSocketFactory;

    @Mock
    private ServerSocket serverSocket;

    @Mock
    private RequestProcessorFactory requestProcessorFactory;

    @Mock
    private ServerInterrupter interrupter;

    private YodaServer yodaServer;

    @Before
    public void setUp() throws Exception {
        yodaServer = mockedYoda();
        when(serverSocketFactory.createServerSocket(anyInt())).thenReturn(serverSocket);
    }

    @Test
    public void yodaServer_creationWithInvalidPortLowerBound_throwsIllegalArgumentException() {
        int testedPort = YodaServer.LOWEST_AVAILABLE_PORT - 1;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Port '" + testedPort + "' is not valid");
        new YodaServer(new ServerSettings(serverSocketFactory, requestProcessorFactory, interrupter), testedPort);
    }

    @Test
    public void yodaServer_creationWithInvalidPortUpperBound_throwsIllegalArgumentException() {
        int testedPort = YodaServer.HIGHEST_AVAILABLE_PORT + 1;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Port '" + testedPort + "' is not valid");
        new YodaServer(new ServerSettings(serverSocketFactory, requestProcessorFactory, interrupter), testedPort);
    }

    @Test
    public void listen_interruptedBeforeFirstRequest_doesNotAcceptAnyRequestBeforeShutdown() throws IOException {
        // given
        when(interrupter.actived()).thenReturn(true);
        // when
        yodaServer.listen();
        // then
        verify(interrupter, times(1)).actived();
        verify(serverSocket, times(0)).accept();
    }

    @Test
    public void listen_interruptedOnlyAfterFirstRequest_callsAcceptOneRequestBeforeShutdown() throws IOException {
        // given
        when(interrupter.actived()).thenReturn(false, true);
        when(serverSocket.accept()).thenReturn(new Socket());
        // when
        yodaServer.listen();
        // then
        verify(interrupter, times(2)).actived();
        verify(serverSocket, times(1)).accept();
    }
    
    @Test
    public void listen_couldNotBoundServerSocketToPort_throwsRuntimeException() throws IOException {
        // expected exception
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Server startup failure");
        // given
        when(serverSocketFactory.createServerSocket(anyInt())).thenThrow(new IOException("Something wrong happened"));
        // when
        yodaServer.listen();
        // then - throws expected exception
    }

    private YodaServer mockedYoda() {
        return new YodaServer(new ServerSettings(serverSocketFactory, requestProcessorFactory, interrupter));
    }

}
package com.major.yodaserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.major.yodaserver.interrupter.ServerInterrupter;
import com.major.yodaserver.requestprocessor.RequestProcessor;
import com.major.yodaserver.requestprocessor.factory.RequestProcessorFactory;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class YodaServerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

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
        new YodaServer(new ServerSettings(serverSocketFactory, requestProcessorFactory, interrupter),
                       new ServerContext(null, testedPort));
    }

    @Test
    public void yodaServer_creationWithInvalidPortUpperBound_throwsIllegalArgumentException() {
        int testedPort = YodaServer.HIGHEST_AVAILABLE_PORT + 1;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Port '" + testedPort + "' is not valid");
        new YodaServer(new ServerSettings(serverSocketFactory, requestProcessorFactory, interrupter),
                       new ServerContext(null, testedPort));
    }
    
    @Test
    public void yodaServer_creationWithFileAsRootDir_throwIllegalArgumentException() throws IOException {
        // expected exception
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Server root must be a directory");
        // given
        File file = tempFolder.newFile("aFile.txt");
        // when
        new YodaServer(new ServerSettings(serverSocketFactory, requestProcessorFactory, interrupter),
                       new ServerContext(file, 8080));
    }

    @Test
    public void yodaServer_creationWithNonExistingRootDir_throwIllegalArgumentException() throws IOException {
        // expected exception
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Server root must exist");
        // given
        File root = new File(tempFolder.getRoot().getCanonicalPath() + "/missing");
        // when
        new YodaServer(new ServerSettings(serverSocketFactory, requestProcessorFactory, interrupter),
                       new ServerContext(root, 8080));
    }

    @Test
    public void listen_interruptedBeforeFirstRequest_doesNotAcceptAnyRequestBeforeShutdown() throws IOException {
        // given
        when(interrupter.activated()).thenReturn(true);
        // when
        yodaServer.listen();
        // then
        verify(interrupter, times(1)).activated();
        verify(serverSocket, times(0)).accept();
    }

    @Test
    public void listen_interruptedOnlyAfterFirstRequest_callsAcceptOneRequestBeforeShutdown() throws IOException {
        // given
        when(interrupter.activated()).thenReturn(false, true);
        when(serverSocket.accept()).thenReturn(new Socket());
        when(requestProcessorFactory.createRequestProcessor(any(), any())).thenReturn(aDoNothingProcessor());
        // when
        yodaServer.listen();
        // then
        verify(interrupter, times(2)).activated();
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

    @Test
    public void listen_twoIncomingRequest_spawnNewProcessorForBoth() throws IOException {
        // given
        ArgumentCaptor<Socket> socketCaptor = ArgumentCaptor.forClass(Socket.class);
        when(interrupter.activated()).thenReturn(false, false, true);
        Socket connectionA = new Socket();
        Socket connectionB = new Socket();
        when(serverSocket.accept()).thenReturn(connectionA, connectionB);
        when(requestProcessorFactory.createRequestProcessor(any(), any())).thenReturn(aDoNothingProcessor());
        // when
        yodaServer.listen();
        // then
        verify(requestProcessorFactory, times(2)).createRequestProcessor(any(), socketCaptor.capture());
        assertSame(connectionA, socketCaptor.getAllValues().get(0));
        assertSame(connectionB, socketCaptor.getAllValues().get(1));
        verify(serverSocket, times(2)).accept();
        verify(interrupter, times(3)).activated();
    }

    private YodaServer mockedYoda() {
        return new YodaServer(new ServerSettings(serverSocketFactory, requestProcessorFactory, interrupter),
                              new ServerContext(tempFolder.getRoot(), 8080));
    }

    private RequestProcessor aDoNothingProcessor() {
        return () -> {};
    }

}
package com.major.yodaserver;

import java.net.ServerSocket;
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
import static org.mockito.Mockito.when;

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

    @Before
    public void setUp() throws Exception {
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

}
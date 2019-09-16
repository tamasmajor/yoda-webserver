package com.major.yodaserver;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class YodaServerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void yodaServer_creationWithInvalidPortLowerBound_throwsIllegalArgumentException() {
        int testedPort = YodaServer.LOWEST_AVAILABLE_PORT - 1;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Port '" + testedPort + "' is not valid");
        new YodaServer(testedPort);
    }

    @Test
    public void yodaServer_creationWithInvalidPortUpperBound_throwsIllegalArgumentException() {
        int testedPort = YodaServer.HIGHEST_AVAILABLE_PORT + 1;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Port '" + testedPort + "' is not valid");
        new YodaServer(testedPort);
    }

}
package com.openclassrooms.starterjwt;

import org.mockserver.integration.ClientAndServer;

public class MockServerConfig {

    private static ClientAndServer mockServer;

    public static void startMockServer(int port) {
        mockServer = ClientAndServer.startClientAndServer(port);
        System.out.println("MockServer started on port: " + port);
    }

    public static ClientAndServer getMockServer() {
        return mockServer;
    }

    public static void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }
}
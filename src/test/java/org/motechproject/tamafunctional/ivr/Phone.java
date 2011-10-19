package org.motechproject.tamafunctional.ivr;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import static junit.framework.Assert.fail;

public class Phone implements HttpHandler {
    private HttpServer httpServer;
    private final int portNumber;
    private boolean called;

    public Phone(int portNumber) {
        this.portNumber = portNumber;
    }

    public void switchOn() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(portNumber), 10);
        httpServer.createContext("/outbound", this);
        httpServer.setExecutor(null);
        httpServer.start();
    }

    public void switchOff() {
        httpServer.stop(0);
    }

    public synchronized void assertIfCalled(long timeoutInSeconds) throws InterruptedException {
        this.wait(timeoutInSeconds * 1000);
        if (!called) {
            fail("Expected to be called, but was not.");
        }
    }

    @Override
    public synchronized void handle(HttpExchange httpExchange) throws IOException {
        called = true;
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.getResponseBody().close();
        this.notify();
    }
}

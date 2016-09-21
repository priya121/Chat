package fakes;

import interfaces.ServerSocketConnection;
import interfaces.SocketConnection;

public class FakeServerSocket implements ServerSocketConnection {
    private final SocketConnection socket;

    public FakeServerSocket(SocketConnection socket) {
        this.socket = socket;
    }

    @Override
    public SocketConnection accept() {
        return socket;
    }

    @Override
    public void close() {
        socket.close();
    }
}

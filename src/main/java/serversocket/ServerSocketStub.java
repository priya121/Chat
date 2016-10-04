package serversocket;

import socket.SocketConnection;

public class ServerSocketStub implements ServerSocketConnection {
    private final SocketConnection socket;

    public ServerSocketStub(SocketConnection socket) {
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

package serversocket;

import socket.SocketConnection;

public interface ServerSocketConnection {
    SocketConnection accept();
    void close();
}

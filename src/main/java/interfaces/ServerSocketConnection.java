package interfaces;

public interface ServerSocketConnection {
    SocketConnection accept();
    void close();
}

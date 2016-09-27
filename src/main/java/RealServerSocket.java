import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;

public class RealServerSocket implements ServerSocketConnection {
    private ServerSocket serverSocket;

    public RealServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public SocketConnection accept () {
        try {
            return new RealSocket(serverSocket.accept());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

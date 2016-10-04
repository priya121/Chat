import serversocket.RealServerSocket;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;

public class ServerApp implements App {
    private final UserIO console;

    public ServerApp (UserIO console) {
        this.console = console;
    }

    public void start() {
        try {
            RealServerSocket serverSocket = new RealServerSocket(new ServerSocket(4444));
            ChatServer server = new ChatServer(console, serverSocket);
            server.start();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

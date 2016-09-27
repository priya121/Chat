import java.io.IOException;
import java.net.ServerSocket;

public class ServerApp {
    private final UserIO console;

    public ServerApp (UserIO console) {
        this.console = console;
    }

    public void create() throws IOException {
        RealServerSocket serverSocket = new RealServerSocket(new ServerSocket(4444));
        ChatServer serverThread = new ChatServer(console, serverSocket);
        serverThread.start();
    }
}

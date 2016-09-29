import java.io.IOException;
import java.net.Socket;

public class ClientApp {
    private final Exit exit;
    private final UserIO console;

    public ClientApp(UserIO console) {
        this.console = console;
        this.exit = new Exit();
    }

    public void create() throws IOException {
        Time clock = new Clock();
        SocketConnection socket = new RealSocket(new Socket("10.0.0.32.", 4444));
        SocketConnection socketConnection = new SocketCreator(console, socket).create(exit);
        ChatClient client = new ChatClient(console, socketConnection, clock);
        client.writeOutToAndReadInFromClient();
    }
}

import interfaces.SocketConnection;

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
        SocketConnection socket = new RealSocket(new Socket("192.168.0.3.", 4444));
        SocketConnection socketConnection = new SocketCreator(console, socket).create(exit);
        ChatClient client = new ChatClient(console, socketConnection);
        client.writeOutToAndReadInFromClient();
    }
}

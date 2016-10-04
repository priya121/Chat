import clock.Clock;
import clock.Time;
import exit.Exit;
import socket.RealSocket;
import socket.SocketConnection;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;

public class ClientApp implements App {
    private final Exit exit;
    private final UserIO console;
    private Time clock;

    public ClientApp(UserIO console) {
        this.console = console;
        this.exit = new Exit();
        this.clock = new Clock();
    }

    public ClientApp(UserIO console, Time clock) {
        this.console = console;
        this.clock = clock;
        this.exit = new Exit();
    }

    public void start() {
        try {
            SocketConnection socket = createSocket();
            ChatClient client = new ChatClient(console, socket, clock);
            client.writeOutToAndReadInFromServer();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private SocketConnection createSocket() throws IOException {
        SocketConnection socket = new RealSocket(new Socket("10.0.0.32.", 4444));
        return new SocketCreator(console, socket).create(exit);
    }
}

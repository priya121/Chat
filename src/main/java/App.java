import interfaces.SocketConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    public static void main(String[] args) throws IOException {
        UserIO console = new UserIO(System.in, System.out);

        if (args[0].equals("in")) {
            Socket socket = new Socket("localhost", 4444);
            SocketConnection socketConnection = new RealSocket(socket);
            ChatClient client = new ChatClient(console, socketConnection);
            client.writeToServer();
        } else if (args[0].equals("out")) {
            RealServerSocket serverSocket = new RealServerSocket(new ServerSocket(4444));
            ChatServer serverThread = new ChatServer(serverSocket, console);
            serverThread.readInFromClient();
        }
    }
}

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    public static void main(String[] args) throws IOException {
        UserIO console = new UserIO(System.in, System.out);
        if (args[0].equals("in")) {
            Socket socket = new Socket(InetAddress.getByName("localhost"), 4444);
            ChatClient client = new ChatClient(console, socket);
            client.writeToServer();
        } else if (args[0].equals("out")) {
            ServerSocket serverSocket = new ServerSocket(4444);
            ChatServer server = new ChatServer(serverSocket, console);
            server.readInFromClient();
        }
    }
}

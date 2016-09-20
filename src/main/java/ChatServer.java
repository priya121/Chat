import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private ServerSocket serverSocket;
    private UserIO io;

    public ChatServer(ServerSocket serverSocket, UserIO io) {
        this.serverSocket = serverSocket;
        this.io = io;
    }

    public Socket makeConnection(ServerSocket serverSocket) {
        try {
            Socket socket = serverSocket.accept();
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void readInFromClient() {
        try {
            Socket server = makeConnection(serverSocket);
            InputStream inputStream = server.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            readTillNoNameGiven(reader, server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeSocket();
    }

    private void readTillNoNameGiven(BufferedReader reader, Socket server) throws IOException {
        String name = reader.readLine();
        while (name != null) {
            io.showOutput(name + " has now joined the chat room");
            writeOutToClient(name , server);
            name = reader.readLine();
        }
        io.showOutput("Bye!");
    }

    private void writeOutToClient(String message, Socket socket) {
        try {
            OutputStream outToServer = socket.getOutputStream();
            OutputStreamWriter outputWriter = new OutputStreamWriter(outToServer);
            outputWriter.write("Welcome " + message);
            outputWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSocket() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

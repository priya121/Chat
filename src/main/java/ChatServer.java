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

    private Socket makeConnection(ServerSocket serverSocket) {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            io.showOutput("Error in connecting socket");
            throw new UncheckedIOException(e);
        }
    }

    public void readInFromClient() {
        try {
            Socket server = makeConnection(serverSocket);
            BufferedReader reader = createBufferedReader(server);
            readInputTillOver(reader, server);
            io.showExitMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeSocket();
    }

    private void readInputTillOver(BufferedReader reader, Socket server) {
        try {
            String name = reader.readLine();
            while (name != null) {
                io.showOutput(name + " has now joined the chat room");
                writeOutToClient(name , server);
                name = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedReader createBufferedReader(Socket server) throws IOException {
        InputStream inputStream = server.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new BufferedReader(inputStreamReader);
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

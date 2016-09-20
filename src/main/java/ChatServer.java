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

    public void readInFromClient() {
        Socket server = makeConnection(serverSocket);
        BufferedReader reader = createBufferedReader(server);
        readInputTillOver(reader, server);
        io.showExitMessage();
        closeSocket();
    }

    private Socket makeConnection(ServerSocket serverSocket) {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private BufferedReader createBufferedReader(Socket server) {
        try {
            InputStream inputStream = server.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            return new BufferedReader(inputStreamReader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void readInputTillOver(BufferedReader reader, Socket server) {
        try {
            String name = reader.readLine();
            while (name != null) {
                io.userJoinedMessage(name);
                writeOutToClient(name , server);
                name = reader.readLine();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeOutToClient(String message, Socket socket) {
        try {
            OutputStream outToServer = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outToServer, true);
            printWriter.println(message);
            printWriter.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void closeSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

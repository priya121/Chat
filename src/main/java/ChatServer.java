import interfaces.ServerSocketConnection;
import interfaces.SocketConnection;

import java.io.*;

public class ChatServer {
    private ServerSocketConnection serverSocket;
    private UserIO io;

    public ChatServer(ServerSocketConnection serverSocket, UserIO io) {
        this.serverSocket = serverSocket;
        this.io = io;
    }

    public void readInFromClient() {
            SocketConnection server = serverSocket.accept();
            BufferedReader reader = createBufferedReader(server);
            readInputTillOver(reader, server);
            io.showExitMessage();
            closeSocket();
    }

    private BufferedReader createBufferedReader(SocketConnection server) {
            InputStream inputStream = server.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            return new BufferedReader(inputStreamReader);
    }

    private void readInputTillOver(BufferedReader reader, SocketConnection server) {
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

    private void writeOutToClient(String message, SocketConnection socket) {
            OutputStream outToServer = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outToServer, true);
            printWriter.println(message);
            printWriter.flush();
    }

    private void closeSocket() {
        serverSocket.close();
    }
}

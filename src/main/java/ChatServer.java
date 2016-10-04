import protocols.*;
import serversocket.ServerSocketConnection;
import socket.SocketConnection;
import streamwriter.RealPrintWriter;
import streamwriter.StreamWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private final ServerSocketConnection serverSocket;
    private final UserIO console;
    private final List<User> users;
    boolean SERVER_LISTENING = true;
    private final int WELCOME_PROTOCOL = 1;
    private final int CHAT_PROTOCOL = 2;
    private final int EXIT_PROTOCOL = 3;

    public ChatServer(UserIO io, ServerSocketConnection serverSocket) {
        this.serverSocket = serverSocket;
        this.console = io;
        this.users = new ArrayList<>();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void start() {
        while (SERVER_LISTENING) {
            SocketConnection client = serverSocket.accept();
            readInFromClient(client);
            client.close();
        }
    }

    public void readInFromClient(SocketConnection client) {
        BufferedReader reader = createBufferedReader(client);
        StreamWriter writer = createPrintWriter(client);
        try {
            actOnProtocol(client, writer, reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void actOnProtocol(SocketConnection client, StreamWriter writer, BufferedReader reader) throws IOException {
        Integer protocolNumber = Integer.valueOf(getMessageFromUser(reader));
        String protocolMessage = getMessageFromUser(reader);
        if (protocolNumber != EXIT_PROTOCOL) {
            String response = getResponse(protocolNumber, protocolMessage);
            sendAndDisplay(writer, response);
            readInFromClient(client);
        } else {
            String exitMessage = new ExitProtocol(protocolMessage).action();
            sendAndDisplay(writer, exitMessage);
        }
    }

    private String getMessageFromUser(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private String getResponse(Integer protocolNumber, String protocolMessage) {
        Protocol protocol = process(protocolNumber, protocolMessage);
        return protocol.action();
    }

    public Protocol process(int requestNumber, String message) {
        switch (requestNumber) {
            case WELCOME_PROTOCOL:
                return new WelcomeProtocol(message, users);
            case CHAT_PROTOCOL:
                return new ChatProtocol(message);
            case EXIT_PROTOCOL:
                return new ExitProtocol(message);
            default:
                return new ExitProtocol(message);
        }
    }

    public String determineAction(Protocol protocol) {
        return protocol.action();
    }

    public int numberOfClients() {
      return users.size();
    }

    private void sendAndDisplay(StreamWriter writer, String result) {
        writer.println(result);
        console.showOutput(result);
    }

    private BufferedReader createBufferedReader(SocketConnection client) {
        InputStream inputStream = client.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new BufferedReader(inputStreamReader);
    }

    private StreamWriter createPrintWriter(SocketConnection client) {
        OutputStream outToServer = client.getOutputStream();
        return new RealPrintWriter(outToServer);
    }
}


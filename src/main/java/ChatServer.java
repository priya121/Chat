import protocols.*;
import serversocket.ServerSocketConnection;
import socket.SocketConnection;
import streamwriter.RealPrintWriter;
import streamwriter.StreamWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private final ServerSocketConnection serverSocket;
    private final UserIO console;
    private final List<User> users;
    private final boolean SERVER_LISTENING = true;
    private final int WELCOME_PROTOCOL = 1;
    private final int CHAT_PROTOCOL = 2;
    private final int EXIT_PROTOCOL = 3;
    private List messageList;

    public ChatServer(UserIO io, ServerSocketConnection serverSocket) {
        this.serverSocket = serverSocket;
        this.console = io;
        this.users = new ArrayList<>();
        this.messageList = new ArrayList();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void start() {
        while (SERVER_LISTENING) {
            SocketConnection client = serverSocket.accept();
            createReader(client);
            createServerThread(client);
        }
    }

    private void createServerThread(SocketConnection client) {
        Runnable runnable = () -> readInFromClient(client);
        Executors.newSingleThreadExecutor().submit(runnable);
    }

    private void createReader(SocketConnection client) {
        Runnable runnable = () -> createReaderTask(client);
        Executors.newSingleThreadExecutor().submit(runnable);
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

    public void createReaderTask(SocketConnection client) {
        BufferedReader reader = createBufferedReader(client);
        ExecutorService service = Executors.newSingleThreadScheduledExecutor();
        ReadInTask task = new ReadInTask(reader, messageList, console);
        service.execute(task);
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


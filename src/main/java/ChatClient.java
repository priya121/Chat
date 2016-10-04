import clock.Time;
import socket.SocketConnection;
import streamwriter.RealPrintWriter;
import streamwriter.StreamWriter;

import java.io.*;

public class ChatClient {
    private final String WELCOME_REQUEST = "1";
    private final String EXIT_REQUEST = "3";
    private final String EXIT_SIGNAL = ".";
    private final SocketConnection socket;
    private final UserIO console;
    private final Time clock;

    public ChatClient(UserIO io, SocketConnection socketConnection, Time clock) {
        this.console = io;
        this.socket = socketConnection;
        this.clock = clock;
    }

    public void writeOutToAndReadInFromServer() {
        StreamWriter writer = createPrintWriter();
        console.showInitialMessage(socket.getPort());
        String name = console.getInput();
        readMessagesFromAndWriteToServer(name, writer);
        closeSocket();
    }

    public void readMessagesFromAndWriteToServer(String name, StreamWriter writer) {
        try {
            BufferedReader reader = createBufferedReader();
            if (!name.equals(EXIT_SIGNAL )) {
                sendWelcomeRequest(name, writer, reader);
                chat(name, writer, reader);
                sendExitRequest(name, writer, reader);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void chat(String name, StreamWriter writer, BufferedReader reader) {
        Chat chat = new Chat(name, console, clock);
        chat.start(writer, reader);
    }

    private void sendExitRequest(String name, StreamWriter writer, BufferedReader reader) throws IOException {
        sendToServer(EXIT_REQUEST, name, writer);
        console.showOutput(getMessageFromServer(reader));
    }

    private void sendWelcomeRequest(String name, StreamWriter writer, BufferedReader reader) throws IOException {
        sendToServer(WELCOME_REQUEST, name, writer);
        console.showOutput(getMessageFromServer(reader));
    }

    private String getMessageFromServer(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private void sendToServer(String number, String name, StreamWriter printWriter) {
        printWriter.println(number);
        printWriter.println(name);
        printWriter.flush();
    }

    private StreamWriter createPrintWriter() {
        OutputStream outToServer = socket.getOutputStream();
        return new RealPrintWriter(outToServer);
    }

    private BufferedReader createBufferedReader() {
        InputStream inputFromClient = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputFromClient);
        return new BufferedReader(inputStreamReader);
    }

    private void closeSocket() {
        socket.close();
    }

}


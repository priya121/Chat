import clock.Time;
import socket.SocketConnection;
import streamwriter.RealPrintWriter;
import streamwriter.StreamWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient {
    private final String WELCOME_REQUEST = "1";
    private final String CHAT_REQUEST = "2";
    private final String EXIT_REQUEST = "3";
    private final String EXIT_SIGNAL = ".";
    private final SocketConnection socket;
    private final UserIO console;
    private final Time clock;
    private final ConcurrentLinkedQueue queue;
    private final ArrayList<String> messages;

    public ChatClient(UserIO io, SocketConnection socketConnection, Time clock) {
        this.console = io;
        this.socket = socketConnection;
        this.clock = clock;
        this.queue = new ConcurrentLinkedQueue<>();
        this.messages = new ArrayList<>();
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
            if (!name.equals(EXIT_SIGNAL)) {
                sendWelcomeRequest(name, writer, reader);

                console.chatStartedMessage();
                String message = console.getInput();

                while (!message.equals(".")) {
                    String toSend = clock.getTimeStamp() + " - " + name + ": " + message;
                    sendToServer(CHAT_REQUEST, toSend, writer);
                    console.showOutput(getMessageFromServer(reader));
                    queue.add(message);
                    createThread(writer);
                    message = console.getInput();
                }
                sendExitRequest(name, writer, reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public ConcurrentLinkedQueue listOfMessages() {
        return queue;
    }

    public void taskRunner(StreamWriter writer) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        WriteOutTask task = new WriteOutTask(messages, writer);
        service.execute(task);
    }

    private void createThread(StreamWriter writer) {
        Runnable runnable = () -> taskRunner(writer);
        Executors.newSingleThreadExecutor().submit(runnable);
    }
}


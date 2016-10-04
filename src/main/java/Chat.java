import clock.Time;
import streamwriter.StreamWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;

public class Chat {
    private final UserIO console;
    private final String name;
    private final Time clock;
    private final String CHAT_REQUEST = "2";

    public Chat(String name, UserIO console, Time clock) {
        this.name = name;
        this.console = console;
        this.clock = clock;
    }

    public void start(StreamWriter writer, BufferedReader reader) {
        try {
            console.chatStartedMessage();
            String message = console.getInput();
            startChatLoop(name, writer, reader, message);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private  String startChatLoop(String name, StreamWriter writer, BufferedReader reader, String message) throws IOException {
        while (!message.equals(".")) {
            String toSend = clock.getTimeStamp() + " - " + name + ": " + message;
            sendToServer(CHAT_REQUEST, toSend, writer);
            console.showOutput(getMessageFromServer(reader));
            message = console.getInput();
        }
        return message;
    }

    private String getMessageFromServer(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private void sendToServer(String number, String name, StreamWriter printWriter) {
        printWriter.println(number);
        printWriter.println(name);
        printWriter.flush();
    }
}

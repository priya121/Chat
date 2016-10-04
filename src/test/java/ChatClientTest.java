import clock.TestClock;
import clock.Time;
import org.junit.Before;
import org.junit.Test;
import socket.SocketMockSpy;
import streamwriter.FakePrintStreamWriter;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class ChatClientTest {
    private final ByteArrayOutputStream recordedOutput = new ByteArrayOutputStream();
    private final PrintStream output = new PrintStream(recordedOutput);
    private SocketMockSpy socket;
    private UserIO console;
    private TestClock testClock;

    @Before
    public void setUp() throws IOException {
        console = createConsole("Hi\n.\n");
        List<String> protocol = Collections.singletonList("3");
        List<String> messages = Collections.singletonList("Priya");
        testClock = new TestClock("12:00pm");
        socket = new SocketMockSpy(protocol, messages);
    }

    @Test
    public void createsOutputStreamToSend() {
        ChatClient client = new ChatClient(console, socket, testClock);
        socket.getOutputStream = false;
        client.writeOutToAndReadInFromServer();
        assertTrue(socket.getOutputStream);
    }

    @Test
    public void createsInputStreamToRead() {
        ChatClient client = new ChatClient(console, socket, testClock);
        socket.getInputStream = false;
        client.writeOutToAndReadInFromServer();
        assertTrue(socket.getInputStream);
    }

    @Test
    public void closesSocketWhenDotEntered() {
        console = createConsole(".\n");
        ChatClient client = new ChatClient(console, socket, testClock);
        socket.closed = false;
        client.writeOutToAndReadInFromServer();
        assertTrue(socket.closed);
    }

    @Test
    public void socketNotClosedUntilEndOfApp() {
        ChatClient client = new ChatClient(console, socket, testClock);
        FakePrintStreamWriter printWriter = new FakePrintStreamWriter(socket);
        socket.closed = false;
        client.readMessagesFromAndWriteToServer("Hi", printWriter);
        assertFalse(socket.closed);
    }

    @Test
    public void readsInFromInputStream() {
        List<String> message = Arrays.asList("Welcome Erica", "Bye");
        List<String> nextMessage = Arrays.asList("Bye Erica! Erica has now left the chat.");
        socket = new SocketMockSpy(message, nextMessage);
        ChatClient client = new ChatClient(console, socket, testClock);
        client.writeOutToAndReadInFromServer();
        assertEquals(recordedOutput.toString(), "You're connected on port 4444\n" +
                                                "Enter your name to register:\n\n" +
                                                "type . to exit:\n\n" +
                                                "Welcome Erica\n" +
                                                "Chat started, type . to quit Application\n" +
                                                "Bye Erica! Erica has now left the chat.\n");
    }

    @Test
    public void writesMessagesToStreamUntilQuit() throws IOException {
        UserIO console = createConsole("Priya\nHi\nhow are you\n.\n.\n");
        ChatClient client = new ChatClient(console, socket, testClock);
        FakePrintStreamWriter printWriter = new FakePrintStreamWriter(socket);
        client.readMessagesFromAndWriteToServer((console.getInput()), printWriter);
        assertThat(printWriter.writtenToStream, containsString("Priya"));
        assertThat(printWriter.writtenToStream, containsString("Hi"));
        assertThat(printWriter.writtenToStream, containsString("how are you"));
    }

    @Test
    public void addsTimeStampToMessage() throws IOException, InterruptedException {
        Time testClock = new TestClock("12:00pm");
        ChatClient client = new ChatClient(console, socket, testClock);
        FakePrintStreamWriter writer = new FakePrintStreamWriter(socket);
        client.readMessagesFromAndWriteToServer("Nadia", writer);
        assertThat(writer.writtenToStream, containsString("12:00pm - Nadia: Hi\n"));
    }

    private UserIO createConsole(String userTypedText) {
        ByteArrayInputStream userInput = new ByteArrayInputStream(userTypedText.getBytes());
        return new UserIO(userInput, output);
    }

}


import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class ChatClientTest {
    private FakeSocketSpy socket;
    private final ByteArrayOutputStream recordedOutput = new ByteArrayOutputStream();
    private final PrintStream output = new PrintStream(recordedOutput);
    private UserIO console;

    @Before
    public void setUp() throws IOException {
        socket = new FakeSocketSpy();
        console = createConsole("Priya\n.\n.\n");
    }

    @Test
    public void getsInputStreamFromServer() {
        ChatClient client = new ChatClient(console, socket);
        socket.getInputStream = false;
        client.writeOutToAndReadInFromClient();
        assertTrue(socket.getOutputStream);
    }

    @Test
    public void createsOutputStreamToSendToServer() {
        ChatClient client = new ChatClient(console, socket);
        socket.getOutputStream = false;
        client.writeOutToAndReadInFromClient();
        assertTrue(socket.getOutputStream);
    }

    @Test
    public void closesSocketAfterUserEntersQuit() {
        ChatClient client = new ChatClient(console, socket);
        socket.closed = false;
        client.writeOutToAndReadInFromClient();
        assertTrue(socket.closed);
    }

    @Test
    public void socketNotClosedUntilStreamReadFromClient() {
        UserIO console = createConsole("Priya\n.\n.\n");
        ChatClient client = new ChatClient(console, socket);
        FakePrintStreamWriter printWriter = new FakePrintStreamWriter(socket);
        socket.closed = false;
        client.writeMessageToServerUntilQuit((console.getInput()), printWriter);
        assertFalse(socket.closed);
    }

    @Test
    public void writesInputToOutputStream() {
        UserIO console = createConsole("Priya\n.\n.\n");
        ChatClient client = new ChatClient(console, socket);
        client.writeOutToAndReadInFromClient();
        assertEquals(recordedOutput.toString(), "You're connected on port 4444\n" +
                                       "Enter your name to register:\n" +
                                       "type . to exit:\n\n" +
                                       "Welcome Priya\n" +
                                       "Chat started, type . to quit App\n" +
                                       "Bye Priya! Priya has now left the chat.\n");
    }

    @Test
    public void writesMessagesToStreamUntilQuit() throws IOException {
        Time testClock = new TestClock("10.00am");
        UserIO console = createConsole("Priya\nHi\nhow are you\n.\n.\n");
        ChatClient client = new ChatClient(console, socket, testClock);
        FakePrintStreamWriter printWriter = new FakePrintStreamWriter(socket);
        client.writeMessageToServerUntilQuit((console.getInput()), printWriter);
        assertThat(printWriter.writtenToStream, containsString("how are you"));
    }

    @Test
    public void readsMessagesInFromServer() {

    }

    private UserIO createConsole(String userTypedText) {
        ByteArrayInputStream userInput = new ByteArrayInputStream(userTypedText.getBytes());
        return new UserIO(userInput, output);
    }
}


import fakes.FakeIO;
import fakes.FakePrintStreamWriter;
import fakes.FakeServerSocket;
import fakes.FakeSocket;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChatClientTest {
    private FakeSocket socket = new FakeSocket();
    private ByteArrayOutputStream recordedOutput = new ByteArrayOutputStream();
    private PrintStream output = new PrintStream(recordedOutput);

    @Test
    public void getsInputStreamFromServer() {
        FakeIO fakeInput = new FakeIO(Arrays.asList("Priya", "quit"));
        ChatClient client = new ChatClient(fakeInput, socket);
        client.writeOutToAndReadInFromClient();
        assertTrue(socket.getOutputStream);
    }

    @Test
    public void closesSocketAfterUserEntersQuit() {
        FakeIO fakeInput = new FakeIO(Arrays.asList("Priya", "quit"));
        ChatClient client = new ChatClient(fakeInput, socket);
        socket.closed = false;
        client.writeOutToAndReadInFromClient();
        assertTrue(socket.closed);
    }

    @Test
    public void writesInputToOutputStream() {
        FakeIO fakeInput = new FakeIO(Arrays.asList("Priya", "quit"));
        ChatClient client = new ChatClient(fakeInput, socket);
        client.writeOutToAndReadInFromClient();
        assertEquals(fakeInput.output, "You're connected on port 4444\n" +
                                       "Enter your name to register:\n" +
                                       "type quit to exit\n" +
                                       "Welcome Priya\n" +
                                       "Bye!");
    }

    @Test
    public void writesMessagesToStreamUntilQuit() throws IOException {
        FakeServerSocket serverSocket = new FakeServerSocket(new FakeSocket());
        UserIO console = createConsole("Priya\nquit\n");
        ChatClient client = new ChatClient(console, socket);
        ChatServer server = new ChatServer(console, serverSocket);
        FakePrintStreamWriter printWriter = new FakePrintStreamWriter(socket);
        client.writeMessageToServerUntilQuit((console.getInput()), printWriter);
        server.readInFromAndWriteOutToClient();
        assertEquals(recordedOutput.toString(), "Welcome Priya\n" +
                                                "Priya has now joined the chat room\n" +
                                                "Bye!\n");
    }


    private UserIO createConsole(String userTypedText) {
        ByteArrayInputStream userInput = new ByteArrayInputStream(userTypedText.getBytes());
        return new UserIO(userInput, output);
    }
}


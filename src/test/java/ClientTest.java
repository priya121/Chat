import interfaces.SocketConnection;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientTest {
    FakeIO fakeInput = new FakeIO(Arrays.asList("Priya", "quit"));
    FakeSocket socket = new FakeSocket();
    private ByteArrayOutputStream recordedOutput = new ByteArrayOutputStream();
    private PrintStream output = new PrintStream(recordedOutput);

    @Test
    public void getsInputStreamFromServer() {
        ChatClient client = new ChatClient(fakeInput, socket);
        client.writeToServer();
        assertTrue(socket.getOutputStream);
    }

    @Test
    public void closesSocketAfterUserEntersQuit() {
        ChatClient client = new ChatClient(fakeInput, socket);
        socket.closed = true;
        client.writeToServer();
        assertTrue(socket.closed);
    }

    @Test
    public void writesInputToOutputStream() {
        ChatClient client = new ChatClient(fakeInput, socket);
        client.writeToServer();
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
        ChatServer server = new ChatServer(serverSocket, console);
        FakePrintWriter printWriter = new FakePrintWriter(socket);
        client.writeMessageToServerUntilQuit(fakeInput.getInput(), printWriter);
        server.readInFromClient();
        assertEquals(recordedOutput.toString(), "Welcome Priya\n" +
                                                "Welcome Priya\n" +
                                                "Priya has now joined the chat room\n" +
                                                "Bye!\n");
    }


    private UserIO createConsole(String userTypedText) {
        ByteArrayInputStream userInput = new ByteArrayInputStream(userTypedText.getBytes());
        return new UserIO(userInput, output);
    }
}

class FakeSocket implements SocketConnection {

    public boolean getOutputStream;
    public boolean getInputStream;
    public boolean closed;

    public FakeSocket() {
        this.getInputStream = false;
        this.closed = false;
    }

    @Override
    public int getPort() {
        return 4444;
    }

    @Override
    public InputStream getInputStream() {
        getInputStream = true;
        String inputFromServer = "Priya";
        return new ByteArrayInputStream(inputFromServer.getBytes());
    }

    @Override
    public OutputStream getOutputStream() {
        getOutputStream = true;
        OutputStream outputStream = new ByteArrayOutputStream() ;
        return outputStream;
    }

    @Override
    public void close() {
        closed = true;
    }
}

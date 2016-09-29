import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import static junit.framework.TestCase.assertEquals;

public class MessageListTest {
    private final ByteArrayOutputStream recordedOutput = new ByteArrayOutputStream();
    private final PrintStream output = new PrintStream(recordedOutput);
    private UserIO console;
    private RealServerSocket serverSocket;

    @Before
    public void setUp() throws IOException {
        console = createConsole("y\nPriya\nHi\n.\n.\n");
        serverSocket = new RealServerSocket(new ServerSocket(4444));
    }

    @After
    public void tearDown() {
        serverSocket.close();
    }

    @Test
    public void addsAMessageToTheMessageList() throws IOException {
        UserIO userOne = new UserIO(new ByteArrayInputStream("y\nPriya\nHi\n.\n.\n".getBytes()), output);
        List<UserIO> clients = Arrays.asList(userOne);
        ChatServer server = new ChatServer(console, serverSocket);
        createClientConnection(server, clients);
        server.exit();
        assertEquals(1, server.getMessageHistory().size());
    }

    @Test
    public void addsTwoMessagesToTheMessageList() throws IOException {
        UserIO userOne = new UserIO(new ByteArrayInputStream("y\nPriya\nHi\nHow are you\n.\n.\n".getBytes()), output);
        List<UserIO> clients = Arrays.asList(userOne);
        ChatServer server = new ChatServer(console, serverSocket);
        createClientConnection(server, clients);
        server.exit();
        assertEquals(2, server.getMessageHistory().size());
    }

    @Test
    public void createsAListOfAllTheMessagesSent() throws IOException {
        UserIO userOne = new UserIO(new ByteArrayInputStream("y\nPriya\nHi\nHow are you\n.\n.\n".getBytes()), output);
        UserIO userTwo = new UserIO(new ByteArrayInputStream(("y\nJoyce\nI'm great thanks!\nHow are you\n.\n.\n").getBytes()), output);
        List<UserIO> clients = Arrays.asList(userOne, userTwo);
        ChatServer server = new ChatServer(console, serverSocket);
        createClientConnection(server, clients);
        server.exit();
        assertEquals(4, server.getMessageHistory().size());
    }

    private void createClientConnection(ChatServer server, List clientInputs) throws IOException {
        for (Object clientInput : clientInputs) {
            createServerThread(server);
            new ClientApp((UserIO) clientInput).create();
        }
    }

    private void createServerThread(final ChatServer server) {
        Runnable runnable = () -> server.start();
        Executors.newSingleThreadExecutor().submit(runnable);
    }

    private UserIO createConsole(String userTypedText) {
        ByteArrayInputStream userInput = new ByteArrayInputStream(userTypedText.getBytes());
        return new UserIO(userInput, output);
    }
}

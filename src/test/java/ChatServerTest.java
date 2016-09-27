import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ChatServerTest {
    private FakeSocketSpy socket;
    private FakeServerSocket socketConnection;
    private ByteArrayOutputStream recordedOutput = new ByteArrayOutputStream();
    private PrintStream output = new PrintStream(recordedOutput);
    private RealSocket realSocket;
    private RealServerSocket serverSocket;
    private UserIO console;

    @Before
    public void setUp() throws IOException {
        console = createConsole("y\nPriya\n.\n.\n.\n");
        socket = new FakeSocketSpy();
        socketConnection = new FakeServerSocket(socket);
        serverSocket = new RealServerSocket(new ServerSocket(4444));
        realSocket = new RealSocket(new Socket("192.168.0.3.", 4444));
    }

    @After
    public void tearDown() {
        socketConnection.close();
        realSocket.close();
        socket.close();
        serverSocket.close();
    }

    @Test
    public void getsInputStreamFromClient() {
        ChatServer server = new ChatServer(console, socketConnection);
        socket.getInputStream = false;
        server.readInFromAndWriteOutToClient(socket);
        assertTrue(socket.getInputStream);
    }

    @Test
    public void createsOutputStreamToSendToClient() {
        ChatServer server = new ChatServer(console, socketConnection);
        socket.getOutputStream = false;
        server.readInFromAndWriteOutToClient(socket);
        assertTrue(socket.getOutputStream);
    }

    @Test
    public void addsNewUser() throws IOException {
        UserIO console = createConsole("Priya\n.\n.\n");
        ChatClient client = new ChatClient(console, realSocket);
        ChatServer server = new ChatServer(console, serverSocket);
        FakePrintStreamWriter printWriter = new FakePrintStreamWriter(realSocket);
        createServerThread(server);
        client.writeMessageToServerUntilQuit((console.getInput()), printWriter);
        server.exit();
        assertEquals(1, server.users.size());
    }

    @Test
    public void addsTwoUsersToTheList() throws IOException {
        UserIO userOne = createUser("Priya\n");
        UserIO userTwo = createUser("Joyce\n");
        List<UserIO> clients = Arrays.asList(userOne, userTwo);
        ChatServer server = new ChatServer(console, serverSocket);
        createClientConnection(server, clients);
        server.exit();
        assertEquals(2, server.users.size());
    }

    @Test
    public void doesNotAddTheSameUserTwice() throws IOException {
        UserIO userOne = createUser("Priya\n");
        UserIO userTwo = createUser("Priya\n");
        List<UserIO> clients = Arrays.asList(userOne, userTwo);
        ChatServer server = new ChatServer(console, serverSocket);
        createClientConnection(server, clients);
        server.exit();
        assertEquals(1, server.users.size());
    }
    
    @Test
    public void addsFiveUsers() throws IOException {
        UserIO userOne = createUser("Priya\n");
        UserIO userTwo = createUser("Joyce\n");
        UserIO userThree = createUser("Erica\n");
        UserIO userFour = createUser("Ben\n");
        UserIO userFive = createUser("Bob\n");
        List<UserIO> clients = Arrays.asList(userOne, userTwo, userThree, userFour, userFive);
        ChatServer server = new ChatServer(console, serverSocket);
        createClientConnection(server, clients);
        server.exit();
        assertEquals(5, server.users.size());
    }

    private void createClientConnection(ChatServer server, List clientInputs) throws IOException {
        for (Object clientInput : clientInputs) {
            createServerThread(server);
            new ClientApp((UserIO) clientInput).create();
        }
    }

    private UserIO createConsole(String userTypedText) {
        ByteArrayInputStream userInput = new ByteArrayInputStream(userTypedText.getBytes());
        return new UserIO(userInput, output);
    }

    private UserIO createUser(String name) {
        UserIO console = createConsole("y\n"+name+".\n.\n");
        return console;
    }

    private void createServerThread(final ChatServer server) {
        Runnable runnable = new Runnable() {

            public void run() {
                server.start();
            }
        };
        Executors.newSingleThreadExecutor().submit(runnable);
    }
}

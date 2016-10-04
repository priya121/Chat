import clock.TestClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import serversocket.RealServerSocket;
import serversocket.ServerSocketConnection;
import socket.SocketConnection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class ChatTest {
    private ServerSocketConnection serverSocket;
    private SocketConnection socketConnection;
    private Socket socket;
    private final ByteArrayOutputStream recordedOutput = new ByteArrayOutputStream();
    private final PrintStream output = new PrintStream(recordedOutput);
    private TestClock testClock;

    @Before
    public void setUp() throws IOException {
        serverSocket = new RealServerSocket(new ServerSocket(4444));
        testClock = new TestClock("10.10am");
    }

    @After
    public void tearDown() throws IOException {
        serverSocket.close();
    }

    @Test
    public void displaysAllMessagesToClient() throws IOException {
        ByteArrayInputStream userInput = new ByteArrayInputStream("y\nBob\nI'm good thanks\nhow are you?\n.\n".getBytes());
        UserIO console = new UserIO(userInput, output);
        ChatServer server = new ChatServer(console, serverSocket);

        startChat(console, server);

        assertThat(recordedOutput.toString(), containsString("Type y to start chat or n to exit:\n" +
                                                             "You're connected on port 4444\n" +
                                                             "Enter your name to register:\n" +
                                                             "\n" +
                                                             "type . to exit:\n" +
                                                             "\n" +
                                                             "Welcome Bob!\n" +
                                                             "\n" +
                                                             "Welcome Bob!\n" +
                                                             "Chat started, type . to quit Application\n" +
                                                             "\n" +
                                                             "10.10am - Bob: I'm good thanks\n" +
                                                             "10.10am - Bob: I'm good thanks\n" +
                                                             "10.10am - Bob: how are you?\n" +
                                                             "10.10am - Bob: how are you?"));
    }

    private UserIO createConsole(String userTypedText) {
        ByteArrayInputStream userInput = new ByteArrayInputStream(userTypedText.getBytes());
        return new UserIO(userInput, output);
    }

    private void createServerThread(ChatServer server) {
        Runnable runnable = server::start;
        Executors.newSingleThreadExecutor().submit(runnable);
    }

    private void startChat(UserIO console, ChatServer server) throws IOException {
        createServerThread(server);
        new AppCreator(console, testClock).start("in");
    }
}

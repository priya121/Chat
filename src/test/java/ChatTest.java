import interfaces.ServerSocketConnection;
import interfaces.SocketConnection;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChatTest {
    private ByteArrayOutputStream recordedOutput = new ByteArrayOutputStream();
    private PrintStream output = new PrintStream(recordedOutput);
    private ServerSocketConnection serverSocket;
    private SocketConnection socketConnection;

    @Before public void setUp() throws IOException {
        serverSocket = new RealServerSocket(new ServerSocket(4444));
        Socket socket = new Socket("localhost", 4444);
        socketConnection = new RealSocket(socket);
    }

    @Test
    public void makesAConnection() throws IOException {
        UserIO console = createConsole("quit\n");
        ChatClient client = new ChatClient(console, socketConnection);
        client.writeOutToAndReadInFromClient();
        assertTrue(recordedOutput.toString().contains("You're connected on port 4444\n"));
    }

    @Test
    public void displaysUserJoinedMessageOnServer() throws IOException {
        UserIO console = createConsole("Priya\nquit\n");
        ChatClient client = new ChatClient(console, socketConnection);
        ChatServer server = new ChatServer(console, serverSocket);
        startChat(client, server);
        assertEquals("You're connected on port 4444\n" +
                     "Enter your name to register:\n" +
                     "type quit to exit\n" +
                     "Priya has now joined the chat room\n" +
                     "Welcome Priya\n" +
                     "Bye!\n", recordedOutput.toString());
    }

    @Test
    public void quitsConnectionWhenUserTypesQuit() throws IOException {
        UserIO input = createConsole("quit\n");
        ChatClient client = new ChatClient(input, socketConnection);
        ChatServer server = new ChatServer(input, serverSocket);
        startChat(client, server);
        assertTrue(recordedOutput.toString().contains("Bye!\n"));
    }
    
    @Test
    public void writesWelcomeMessageBackToUser() throws IOException {
        UserIO console = createConsole("Priya\nquit\n");
        ChatClient client = new ChatClient(console, socketConnection);
        ChatServer server = new ChatServer(console, serverSocket);
        startChat(client, server);
        assertEquals("You're connected on port 4444\n" +
                "Enter your name to register:\n" +
                "type quit to exit\n" +
                "Priya has now joined the chat room\n" +
                "Welcome Priya\n" +
                "Bye!\n", recordedOutput.toString());
    }

    private void startChat(ChatClient client, ChatServer server) throws IOException {
        createServerThread(server);
        client.writeOutToAndReadInFromClient();
    }

    private void createServerThread(final ChatServer server) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                server.readInFromAndWriteOutToClient();
            }
        };
        Executors.newSingleThreadExecutor().submit(runnable);
    }

    private UserIO createConsole(String userTypedText) {
        ByteArrayInputStream userInput = new ByteArrayInputStream(userTypedText.getBytes());
        return new UserIO(userInput, output);
    }
}

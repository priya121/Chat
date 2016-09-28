import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    @Before public void setUp() throws IOException {
        serverSocket = new RealServerSocket(new ServerSocket(4444));
        socket = new Socket("localhost", 4444);
        socketConnection = new RealSocket(socket);
    }

    @After
    public void tearDown() throws IOException {
        socketConnection.close();
        serverSocket.close();
        socket.close();
    }

    @Test
    public void makesAConnection() throws IOException {
        UserIO console = createConsole(".\n");
        ChatClient client = new ChatClient(console, socketConnection);
        client.writeOutToAndReadInFromClient();
        assertThat(recordedOutput.toString(), containsString("You're connected on port 4444\n"));
    }

    @Test
    public void displaysUserJoinedMessageOnServer() throws IOException {
        UserIO console = createConsole("Priya\n.\n.\n");
        ChatClient client = new ChatClient(console, socketConnection);
        ChatServer server = new ChatServer(console, serverSocket);
        startChat(client, server);
        server.exit();
        assertThat(recordedOutput.toString(), containsString("Priya has now joined the chat room\n"));
    }

    @Test
    public void quitsConnectionWhenUserTypesQuit() throws IOException {
        UserIO input = createConsole("Priya\n.\n.\n");
        ChatClient client = new ChatClient(input, socketConnection);
        ChatServer server = new ChatServer(input, serverSocket);
        startChat(client, server);
        server.exit();
        assertThat(recordedOutput.toString(), containsString("Bye Priya! Priya has now left the chat.\n"));
    }
    
    @Test
    public void writesWelcomeMessageBackToUser() throws IOException {
        UserIO console = createConsole("Priya\n.\n.\n");
        ChatClient client = new ChatClient(console, socketConnection);
        ChatServer server = new ChatServer(console, serverSocket);
        startChat(client, server);
        server.exit();
        assertThat(recordedOutput.toString(), containsString("Welcome Priya\n"));
    }

    @Test
    public void userCanStartAConversation() throws IOException {
        UserIO console = createConsole("Priya\nHi\nHow are you?\n.\n.\n");
        ChatClient client = new ChatClient(console, socketConnection);
        ChatServer server = new ChatServer(console, serverSocket);
        startChat(client, server);
        server.exit();
        assertThat(recordedOutput.toString(), containsString("Priya: Hi\n"));
        assertThat(recordedOutput.toString(), containsString("Priya: How Are You?\n"));
    }

    private void startChat(ChatClient client, ChatServer server) throws IOException {
        createServerThread(server);
        client.writeOutToAndReadInFromClient();
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

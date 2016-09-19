import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChatServerTest {
    private ByteArrayOutputStream recordedOutput;
    private PrintStream output;
    private ServerSocket serverSocket;
    private Socket socket;

    @Before public void setUp() throws IOException {
        recordedOutput = new ByteArrayOutputStream();
        output = new PrintStream(recordedOutput);
        serverSocket = new ServerSocket(4444);
        socket = new Socket("localhost", 4444);
    }

    @Test
    public void makesAConnection() throws IOException {
        UserIO console = createConsole("quit\n");
        ChatClient client = new ChatClient(console, socket);
        client.writeToServer();
        assertTrue(recordedOutput.toString().contains("You're connected on port 4444\n"));
    }

    @Test
    public void displaysUserJoinedMessageOnServer() throws IOException {
        UserIO console = createConsole("Priya\nquit\n");
        ChatClient client = new ChatClient(console, socket);
        ChatServer server = new ChatServer(serverSocket, console);
        startChat(client, server);
        assertEquals("You're connected on port 4444\n" +
                     "Enter your name to register:\n" +
                     "type quit to exit\n" +
                     "Priya has now joined the chat room\n" +
                     "Bye!\n", recordedOutput.toString());
    }

    @Test
    public void quitsConnectionWhenUserTypesQuit() throws IOException {
        UserIO input = createConsole("quit\n");
        ChatClient client = new ChatClient(input, socket);
        ChatServer server = new ChatServer(serverSocket, input);
        startChat(client, server);
        assertTrue(recordedOutput.toString().contains("Bye!\n"));
    }

    private void startChat(ChatClient client, ChatServer server) throws IOException {
        client.writeToServer();
        server.readInFromClient();
    }

    private UserIO createConsole(String userTypedText) {
        ByteArrayInputStream userInput = new ByteArrayInputStream(userTypedText.getBytes());
        return new UserIO(userInput, output);
    }
}

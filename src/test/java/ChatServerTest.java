import org.junit.Before;
import org.junit.Test;
import protocols.*;
import serversocket.ServerSocketStub;
import socket.SocketMockSpy;
import streamwriter.MockPrintStreamWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ChatServerTest {
    private SocketMockSpy fakeSocketSpy;
    private ServerSocketStub serverSocketStub;
    private UserIO console;
    private final ByteArrayOutputStream recordedOutput = new ByteArrayOutputStream();
    private final PrintStream output = new PrintStream(recordedOutput);
    private final List<User> users = new ArrayList<>();
    private final String EXIT_PROTOCOL = "3";
    private MockPrintStreamWriter writer;

    @Before
    public void setUp() throws IOException {
        console = createConsole("");
        List<String> protocol = Collections.singletonList(EXIT_PROTOCOL);
        List<String> message = Collections.singletonList("Priya");
        fakeSocketSpy = new SocketMockSpy(protocol, message);
        writer = new MockPrintStreamWriter(fakeSocketSpy);
        serverSocketStub = new ServerSocketStub(fakeSocketSpy);
    }

    @Test
    public void welcomeProtocolFor1() {
        ChatServer server = new ChatServer(console, serverSocketStub);
        int WELCOME_PROTOCOL = 1;

        Protocol protocol = server.process(WELCOME_PROTOCOL, "Priya");

        assertThat(protocol, instanceOf(WelcomeProtocol.class));
    }

    @Test
    public void chatProtocol2() {
        ChatServer server = new ChatServer(console, serverSocketStub);
        int CHAT_PROTOCOL = 2;

        Protocol protocol = server.process(CHAT_PROTOCOL, "");

        assertThat(protocol, instanceOf(ChatProtocol.class));
    }

    @Test
    public void exitProtocolFor3() {
        ChatServer server = new ChatServer(console, serverSocketStub);
        int EXIT_PROTOCOL = 3;

        Protocol protocol = server.process(EXIT_PROTOCOL, "Priya");

        assertThat(protocol, instanceOf(ExitProtocol.class));
    }

    @Test
    public void welcomeProtocolReturnsWelcomeMessage() {
        ChatServer server = new ChatServer(console, serverSocketStub);
        Protocol welcome = new WelcomeProtocol("Priya", users);

        String outcome = server.determineAction(welcome);

        assertEquals("Welcome Priya!\n", outcome);
    }

    @Test
    public void welcomeProtocolReturnsWelcomeMessageForAnotherUser() {
        ChatServer server = new ChatServer(console, serverSocketStub);
        Protocol welcome = new WelcomeProtocol("Ujvara", users);

        String outcome = server.determineAction(welcome);

        assertEquals("Welcome Ujvara!\n", outcome);
    }

    @Test
    public void welcomeProtocolReturnsWelcomeBackMessageForExistingUser() {
        ChatServer server = new ChatServer(console, serverSocketStub);
        users.add(new User("Ujvara"));
        Protocol welcome = new WelcomeProtocol("Ujvara", users);

        String outcome = server.determineAction(welcome);

        assertEquals("Welcome back Ujvara!\n", outcome);
    }

    @Test
    public void startsChatWithChatProtocol() {
        console = createConsole("Priya\n");
        ChatServer server = new ChatServer(console, serverSocketStub);
        Protocol chat = new ChatProtocol("Hi!");

        String outcome = server.determineAction(chat);

        assertEquals("Hi!", outcome);
    }

    @Test
    public void displaysExitMessageWhen3ReceivedFromClientSocket() {
        ChatServer server = new ChatServer(console, serverSocketStub);
        List<String> protocol = Collections.singletonList("3");
        List<String> message = Collections.singletonList("Sarah");
        fakeSocketSpy = new SocketMockSpy(protocol, message);

        server.readInFromClient(fakeSocketSpy);

        assertEquals("Bye Sarah! Exiting...\n", recordedOutput.toString());
    }

    @Test
    public void displaysMessageFromClientWhen2ReceivedFromClient() {
        ChatServer server = new ChatServer(console, serverSocketStub);
        List<String> protocols = Arrays.asList("2", "2", "3");
        List<String> messages = Arrays.asList("Hi", "How are you?", "Priya");
        fakeSocketSpy = new SocketMockSpy(protocols, messages);

        server.readInFromClient(fakeSocketSpy);

        assertEquals("Hi\n" +
                     "How are you?\n" +
                     "Bye Priya! Exiting...\n", recordedOutput.toString());
    }

    @Test
    public void exitsIfGivenExitProtocol() {
        ChatServer server = new ChatServer(console, serverSocketStub);
        Protocol chat = new ExitProtocol("Priya");

        String outcome = server.determineAction(chat);

        assertEquals("Bye Priya! Exiting...", outcome);
    }

    @Test
    public void createsInputStreamForClient() {
        ChatServer server = new ChatServer(console, serverSocketStub);
        fakeSocketSpy.getInputStream = false;

        server.readInFromClient(fakeSocketSpy);

        assertTrue(fakeSocketSpy.getInputStream);
    }

    @Test
    public void getsOutputStreamForClient() {
        ChatServer server = new ChatServer(console, serverSocketStub);
        fakeSocketSpy.getOutputStream = false;

        server.readInFromClient(fakeSocketSpy);

        assertTrue(fakeSocketSpy.getOutputStream);
    }

    @Test
    public void writesOutputStreamToClient() throws IOException {
        ChatServer server = new ChatServer(console, serverSocketStub);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("1\nMalika\n".getBytes())));

        server.actOnProtocol(fakeSocketSpy, writer, reader);

        assertThat(writer.writtenToStream, containsString("Welcome Malika!\n"));
    }

    @Test
    public void addsNewUser() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(console, serverSocketStub);
        List<String> protocols = Arrays.asList("1", "3");
        List<String> messages = Arrays.asList("Priya", "Priya");
        fakeSocketSpy = new SocketMockSpy(protocols, messages);

        server.readInFromClient(fakeSocketSpy);

        assertEquals(1, server.numberOfClients());
    }

    @Test
    public void addsTwoUsersToTheList() throws IOException {
        ChatServer server = new ChatServer(console, serverSocketStub);
        List<String> protocols = Arrays.asList("1", "1", "3");
        List<String> messages = Arrays.asList("Priya", "Joyce", "Priya");
        fakeSocketSpy = new SocketMockSpy(protocols, messages);

        server.readInFromClient(fakeSocketSpy);

        assertEquals(2, server.numberOfClients());
    }

    @Test
    public void doesNotAddTheSameUserTwice() throws IOException {
        ChatServer server = new ChatServer(console, serverSocketStub);
        List<String> protocols = Arrays.asList("1", "1", "3");
        List<String> messages = Arrays.asList("Priya", "Priya", "Priya");
        fakeSocketSpy = new SocketMockSpy(protocols, messages);

        server.readInFromClient(fakeSocketSpy);

        assertEquals(1, server.numberOfClients());
    }

    @Test
    public void addsFourUsers() throws IOException {
        ChatServer server = new ChatServer(console, serverSocketStub);
        String WELCOME_PROTOCOL = "1";
        String EXIT_PROTOCOL = "3";
        List<String> protocols = Arrays.asList(WELCOME_PROTOCOL, WELCOME_PROTOCOL, WELCOME_PROTOCOL, WELCOME_PROTOCOL, EXIT_PROTOCOL);
        List<String> messages = Arrays.asList("Eric", "Ludmilla", "Tom", "Harry", "Priya");
        fakeSocketSpy = new SocketMockSpy(protocols, messages);

        server.readInFromClient(fakeSocketSpy);

        assertEquals(4, server.numberOfClients());
    }

    private UserIO createConsole(String userTypedText) {
        ByteArrayInputStream userInput = new ByteArrayInputStream(userTypedText.getBytes());
        return new UserIO(userInput, output);
    }
}

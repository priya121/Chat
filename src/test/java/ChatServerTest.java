import fakes.FakePrintStreamWriter;
import fakes.FakeServerSocket;
import fakes.FakeSocketSpy;
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
        console = createConsole("");
        socket = new FakeSocketSpy();
        socketConnection = new FakeServerSocket(socket);
        serverSocket = new RealServerSocket(new ServerSocket(4444));
        realSocket = new RealSocket(new Socket("localhost", 4444));
    }

    @After
    public void tearDown() {
        realSocket.close();
        socket.close();
        serverSocket.close();
    }

    @Test
    public void getsInputStreamFromClient() {
        ChatServer server = new ChatServer(console, socketConnection);
        socket.getInputStream = false;
        server.readInFromAndWriteOutToClient();
        assertTrue(socket.getInputStream);
    }

    @Test
    public void createsOutputStreamToSendToClient() {
        ChatServer server = new ChatServer(console, socketConnection);
        socket.getOutputStream = false;
        server.readInFromAndWriteOutToClient();
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
        assertEquals(1, server.users.size());
    }

    private UserIO createConsole(String userTypedText) {
        ByteArrayInputStream userInput = new ByteArrayInputStream(userTypedText.getBytes());
        return new UserIO(userInput, output);
    }

    private void createServerThread(final ChatServer server) {
        Runnable runnable = new Runnable() {

            public void run() {
                server.readInFromAndWriteOutToClient();
            }
        };
        Executors.newSingleThreadExecutor().submit(runnable);
    }
}

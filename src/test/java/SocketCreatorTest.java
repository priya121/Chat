import exit.ExitSpy;
import exit.Exiter;
import org.junit.Test;
import socket.SocketConnection;
import socket.SocketMockSpy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SocketCreatorTest {
    private final ByteArrayOutputStream recordedOutput = new ByteArrayOutputStream();
    private final PrintStream output = new PrintStream(recordedOutput);
    SocketConnection socket = new SocketMockSpy(Arrays.asList("1"), Arrays.asList(""));

    @Test
    public void createsASocketGivenAnIPAddressAndPort() throws IOException {
        UserIO console = getUserIO("y\n");
        Exiter exit = new ExitSpy("Priya");
        SocketCreator socketCreator = new SocketCreator(console, socket);
        SocketConnection creator = socketCreator.create(exit);
        assertTrue(creator instanceof SocketMockSpy);
    }

    @Test
    public void asksUserToReEnterStartChatMessage() throws IOException {
        UserIO console = getUserIO("chat\ny\n");
        Exiter exit = new ExitSpy("Priya");
        SocketCreator socketCreator = new SocketCreator(console, socket);
        socketCreator.create(exit);
        assertThat(recordedOutput.toString(), containsString("Invalid start message"));
    }

    @Test
    public void exitsIfUserEntersN() throws IOException {
        UserIO console = getUserIO("n\n");
        ExitSpy exit = new ExitSpy("Priya");
        SocketCreator socketCreator = new SocketCreator(console, socket);
        socketCreator.create(exit);
        assertTrue(exit.called);
    }

    private UserIO getUserIO(String userInput) {
        ByteArrayInputStream input = new ByteArrayInputStream(userInput.getBytes());
        return new UserIO(input, output);
    }

}

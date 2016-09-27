import fakes.FakeSocketSpy;
import interfaces.Exiter;
import interfaces.SocketConnection;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SocketCreatorTest {
    private ByteArrayOutputStream recordedOutput = new ByteArrayOutputStream();
    private PrintStream output = new PrintStream(recordedOutput);

    @Test
    public void createsASocketGivenAnIPAddressAndPort() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream("y\n".getBytes());
        UserIO console = new UserIO(input, output);
        Exiter exit = new FakeExiter(console);
        SocketConnection socket = new FakeSocketSpy();
        SocketCreator socketCreator = new SocketCreator(console, socket);
        SocketConnection creator = socketCreator.create(exit);
        assertTrue(creator instanceof FakeSocketSpy);
    }

    @Test
    public void asksUserToReenterStartChatMessage() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream("chat\ny\n".getBytes());
        UserIO console = new UserIO(input, output);
        Exiter exit = new FakeExiter(console);
        SocketConnection socket = new FakeSocketSpy();
        SocketCreator socketCreator = new SocketCreator(console, socket);
        socketCreator.create(exit);
        assertThat(recordedOutput.toString(), containsString("Invalid start message"));
    }

    @Test
    public void exitsIfUserEntersN() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream("n\n".getBytes());
        UserIO console = new UserIO(input, output);
        FakeExiter exit = new FakeExiter(console);
        SocketConnection socket = new FakeSocketSpy();
        SocketCreator socketCreator = new SocketCreator(console, socket);
        socketCreator.create(exit);
        assertTrue(exit.called);
    }
}

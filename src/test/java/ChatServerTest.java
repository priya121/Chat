import fakes.FakeIO;
import fakes.FakeServerSocket;
import fakes.FakeSocket;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class ChatServerTest {
    private FakeIO fakeInput;
    private FakeSocket socket;
    private FakeServerSocket socketConnection;

    @Before
     public void setUp() throws IOException {
         fakeInput = new FakeIO(Arrays.asList("Priya", "quit"));
         socket = new FakeSocket();
         socketConnection = new FakeServerSocket(socket);
     }
     @Test
     public void getsInputStreamFromClient() {
          ChatServer server = new ChatServer(fakeInput, socketConnection);
          socket.getInputStream = false;
          server.readInFromAndWriteOutToClient();
          assertTrue(socket.getInputStream);
     }

     @Test
     public void createsOutputStreamToSendToClient() {
          ChatServer server = new ChatServer(fakeInput, socketConnection);
          socket.getOutputStream = false;
          server.readInFromAndWriteOutToClient();
          assertTrue(socket.getOutputStream);
     }
}

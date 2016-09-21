import fakes.FakeIO;
import fakes.FakeServerSocket;
import fakes.FakeSocket;
import interfaces.ServerSocketConnection;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class ChatServerTest {

     @Test
     public void getsInputFromClient() {
          FakeIO fakeInput = new FakeIO(Arrays.asList("Priya", "quit"));
          FakeSocket socket = new FakeSocket();
          ServerSocketConnection socketConnection = new FakeServerSocket(socket);
          ChatServer server = new ChatServer(fakeInput, socketConnection);
          server.readInFromAndWriteOutToClient();
          assertTrue(socket.getOutputStream);
     }

     @Test
     public void writesOutputToClient() {
          FakeIO fakeInput = new FakeIO(Arrays.asList("Priya", "quit"));
          FakeSocket socket = new FakeSocket();
          ServerSocketConnection socketConnection = new FakeServerSocket(socket);
          ChatServer server = new ChatServer(fakeInput, socketConnection);
          server.readInFromAndWriteOutToClient();
          assertTrue(socket.getInputStream);
     }
}

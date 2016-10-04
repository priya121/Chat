package socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class SocketMockSpy implements SocketConnection {
    public boolean getOutputStream;
    public boolean getInputStream;
    public boolean closed;
    private final LinkedList<String> messages;
    private final LinkedList<String> protocol;

    public SocketMockSpy(List<String> protocol, List<String> messages) {
        this.getInputStream = false;
        this.getOutputStream = false;
        this.closed = false;
        this.protocol = new LinkedList<>(protocol);
        this.messages = new LinkedList<>(messages);
    }

    @Override
    public int getPort() {
        return 4444;
    }

    @Override
    public InputStream getInputStream() {
        getInputStream = true;
        return new ByteArrayInputStream((protocol.pop() + "\n" + messages.pop()).getBytes());
    }

    @Override
    public OutputStream getOutputStream() {
        getOutputStream = true;
        return new ByteArrayOutputStream();
    }

    @Override
    public void close() {
        closed = true;
    }
}

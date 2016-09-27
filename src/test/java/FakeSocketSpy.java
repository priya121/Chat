import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FakeSocketSpy implements SocketConnection {
    public boolean getOutputStream;
    public boolean getInputStream;
    public boolean closed;

    public FakeSocketSpy() {
        this.getInputStream = false;
        this.getOutputStream = false;
        this.closed = false;
    }

    @Override
    public int getPort() {
        return 4444;
    }

    @Override
    public InputStream getInputStream() {
        getInputStream = true;
        String inputFromServer = "Priya";
        return new ByteArrayInputStream(inputFromServer.getBytes());
    }

    @Override
    public OutputStream getOutputStream() {
        getOutputStream = true;
        OutputStream outputStream = new ByteArrayOutputStream() ;
        return outputStream;
    }

    @Override
    public void close() {
        closed = true;
    }
}

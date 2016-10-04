package socket;

import java.io.InputStream;
import java.io.OutputStream;

public interface SocketConnection {
    int getPort();
    InputStream getInputStream();
    OutputStream getOutputStream();
    void close();
}

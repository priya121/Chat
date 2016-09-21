import interfaces.SocketConnection;
import interfaces.Writer;

import java.io.OutputStream;
import java.io.PrintWriter;

public class FakePrintWriter implements Writer {

    private final PrintWriter printWriter;
    private final OutputStream outputStream;

    public FakePrintWriter(SocketConnection socketConnection) {
        this.outputStream = socketConnection.getOutputStream();
        this.printWriter = new PrintWriter(outputStream, true);
    }

    @Override
    public void println(String name) {
        printWriter.println(name);
    }

    @Override
    public void flush() {
        printWriter.flush();
    }
}

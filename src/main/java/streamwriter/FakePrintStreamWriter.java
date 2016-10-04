package streamwriter;

import socket.SocketConnection;

import java.io.OutputStream;
import java.io.PrintWriter;

public class FakePrintStreamWriter implements StreamWriter {

    private final PrintWriter printWriter;
    private final OutputStream outputStream;
    public String writtenToStream;
    boolean AUTOFLUSH = true;

    public FakePrintStreamWriter(SocketConnection socketConnection) {
        this.outputStream = socketConnection.getOutputStream();
        this.printWriter = new PrintWriter(outputStream, AUTOFLUSH);
    }

    @Override
    public void println(String name) {
        writtenToStream += name + "\n";
        printWriter.println(name);
    }

    @Override
    public void flush() {
        printWriter.flush();
    }
}

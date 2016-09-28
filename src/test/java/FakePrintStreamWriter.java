import java.io.OutputStream;
import java.io.PrintWriter;

public class FakePrintStreamWriter implements StreamWriter {

    private final PrintWriter printWriter;
    private final OutputStream outputStream;
    public String writtenToStream;

    public FakePrintStreamWriter(SocketConnection socketConnection) {
        this.outputStream = socketConnection.getOutputStream();
        this.printWriter = new PrintWriter(outputStream, true);
    }

    @Override
    public void println(String name) {
        writtenToStream += name;
        printWriter.println(name);
    }

    @Override
    public void flush() {
        printWriter.flush();
    }
}

import interfaces.StreamWriter;

import java.io.OutputStream;
import java.io.PrintWriter;

public class RealPrintWriter implements StreamWriter {
    private final PrintWriter printWriter;
    private static boolean AUTOFLUSH;

    public RealPrintWriter(OutputStream outputStream) {
        AUTOFLUSH = true;
        this.printWriter = new PrintWriter(outputStream, AUTOFLUSH);
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

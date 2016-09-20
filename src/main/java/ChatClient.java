import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private UserIO io;

    public ChatClient(UserIO io, Socket socket) {
        this.io = io;
        this.socket = socket;
    }

    public void writeToServer() throws IOException {
        io.showInitialMessage(socket.getPort());
        String message = io.getInput();
        PrintWriter printWriter = createPrintWriter();
        writeMessageTillQuit(message, printWriter);
        closeSocket();
    }

    private void writeMessageTillQuit(String message, PrintWriter printWriter) {
        while (!message.contains("quit")) {
            printWriter.println(message);
            printWriter.flush();
            message = io.getInput();
        }
        closeSocket();
    }

    private PrintWriter createPrintWriter() throws IOException {
        OutputStream outToServer = socket.getOutputStream();
        return new PrintWriter(outToServer, true);
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

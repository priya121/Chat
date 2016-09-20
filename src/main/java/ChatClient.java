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
        io.showConnectionMessage(socket.getPort());
        io.showInitialMessage();
        PrintWriter printWriter = createPrintWriter();

        writeTillQuit(printWriter);
        closeSocket();
    }

    private void writeTillQuit(PrintWriter printWriter) {
        String message = io.getInput();
        while (!message.contains("quit")) {
            printWriter.println(message);
            printWriter.flush();

            message = io.getInput();
        }
    }

    private PrintWriter createPrintWriter() throws IOException {
        OutputStream outToServer = socket.getOutputStream();
        return new PrintWriter(outToServer, true);
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

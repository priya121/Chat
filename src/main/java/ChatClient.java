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
        showConnectionMessage();
        io.showInitialMessage();

        String message = io.getInput();

        writeMessageTillQuit(message);
        closeSocket();
    }

    private void writeMessageTillQuit(String message) throws IOException {
        while (!message.equals("quit")) {
            PrintWriter printWriter = createPrintWriter();
            printWriter.println(message);
            printWriter.flush();
            message = io.getInput();
        }
    }

    private void showConnectionMessage() {
        io.showConnectionMessage(socket.getPort());
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

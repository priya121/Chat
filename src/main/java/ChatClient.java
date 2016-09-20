import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private UserIO io;

    public ChatClient(UserIO io, Socket socket) {
        this.io = io;
        this.socket = socket;
    }

    public void writeToServer() {
        io.showInitialMessage(socket.getPort());
        String message = io.getInput();
        try {
            PrintWriter printWriter = createPrintWriter();
            writeMessageTillQuit(message, printWriter);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        io.showExitMessage();
        closeSocket();
    }

    private void writeMessageTillQuit(String name, PrintWriter printWriter) {
        while (!name.contains("quit")) {
            printWriter.println(name);
            printWriter.flush();
            try {
                InputStream inputFromClient = socket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputFromClient);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                io.welcomeMessage(reader.readLine());
                name = io.getInput();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
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

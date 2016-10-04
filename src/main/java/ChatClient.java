import interfaces.ConsoleIO;
import interfaces.SocketConnection;
import interfaces.StreamWriter;

import java.io.*;

public class ChatClient {
    private final SocketConnection socket;
    private final ConsoleIO io;

    public ChatClient(ConsoleIO io, SocketConnection socket) {
        this.io = io;
        this.socket = socket;
    }

    public void writeOutToAndReadInFromClient() {
        io.showInitialMessage(socket.getPort());
        String name = io.getInput();
        StreamWriter printWriter = createPrintWriter();
        writeMessageToServerUntilQuit(name, printWriter);
        io.showExitMessage();
        closeSocket();
    }

    public void writeMessageToServerUntilQuit(String name, StreamWriter printWriter) {
        while (!name.contains("quit")) {
            try {
                printWriter.println(name);
                printWriter.flush();
                readInMessageFromServer();
                name = io.getInput();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private void readInMessageFromServer() throws IOException {
        BufferedReader reader = createBufferedReader();
        io.welcomeMessage(reader.readLine());
    }

    private StreamWriter createPrintWriter() {
        OutputStream outToServer = socket.getOutputStream();
        return new RealPrintWriter(outToServer);
    }

    private BufferedReader createBufferedReader() {
        InputStream inputFromClient = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputFromClient);
        return new BufferedReader(inputStreamReader);
    }

    private void closeSocket() {
        socket.close();
    }
}

import interfaces.ConsoleIO;
import interfaces.SocketConnection;

import java.io.*;

public class ChatClient {
    private final SocketConnection socket;
    private final ConsoleIO io;

    public ChatClient(ConsoleIO io, SocketConnection socket) {
        this.io = io;
        this.socket = socket;
    }

    public void writeToServer() {
        io.showInitialMessage(socket.getPort());
        String name = io.getInput();
        interfaces.Writer printWriter = createPrintWriter();
        writeMessageToServerUntilQuit(name, printWriter);
        io.showExitMessage();
        closeSocket();
    }

    public void writeMessageToServerUntilQuit(String name, interfaces.Writer printWriter) {
         while (!name.contains("quit")) {
            printWriter.println(name);
            printWriter.flush();
            readInMessageFromServer();
            name = io.getInput();
        }
    }

    private void readInMessageFromServer() {
        try {
            BufferedReader reader = createBufferedReader();
            io.welcomeMessage(reader.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private RealPrintWriter createPrintWriter() {
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

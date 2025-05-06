import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Main server class that listens for and handles client connections
public class Server {
    // Constant defining the server port
    private static final int PORT = 51234;
    // Thread pool for handling client connections concurrently
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    // Shared tuple space for client-server communication
    private final TupleSpace tupleSpace = new TupleSpace();

    // Method to start the server
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            // Infinite loop to continuously accept client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new ClientHandler(clientSocket, tupleSpace));
            }
        } catch (IOException e) {
            // Handle I/O exceptions (like port already in use)
            e.printStackTrace();
        }
    }
    // Main method - program entry point
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
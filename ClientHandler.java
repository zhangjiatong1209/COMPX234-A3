import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Handler class for client connections, implements Runnable for thread usage
public class ClientHandler implements Runnable {
     // Fields to store client socket and shared tuple space
    private final Socket clientSocket;
    private final TupleSpace tupleSpace;

// Constructor to initialize handler with client socket and tuple space
    public ClientHandler(Socket clientSocket, TupleSpace tupleSpace) {
        this.clientSocket = clientSocket;
        this.tupleSpace = tupleSpace;
    }
    @Override
    public void run() {
        // Try-with-resources block for automatic resource management
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String request;
            // Continuously read requests from client until connection closes
            while ((request = in.readLine()) != null) {
                String response = processRequest(request);
                out.println(response);
            }
        } catch (IOException e) {
            // Handle I/O exceptions
            e.printStackTrace();
        } finally {
            // Ensure socket is closed even if exception occurs
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
     // Processes client requests and generates responses
    private String processRequest(String request) {
        String command = request.substring(3, 4);
        String key = request.substring(4);
        String value = "";
        // Special handling for PUT command which has a value
        if (command.equals("P")) {
            value = request.substring(key.length() + 5);
        }
         // Process based on command type
        switch (command) {
            case "R":
                String readValue = tupleSpace.read(key);
                if (readValue != null) {
                    return formatResponse("OK (" + key + ", " + readValue + ") read", request.length());
                } else {
                    return formatResponse("ERR " + key + " does not exist", request.length());
                }
            case "G":
                String getValue = tupleSpace.get(key);
                if (getValue != null) {
                    return formatResponse("OK (" + key + ", " + getValue + ") removed", request.length());
                } else {
                    return formatResponse("ERR " + key + " does not exist", request.length());
                }
            case "P":
                int putResult = tupleSpace.put(key, value);
                if (putResult == 0) {
                    return formatResponse("OK (" + key + ", " + value + ") added", request.length());
                } else {
                    return formatResponse("ERR " + key + " already exists", request.length());
                }
            default:
                return formatResponse("ERR invalid command", request.length());
        }
    }
    // Formats response messages with size prefix
    private String formatResponse(String message, int requestSize) {
        int size = message.length() + 3;
        return String.format("%03d %s", size, message);
    }
}
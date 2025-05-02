import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final TupleSpace tupleSpace;

    public ClientHandler(Socket clientSocket, TupleSpace tupleSpace) {
        this.clientSocket = clientSocket;
        this.tupleSpace = tupleSpace;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                String response = processRequest(request);
                out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String processRequest(String request) {
        String command = request.substring(3, 4);
        String key = request.substring(4);
        String value = "";
        if (command.equals("P")) {
            value = request.substring(key.length() + 5);
        }

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

    private String formatResponse(String message, int requestSize) {
        int size = message.length() + 3;
        return String.format("%03d %s", size, message);
    }
}
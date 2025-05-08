import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    // The port number on which the server will listen for incoming client connections.
    private static final int PORT = 9090;
    //The time interval (in seconds) at which the server will print a summary of its operations.
    private static final int TIMEOUT_SECONDS = 10;
    // A list to store the tuples in the tuple space. Each tuple consists of a key - value pair.
    private final List<Tuple> tupleSpace = new ArrayList<>();
    // The total number of clients that have connected to the server.
    private int clientCount = 0;
    private int operationCount = 0;
    private int readCount = 0;
    private int getCount = 0;
    private int putCount = 0;
    private int errorCount = 0;
     // A thread pool with a fixed size of 10 threads to handle multiple client connections concurrently.
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // Starts the server, listens for incoming client connections, and schedules summary tasks
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Print a message indicating that the server is running and ready to accept clients
            System.out.println("Server is running and ready to accept multiple clients...");
            // Create a timer to schedule the summary task
            Timer timer = new Timer();
            // Schedule the SummaryTask to run every TIMEOUT_SECONDS seconds, starting immediately.
            timer.schedule(new SummaryTask(), 0, TIMEOUT_SECONDS * 1000);

            // Continuously accept incoming client connections.
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientCount++;
                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            // Print the stack trace if an I/O error occurs while starting the server.
            e.printStackTrace();
        }
    }
// A private inner class that implements the Runnable interface to handle client requests.
    private class ClientHandler implements Runnable {
        private final Socket clientSocket;

 // Constructor to initialize the client socket.
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

// The main method that is executed when the thread is started.
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    // A variable to hold each line of input from the client.
                String inputLine;
                // Read each line of input from the client until the end of the stream is reached.
                while ((inputLine = in.readLine()) != null) {
                    operationCount++;
                    // Split the input line into parts based on whitespace.
                    String[] parts = inputLine.split(" ");
                    String command = parts[0];
                    String key = parts[1];
                    String response;
                    if ("PUT".equals(command)) {
                        if (parts.length < 3) {
                            response = "ERR invalid input";
                            errorCount++;
                        } else {
                            String value = parts[2];
                            response = handlePut(key, value);
                        }
                    } else if ("READ".equals(command)) {
                        response = handleRead(key);
                    } else if ("GET".equals(command)) {
                        response = handleGet(key);
                    } else {
                        response = "ERR invalid command";
                        errorCount++;
                    }
                     // Send the response back to the client.
                    out.println(response);
                }
            } catch (IOException e) {
                // Print the stack trace if an I/O error occurs while handling the client connection.
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                      // Print the stack trace if an error occurs while closing the socket.
                    e.printStackTrace();
                }
            }
        }

         // Handles the PUT operation by adding a new tuple to the tuple space if the key does not exist.
        private String handlePut(String key, String value) {
            putCount++;
            for (Tuple tuple : tupleSpace) {
                if (tuple.getKey().equals(key)) {
                    return "ERR " + key + " already exists";
                }
            }
                 
// If the key does not exist, add a new tuple to the tuple space.
            tupleSpace.add(new Tuple(key, value));
            return "OK (" + key + ", " + value + ") added";
        }

        // Handles the READ operation by retrieving the value associated with the key from the tuple space.
        private String handleRead(String key) {
            readCount++;
            for (Tuple tuple : tupleSpace) {
                if (tuple.getKey().equals(key)) {
                    return "OK (" + key + ", " + tuple.getValue() + ") read";
                }
            }
            errorCount++;
            return "ERR " + key + " does not exist";
        }

        // Handles the GET operation by removing the tuple associated with the key from the tuple space.
        private String handleGet(String key) {
            getCount++;
            for (Iterator<Tuple> it = tupleSpace.iterator(); it.hasNext(); ) {
                Tuple tuple = it.next();
                if (tuple.getKey().equals(key)) {
                    it.remove();
                    return "OK (" + key + ", " + tuple.getValue() + ") removed";
                }
            }
            errorCount++;
            return "ERR " + key + " does not exist";
        }
    }

     // A private inner class that extends TimerTask to print a summary of the server's operations.
    private class SummaryTask extends TimerTask {
        // The main method that is executed when the timer fires.
        @Override
        public void run() {
            int tupleCount = tupleSpace.size();
            int totalTupleSize = 0;
            int totalKeySize = 0;
            int totalValueSize = 0;
            
             // Iterate through all tuples in the tuple space to calculate the total sizes.
            for (Tuple tuple : tupleSpace) {
                totalTupleSize += tuple.getKey().length() + tuple.getValue().length();
                totalKeySize += tuple.getKey().length();
                totalValueSize += tuple.getValue().length();
            }
            double averageTupleSize = tupleCount > 0? (double) totalTupleSize / tupleCount : 0;
            double averageKeySize = tupleCount > 0? (double) totalKeySize / tupleCount : 0;
            double averageValueSize = tupleCount > 0? (double) totalValueSize / tupleCount : 0;

                // Print the server summary.
            System.out.println("xxxxxxxxxxxxxxxxSERVER SUMMARYxxxxxxxxxxxxxxx");
            System.out.println("tuples number: " + tupleCount);
            System.out.println("Total number of clients: " + clientCount);
            System.out.println("Total number of operations: " + operationCount);
            System.out.println("Average tuplesize: " + averageTupleSize);
            System.out.println("Average keysize: " + averageKeySize);
            System.out.println("Average valuesize: " + averageValueSize);
            System.out.println("Total READs: " + readCount);
            System.out.println("Total GETs: " + getCount);
            System.out.println("Total PUTs: " + putCount);
            System.out.println("Total errors: " + errorCount);
            System.out.println("xxxxxxxxxxxENDxxxxxxxxxxxxxxxxxx");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
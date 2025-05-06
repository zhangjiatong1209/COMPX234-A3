import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 9090;
    private static final int TIMEOUT_SECONDS = 10;
    private final List<Tuple> tupleSpace = new ArrayList<>();
    private int clientCount = 0;
    private int operationCount = 0;
    private int readCount = 0;
    private int getCount = 0;
    private int putCount = 0;
    private int errorCount = 0;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and ready to accept multiple clients...");
            Timer timer = new Timer();
            timer.schedule(new SummaryTask(), 0, TIMEOUT_SECONDS * 1000);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientCount++;
                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    operationCount++;
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

        private String handlePut(String key, String value) {
            putCount++;
            for (Tuple tuple : tupleSpace) {
                if (tuple.getKey().equals(key)) {
                    return "ERR " + key + " already exists";
                }
            }
            tupleSpace.add(new Tuple(key, value));
            return "OK (" + key + ", " + value + ") added";
        }

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

    private class SummaryTask extends TimerTask {
        @Override
        public void run() {
            int tupleCount = tupleSpace.size();
            int totalTupleSize = 0;
            int totalKeySize = 0;
            int totalValueSize = 0;
            for (Tuple tuple : tupleSpace) {
                totalTupleSize += tuple.getKey().length() + tuple.getValue().length();
                totalKeySize += tuple.getKey().length();
                totalValueSize += tuple.getValue().length();
            }
            double averageTupleSize = tupleCount > 0? (double) totalTupleSize / tupleCount : 0;
            double averageKeySize = tupleCount > 0? (double) totalKeySize / tupleCount : 0;
            double averageValueSize = tupleCount > 0? (double) totalValueSize / tupleCount : 0;

            System.out.println("--------------------- Server Summary ---------------------");
            System.out.println("Number of tuples: " + tupleCount);
            System.out.println("Average tuple size: " + averageTupleSize);
            System.out.println("Average key size: " + averageKeySize);
            System.out.println("Average value size: " + averageValueSize);
            System.out.println("Total number of clients: " + clientCount);
            System.out.println("Total number of operations: " + operationCount);
            System.out.println("Total READs: " + readCount);
            System.out.println("Total GETs: " + getCount);
            System.out.println("Total PUTs: " + putCount);
            System.out.println("Total errors: " + errorCount);
            System.out.println("---------------------------------------------------------");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
import java.io.*;
import java.net.*;

public class Client {
    // The host address of the server to which the client will connect
    private static final String SERVER_HOST = "localhost";
     // The port number of the server to which the client will connect.
    private static final int SERVER_PORT = 9090;

// An array containing the file paths of the text files to be sent to the server
    public static void main(String[] args) {
        String[] filePaths = {
            "client_1.txt",
            "client_2.txt",
            "client_3.txt",
            "client_4.txt",
            "client_5.txt",
            "client_6.txt",
            "client_7.txt",
            "client_8.txt",
            "client_9.txt",
            "client_10.txt"
        };

        // Iterate through each file path in the array.
        for (String filePath : filePaths) {
            // Create a BufferedReader to read the contents of the current text file.
            try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
            // Create a Socket to establish a connection to the server.
                 Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            // Create a BufferedReader to read responses from the server.
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    // A variable to hold each line read from the file
                String inputLine;
                // Read each line from the file until the end is reached.
                while ((inputLine = fileReader.readLine()) != null) {
                    out.println(inputLine);
                    String response = in.readLine();
                    System.out.println(inputLine + ": " + response);
                }
            } catch (IOException e) {
                // If an I/O error occurs, print the stack trace.
                e.printStackTrace();
            }
        }
    }
}
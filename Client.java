import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9090;

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

        for (String filePath : filePaths) {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
                 Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                String inputLine;
                while ((inputLine = fileReader.readLine()) != null) {
                    out.println(inputLine);
                    String response = in.readLine();
                    System.out.println(inputLine + ": " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
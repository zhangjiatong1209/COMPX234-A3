import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 51234;

    public void sendRequests(String filePath) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                out.println(line);
                String response = in.readLine();
                System.out.println(line + ": " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 定义十个测试文件路径
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

        Client client = new Client();
        for (String filePath : filePaths) {
            client.sendRequests(filePath);
        }
    }
}
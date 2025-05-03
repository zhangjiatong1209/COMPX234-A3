import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 51234;

    /**
     * 向服务器发送文件路径请求
     *
     * @param filePath 要发送的文件路径
     */
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
        if (args.length == 0) {
            System.err.println("Usage: java Client <request_file_path1> [<request_file_path2>...]");
            System.exit(1);
        }
        Client client = new Client();
        for (String filePath : args) {
            client.sendRequests(filePath);
        }
    }
}
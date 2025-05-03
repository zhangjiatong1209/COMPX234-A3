import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 客户端类，用于向服务器发送文件路径请求并接收响应
 */
public class Client {
    // 服务器主机地址
    private static final String SERVER_HOST = "localhost";
    // 服务器端口号
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
            // 逐行读取文件内容并发送给服务器
            while ((line = fileReader.readLine()) != null) {
                out.println(line);
                // 接收服务器的响应
                String response = in.readLine();
                // 打印本地发送的内容和服务器的响应
                System.out.println(line + ": " + response);
            }
        } catch (IOException e) {
            // 打印异常堆栈信息，实际应用中可以更详细地记录日志等
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 直接在代码中指定十个测试文件路径
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
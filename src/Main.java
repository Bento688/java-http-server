import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            boolean isRunning = true;

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection established!");

                InputStream input = clientSocket.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String requestLine = bufferedReader.readLine();
                System.out.println("Request: " + requestLine);

                String headerLine;
                while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isEmpty()) {
                    System.out.println("Header: " + headerLine);
                }

                String htmlBody = "<html><body><h1>Hello from Pure Java!</h1><p>The server is alive.</p></body></html>";

                String httpResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" + "Content-Length: " + htmlBody.getBytes().length + "\r\n" + "\r\n"
                        + htmlBody;

                OutputStream output = clientSocket.getOutputStream();
                output.write(httpResponse.getBytes("UTF-8"));

                output.flush();

                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }
    }
}

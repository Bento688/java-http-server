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
                if (requestLine == null) continue;

                System.out.println("Request: " + requestLine);

                String[] requestParts = requestLine.split(" ");
                String method = requestParts[0];
                String path = requestParts[1];

                String htmlBody;
                String statusCode = "200 OK";

                if (path.equals("/")) {
                    htmlBody = "<html><body><h1>Home Page</h1><p>Welcome to pure Java.</p></body></html>";
                } else if (path.equals("/about")) {
                    htmlBody = "<html><body><h1>About Us</h1><p>We build from scratch.</p></body></html>";
                } else {
                    htmlBody = "<html><body><h1>404 Not Found</h1><p>Nothing to see here.</p></body></html>";
                    statusCode = "404 Not Found";
                }

                String headerLine;
                while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isEmpty()) {
                    System.out.println("Header: " + headerLine);
                }

                String httpResponse = "HTTP/1.1 " +  statusCode + "\r\n" +
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

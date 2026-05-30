import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        int port = 8080;

        // make a threadPool of 10 workers.
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        Router app = new Router();

        app.get("/", (req) -> "<html><body><h1>Home Page</h1><p>Welcome to pure Java.</p></body></html>");
        app.get("/about", (req) -> "<html><body><h1>About Us</h1><p>We build from scratch.</p></body></html>");
        app.get("/api", (req) -> "{\"status\": \"active\", \"language\": \"Java\"}");
        app.get("/echo", (req) -> {
            // 'req' contains the full request line, e.g., "GET /echo HTTP/1.1"
            return "<html><body><h1>Echo:</h1><p>You sent: " + req + "</p></body></html>";
        });

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            boolean isRunning = true;

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();

                threadPool.submit(() -> {
                    try {
                        System.out.println("Handled by " + Thread.currentThread().getName());

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        HttpRequest req = RequestParser.parse(bufferedReader);

                        String requestLine = bufferedReader.readLine();

                        if (req != null) {
                            String[] requestParts = requestLine.split(" ");
                            String method = requestParts[0];
                            String path = requestParts[1];

                            // Drain the headers
                            String headerLine;
                            while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isEmpty()) {};

                            String httpResponse = app.handleRequest(req);

                            OutputStream output = clientSocket.getOutputStream();
                            output.write(httpResponse.getBytes("UTF-8"));
                            output.flush();
                        }
                    } catch (IOException e) {
                        System.out.println("Failed to start server: " + e.getMessage());
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }
    }
}

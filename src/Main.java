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

        app.get("/api/user", (req) -> {
            String userId = req.getQueryParam("id");

            if (userId == null) {
                return new HttpResponse(400, "Bad Request", "Missing ID Parameter!");
            }

            HttpResponse res = new HttpResponse(200, "OK", "{\"user\": " + userId + "}");
            res.addHeader("Content-Type", "application/json");
            return res;
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

                        if (req != null) {
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

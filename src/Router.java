import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Router {

    // the routes HashMap
    private Map<String, Function<String, String>> routes = new HashMap<>();

    // the get method (append GET operations to the hashmap)
    public void get(String path, Function<String, String> handler) {
        routes.put("GET " + path, handler);
    }

    // the "runner" for the method inside the hashmap
    public String handleRequest(String method, String path, String rawRequest) {
        String routeKey = method + " " + path;
        String statusCode = "200 OK";
        String htmlBody;

        if (routes.containsKey(routeKey)) {
            htmlBody = routes.get(routeKey).apply(rawRequest);
        } else {
            htmlBody = "<html><body><h1>404 Not Found</h1><p>Route does not exist.</p></body></html>";
            statusCode = "404 Not Found";
        }

        return "HTTP/1.1 " + statusCode + "\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + htmlBody.getBytes().length + "\r\n" + "\r\n" +
                htmlBody;
    }
}

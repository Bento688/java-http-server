import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Router {

    // the routes HashMap
    private Map<String, Function<HttpRequest, HttpResponse>> routes = new HashMap<>();

    // the get method (append GET operations to the hashmap)
    public void get(String path, Function<HttpRequest, HttpResponse> handler) {
        routes.put("GET " + path, handler);
    }

    // the "runner" for the method inside the hashmap
    public String handleRequest(HttpRequest req) {
        String routeKey = req.getMethod() + " " + req.getPath();
        HttpResponse response;

        if (routes.containsKey(routeKey)) {
            response = routes.get(routeKey).apply(req);
        } else {
            response = new HttpResponse(404, "Not Found", "<html><body><h1>404 Not Found</h1></body></html>");
        }

        return response.build();
    }
}

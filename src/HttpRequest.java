import java.util.Map;
import java.util.HashMap;

public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private final String body;

    // constructor
    public HttpRequest(String method, String path, Map<String, String> queryParams, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
        this.body = body;
    }

    // getters
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getQueryParam(String key) { return queryParams.get(key); }
    public String getBody() { return body; }
}

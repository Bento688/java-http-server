import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpResponse(int statusCode, String statusMessage, String body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.body = body;

        this.headers.put("Content-Type", "text/html; charset=UTF-8");
        this.headers.put("Content-Length", String.valueOf(body.getBytes().length));
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    // serialization: converting the HttpResponse object into raw network bytes.
    public String build() {
        StringBuilder responseBuilder = new StringBuilder();

        responseBuilder.append("HTTP/1.1 ")
                .append(statusCode).append(" ")
                .append(statusMessage).append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseBuilder.append(header.getKey()).append(": ")
                    .append(header.getValue()).append("\r\n");
        }

        responseBuilder.append("\r\n").append(body);

        return responseBuilder.toString();
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {
    public static HttpRequest parse(BufferedReader reader) throws IOException {

        // read the request
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        // get the details about the request
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String fullPath = parts[1];

        String path = fullPath;
        Map<String, String> queryParams = new HashMap<>();

        // getting the queryParams
        if (fullPath.contains("?")) { // fullPath = "/example?exampleQuery=XXX&exampleQuery2=YYY"
            String[] pathParts = fullPath.split("\\?");
            path = pathParts[0]; // "/example"
            String queryString = pathParts[1]; // "exampleQuery=XXX&exampleQuery2=YYY"

            for (String param : queryString.split("&")) {
                String[] paramPair = param.split("=");
                if (paramPair.length == 2) {
                    queryParams.put(paramPair[0], paramPair[1]);
                }
            }
        }

        Map<String, String> headers = new HashMap<>();
        String headerLine;
        int contentLength = 0;

        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(": ");
            if (headerParts.length == 2) {
               headers.put(headerParts[0].trim(), headerParts[1].trim());

               if (headerParts[0].equalsIgnoreCase("Content-Length")) {
                  contentLength = Integer.parseInt(headerParts[1].trim());
               }
            }
        }

        StringBuilder bodyBuilder = new StringBuilder();
        if (contentLength > 0) {
          char[] bodyChars = new char[contentLength];
          reader.read(bodyChars, 0, contentLength);
          bodyBuilder.append(bodyChars);
        }

        return new HttpRequest(method, path, queryParams, headers, bodyBuilder.toString());
    }
}

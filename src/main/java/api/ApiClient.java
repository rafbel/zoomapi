package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ApiClient {

    private static Throttle throttle = new Throttle(1);
    private String baseUri;
    private Integer timeout;
    private String token = null;
    private Gson gson;

    private static ApiClient instance = null;

    private ApiClient() {
        this.gson = new Gson();
    }

    public static ApiClient getThrottledInstance() throws InterruptedException {
        throttle.permit();
        if (instance == null) {
            instance = new ApiClient();
        }

        return instance;
    }

    /**
     * Generates API path URL
     *
     * @param endpoint Path URL
     * @return Concatenated baseUri and endpoint
     */
    protected String urlFor(String endpoint) {
        return this.baseUri + endpoint;
    }

    /**
     * HTTP GET method request
     *
     * @param endpoint Path URL
     * @return JsonObject
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException
     */
    public JsonObject getRequest(String endpoint) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(urlFor(endpoint)))
            .timeout(Duration.ofSeconds(this.timeout))
            .setHeader("Authorization", "Bearer " + this.token)
            .build();

        return toJsonObject(client.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    /**
     * HTTP GET method request
     *
     * @param endpoint Path URL
     * @param params Query parameters
     * @return JsonObject
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException
     */
    public JsonObject getRequest(String endpoint, List<NameValuePair> params) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();

        URIBuilder uriBuilder = new URIBuilder(urlFor(endpoint));
        if (params != null) {
            uriBuilder.addParameters(params);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriBuilder.toString()))
                .timeout(Duration.ofSeconds(this.timeout))
                .setHeader("Authorization", "Bearer " + this.token)
                .build();

        return toJsonObject(client.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    /**
     * HTTP POST method request
     *
     * @param endpoint Path URL
     * @param params Query parameters
     * @param data Request body
     * @return JsonObject
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public JsonObject postRequest(String endpoint, List<NameValuePair> params, Map<String, Object> data) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URIBuilder uriBuilder = new URIBuilder(urlFor(endpoint));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriBuilder.toString()))
                .timeout(Duration.ofSeconds(this.timeout))
                .setHeader("Authorization", "Bearer " + this.token)
                .setHeader("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                .build();

        return toJsonObject(client.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    /**
     * HTTP POST method request
     *
     * @param endpoint Path URL
     * @return JsonObject
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public JsonObject postRequest(String endpoint, Map<String, Object> data) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlFor(endpoint)))
                .timeout(Duration.ofSeconds(this.timeout))
                .setHeader("Authorization", "Bearer " + this.token)
                .setHeader("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                .build();

        return toJsonObject(client.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    /**
     * HTTP PUT method request
     *
     * @param endpoint Path URL
     * @param params Query parameters
     * @param data Request body
     * @return JsonObject
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public JsonObject putRequest(String endpoint, List<NameValuePair> params, Map<String, Object> data) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URIBuilder uriBuilder = new URIBuilder(urlFor(endpoint));
        if (params != null) {
            uriBuilder.addParameters(params);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriBuilder.toString()))
                .timeout(Duration.ofSeconds(this.timeout))
                .setHeader("Authorization", "Bearer " + this.token)
                .setHeader("Content-type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                .build();

        return toJsonObject(client.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    /**
     * HTTP DELETE method request
     *
     * @param endpoint Path URL
     * @return JsonObject
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public JsonObject deleteRequest(String endpoint) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlFor(endpoint)))
                .timeout(Duration.ofSeconds(this.timeout))
                .setHeader("Authorization", "Bearer " + this.token)
                .setHeader("Content-type", "application/json")
                .DELETE()
                .build();

        return toJsonObject(client.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    /**
     * HTTP PATCH method request
     *
     * @param endpoint Path URL
     * @param data Request body
     * @return JsonObject
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public JsonObject patchRequest(String endpoint, Map<String, Object> data) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlFor(endpoint)))
                .timeout(Duration.ofSeconds(this.timeout))
                .setHeader("Authorization", "Bearer " + this.token)
                .setHeader("Content-type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                .build();

        return toJsonObject(client.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    private JsonObject toJsonObject(HttpResponse response) {
        return gson.fromJson(response.body().toString(), JsonObject.class);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}

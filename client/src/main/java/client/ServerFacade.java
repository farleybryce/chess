package client;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterLoginResult register(RegisterRequest registerRequest) throws DataAccessException {
        var request = buildRequest("POST", "/user", registerRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterLoginResult.class);
    }

    public RegisterLoginResult login(LoginRequest loginRequest) throws DataAccessException {
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterLoginResult.class);
    }

    public void logout(String authToken) throws DataAccessException {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public CreateResult createGame(CreateRequest createRequest, String authToken) throws DataAccessException {
        var request = buildRequest("POST", "/game", createRequest, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateResult.class);
    }

    public void joinGame(JoinRequest joinRequest, String authToken) throws DataAccessException {
        var request = buildRequest("PUT", "/game", joinRequest, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public ListResult listGames(String authToken) throws DataAccessException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListResult.class);
    }

    public void clear() throws DataAccessException {
        var request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }

    /*
    --- HTTP helper methods ---
     */
    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new DataAccessException(status, body);
            }

            throw new DataAccessException(status, "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
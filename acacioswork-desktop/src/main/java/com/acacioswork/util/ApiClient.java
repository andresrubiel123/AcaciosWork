package com.acacioswork.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;

/** Cliente API REST robusto con soporte para JWT y ApiResponse. @author RADJ */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8081/api";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static HttpRequest.Builder createBuilder(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("User-Agent", "AcaciosWork-Desktop/1.0");

        String token = SessionManager.getToken();
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    public static <T> T get(String endpoint, Class<T> responseType) throws Exception {
        HttpRequest request = createBuilder(endpoint).GET().build();
        return execute(request, responseType);
    }

    public static <T> T post(String endpoint, Object body, Class<T> responseType) throws Exception {
        String jsonBody = mapper.writeValueAsString(body);
        HttpRequest request = createBuilder(endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return execute(request, responseType);
    }

    public static <T> T put(String endpoint, Object body, Class<T> responseType) throws Exception {
        String jsonBody = mapper.writeValueAsString(body);
        HttpRequest request = createBuilder(endpoint)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return execute(request, responseType);
    }

    public static void delete(String endpoint) throws Exception {
        HttpRequest request = createBuilder(endpoint).DELETE().build();
        execute(request, Void.class);
    }

    private static <T> T execute(HttpRequest request, Class<T> responseType) throws Exception {
        System.out.println("[DEBUG] " + request.method() + " a: " + request.uri());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            if (body == null || body.isBlank()) return null;
            if (responseType == Void.class) return null;

            // Deserializar usando el envoltorio ApiResponse<T>
            JavaType type = mapper.getTypeFactory().constructParametricType(ApiResponse.class, responseType);
            ApiResponse<T> apiResponse = mapper.readValue(body, type);

            if (apiResponse.isSuccess()) {
                return apiResponse.getData();
            } else {
                throw new RuntimeException("API Error: " + apiResponse.getMessage());
            }
        } else if (response.statusCode() == 401 || response.statusCode() == 403) {
            SessionManager.logout();
            throw new RuntimeException("Sesión expirada o acceso denegado.");
        } else {
            // Intentar extraer mensaje de error de ApiResponse si existe
            try {
                ApiResponse<?> errorResp = mapper.readValue(body, ApiResponse.class);
                throw new RuntimeException("Error (" + response.statusCode() + "): " + errorResp.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Error del Servidor (" + response.statusCode() + "): " + body);
            }
        }
    }
}

package com.tecsup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MicroserviciosPlatoApplicationTests {
    @Value("${local.server.port}") int port;
    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> request(String method, String path, String body) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/platos"+path))
            .timeout(Duration.ofSeconds(10)).header("Content-Type", "application/json")
            .method(method, HttpRequest.BodyPublishers.ofString(body)).build(),
            HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void crudValidacionYNoEncontrado() throws Exception {
        assertEquals(400, request("POST", "", "{}").statusCode());
        var created = request("POST", "", """
{"nombre":"Ceviche","precio":25.50,"categoria":"Fondos"}
""");
        assertEquals(201, created.statusCode(), created.body());
        var matcher = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(created.body());
        assertTrue(matcher.find(), created.body());
        String path = "/"+matcher.group(1);
        assertEquals(200, request("GET", path, "").statusCode());
        assertEquals(200, request("GET", "", "").statusCode());
        var updated = request("PUT", path, """
{"nombre":"Ceviche","precio":30.50,"categoria":"Fondos"}
""");
        assertEquals(200, updated.statusCode(), updated.body());
        
        assertEquals(200, request("DELETE", path, "").statusCode());
        assertEquals(404, request("GET", path, "").statusCode());
        assertEquals(404, request("PUT", path, """
{"nombre":"Ceviche","precio":25.50,"categoria":"Fondos"}
""").statusCode());
        assertEquals(404, request("DELETE", path, "").statusCode());
    }
}

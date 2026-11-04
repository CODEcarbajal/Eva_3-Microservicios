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
class MicroserviciosPedidoApplicationTests {
    @Value("${local.server.port}") int port;
    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> request(String method, String path, String body) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/pedidos"+path))
            .timeout(Duration.ofSeconds(10)).header("Content-Type", "application/json")
            .method(method, HttpRequest.BodyPublishers.ofString(body)).build(),
            HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void crudValidacionYNoEncontrado() throws Exception {
        assertEquals(400, request("POST", "", "{}").statusCode());
        var created = request("POST", "", """
{"fecha":"2026-11-04","idCliente":1,"detalles":[{"idPlato":1,"cantidad":2,"precio":25.50}]}
""");
        assertEquals(201, created.statusCode(), created.body());
        var matcher = Pattern.compile("\"idPedido\"\\s*:\\s*(\\d+)").matcher(created.body());
        assertTrue(matcher.find(), created.body());
        String path = "/"+matcher.group(1);
        assertEquals(200, request("GET", path, "").statusCode());
        assertEquals(200, request("GET", "", "").statusCode());
        var updated = request("PUT", path, """
{"fecha":"2026-11-04","idCliente":1,"detalles":[{"idPlato":1,"cantidad":2,"precio":30.50}]}
""");
        assertEquals(200, updated.statusCode(), updated.body());
        
        assertTrue(created.body().contains("\"total\":51.0"));
        assertTrue(updated.body().contains("\"total\":61.0"));
        assertFalse(updated.body().contains("\"pedido\":"));

        assertEquals(200, request("DELETE", path, "").statusCode());
        assertEquals(404, request("GET", path, "").statusCode());
        assertEquals(404, request("PUT", path, """
{"fecha":"2026-11-04","idCliente":1,"detalles":[{"idPlato":1,"cantidad":2,"precio":25.50}]}
""").statusCode());
        assertEquals(404, request("DELETE", path, "").statusCode());
    }
}

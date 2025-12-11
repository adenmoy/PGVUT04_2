import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ServidorApi {
    public static void main(String[] args) throws IOException {
        // Crear servidor HTTP en el puerto 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Endpoint GET /api/hola
        server.createContext("/api/hola", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) throws IOException {
                String respuesta = "{\"mensaje\":\"Hola mundo\"}";
                t.getResponseHeaders().add("Content-Type", "application/json");
                t.sendResponseHeaders(200, respuesta.length());
                OutputStream os = t.getResponseBody();
                os.write(respuesta.getBytes());
                os.close();
            }
        });

        // Endpoint GET /api/usuarios
        server.createContext("/api/usuarios", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) throws IOException {
                String respuesta = "[\"Ana\", \"Pedro\", \"Luis\"]";
                t.getResponseHeaders().add("Content-Type", "application/json");
                t.sendResponseHeaders(200, respuesta.length());
                OutputStream os = t.getResponseBody();
                os.write(respuesta.getBytes());
                os.close();
            }
        });

        server.start();
        System.out.println("Servidor iniciado en http://localhost:8080/");
    }
}

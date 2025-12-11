import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServidorApiCrudDemo {

    private static List<String> usuarios = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        usuarios.add("Ana");
        usuarios.add("Pedro");
        usuarios.add("Luis");

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/usuarios", new UsuarioHandler());
        server.start();

        System.out.println("API REST en http://localhost:8080/api/usuarios");
    }

    static class UsuarioHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            String path = t.getRequestURI().getPath();

            // GET /api/usuarios
            if (method.equals("GET") && path.equals("/api/usuarios")) {
                responder(t, listaUsuarios());
                return;
            }

            // POST /api/usuarios  -> body con nombre
            if (method.equals("POST") && path.equals("/api/usuarios")) {
                String body = leerBody(t);
                usuarios.add(body.trim());
                responder(t, "{\"status\":\"creado\", \"usuario\":\"" + body + "\"}");
                return;
            }

            // PUT /api/usuarios/{id}
            if (method.equals("PUT") && path.matches("/api/usuarios/\\d+")) {
                int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
                if (id >= usuarios.size()) {
                    responder(t, "{\"error\":\"no existe\"}", 404);
                    return;
                }
                String body = leerBody(t);
                usuarios.set(id, body.trim());
                responder(t, "{\"status\":\"actualizado\"}");
                return;
            }

            // DELETE /api/usuarios/{id}
            if (method.equals("DELETE") && path.matches("/api/usuarios/\\d+")) {
                int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
                if (id >= usuarios.size()) {
                    responder(t, "{\"error\":\"no existe\"}", 404);
                    return;
                }
                usuarios.remove(id);
                responder(t, "{\"status\":\"eliminado\"}");
                return;
            }

            

            responder(t, "{\"error\":\"ruta no encontrada\"}", 404);
        }
    }

    private static String listaUsuarios() {
        return usuarios.toString()
                .replace("[", "[\"")
                .replace("]", "\"]")
                .replace(", ", "\",\"");
    }

    private static String leerBody(HttpExchange t) throws IOException {
        InputStream is = t.getRequestBody();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void responder(HttpExchange t, String respuesta) throws IOException {
        responder(t, respuesta, 200);
    }

    private static void responder(HttpExchange t, String respuesta, int status) throws IOException {
        t.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(status, bytes.length);
        OutputStream os = t.getResponseBody();
        os.write(bytes);
        os.close();
    }
}

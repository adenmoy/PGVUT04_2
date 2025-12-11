import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
//import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;



public class ServidorApiCrud {

    private static List<Usuario> usuarios = new ArrayList<>();
    private static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {

        // Datos iniciales
        usuarios.add(new Usuario("Ana", "1234"));
        usuarios.add(new Usuario("Pedro", "abcd"));
        usuarios.add(new Usuario("Luis", "9999"));

        // Servidor HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/usuarios", new UsuarioHandler());
        server.start();

        System.out.println("API CRUD en http://localhost:8080/api/usuarios");
    }

    // =========================
    // HANDLER PRINCIPAL CRUD
    // =========================
    static class UsuarioHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {

        String method = t.getRequestMethod();
        String path = t.getRequestURI().getPath();

        // GET /api/usuarios  (lista completa)
        if (method.equals("GET") && path.equals("/api/usuarios")) {
            responder(t, gson.toJson(usuarios));
            return;
        }

        // GET /api/usuarios/{id}
        if (method.equals("GET") && path.matches("/api/usuarios/\\d+")) {
            int id = extraerId(path);

            if (!existe(id)) {
                responder(t, "{\"error\":\"usuario no existe\"}", 404);
                return;
            }

            Usuario u = usuarios.get(id);
            responder(t, gson.toJson(u));
            return;
        }

        // POST /api/usuarios
        if (method.equals("POST") && path.equals("/api/usuarios")) {
            String body = leerBody(t);
            Usuario nuevo = gson.fromJson(body, Usuario.class);
            usuarios.add(nuevo);
            responder(t, "{\"status\":\"creado\"}");
            return;
        }

        // PUT /api/usuarios/{id}
        if (method.equals("PUT") && path.matches("/api/usuarios/\\d+")) {
            int id = extraerId(path);

            if (!existe(id)) {
                responder(t, "{\"error\":\"usuario no existe\"}", 404);
                return;
            }

            Usuario editado = gson.fromJson(leerBody(t), Usuario.class);
            usuarios.set(id, editado);
            responder(t, "{\"status\":\"actualizado\"}");
            return;
        }

        // DELETE /api/usuarios/{id}
        if (method.equals("DELETE") && path.matches("/api/usuarios/\\d+")) {
            int id = extraerId(path);

            if (!existe(id)) {
                responder(t, "{\"error\":\"usuario no existe\"}", 404);
                return;
            }

            usuarios.remove(id);
            responder(t, "{\"status\":\"eliminado\"}");
            return;
        }

        // Rutas no válidas
        responder(t, "{\"error\":\"ruta no encontrada\"}", 404);
    }
}

    // =========================
    //     FUNCIONES UTILIDAD
    // =========================

    private static boolean existe(int id) {
        return id >= 0 && id < usuarios.size();
    }

    private static int extraerId(String path) {
        return Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
    }

    private static String leerBody(HttpExchange t) throws IOException {
        return new String(t.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void responder(HttpExchange t, String respuesta) throws IOException {
        responder(t, respuesta, 200);
    }

    private static void responder(HttpExchange t, String respuesta, int status) throws IOException {
        t.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(bytes);
        }
    }
}

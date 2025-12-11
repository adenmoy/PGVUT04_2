import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



public class ClienteApiCrud {

    private static final String BASE_URL = "http://localhost:8080/api/usuarios";
    private static HttpClient client = HttpClient.newHttpClient();
    private static Gson gson = new Gson();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== CLIENTE CRUD USUARIOS ===");
            System.out.println("1. Listar usuarios");
            System.out.println("2. Obtener usuario por ID");
            System.out.println("3. Crear usuario");
            System.out.println("4. Actualizar usuario");
            System.out.println("5. Eliminar usuario");

            System.out.println("9. Salir");
            System.out.print("Opción: ");

            int op = Integer.parseInt(sc.nextLine());

            try {
                switch (op) {
                    case 1 -> listar();
                    case 2 -> obtenerUno(sc);
                    case 3 -> crear(sc);
                    case 4 -> actualizar(sc);
                    case 5 -> eliminar(sc);
                    
                    case 9 -> {
                        System.out.println("Saliendo...");
                        return;
                    }
                    default -> System.out.println("Opción inválida");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // =========================
    //   OPERACIONES CRUD
    // =========================

    private static void listar() throws Exception {
        System.out.println("\n"+BASE_URL);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        var res = client.send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() == 200) {
            List<Usuario> lista = gson.fromJson(res.body(),
                    new TypeToken<List<Usuario>>(){}.getType());

            System.out.println("\nUsuarios:");
            lista.forEach(System.out::println);
        } else {
            System.out.println("Código: " + res.statusCode());
            System.out.println(res.body());
        }
    }

    private static void obtenerUno(Scanner sc) throws Exception {
        System.out.print("ID: ");
        int id = Integer.parseInt(sc.nextLine());

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        var res = client.send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() == 200) {
            Usuario u = gson.fromJson(res.body(), Usuario.class);
            System.out.println("\n" + u);
        } else {
            System.out.println("Código: " + res.statusCode());
            System.out.println(res.body());
        }
    }

    private static void crear(Scanner sc) throws Exception {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Clave: ");
        String clave = sc.nextLine();

        Usuario u = new Usuario();
        u.nombre = nombre;
        u.clave = clave;

        String json = gson.toJson(u);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        mostrarRespuestaBruta(res);
    }

    private static void actualizar(Scanner sc) throws Exception {
        System.out.print("ID: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Nueva clave: ");
        String clave = sc.nextLine();

        Usuario u = new Usuario();
        u.nombre = nombre;
        u.clave = clave;

        String json = gson.toJson(u);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        mostrarRespuestaBruta(res);
    }

    private static void eliminar(Scanner sc) throws Exception {
        System.out.print("ID: ");
        int id = Integer.parseInt(sc.nextLine());

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        mostrarRespuestaBruta(res);
    }

    // =========================
    //   UTILIDAD
    // =========================

    private static void mostrarRespuestaBruta(HttpResponse<String> res) {
        System.out.println("\nCódigo: " + res.statusCode());
        System.out.println("Respuesta:");
        System.out.println(res.body());
    }
}

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.google.gson.Gson;

// ==========================
//   MODELOS JSON
// ==========================
class Pokemon {
    String name;
    int height;
    int weight;
    List<TypeSlot> types;
}

class TypeSlot {
    int slot;
    Type type;
}

class Type {
    String name;
}

// ==========================
//   CLIENTE POKEAPI
// ==========================
public class PokeClient {

    private static final String BASE_URL = "https://pokeapi.co/api/v2/pokemon/";
    private final HttpClient client = HttpClient.newHttpClient();

    private final Gson gson = new Gson();

    public Pokemon getPokemon(String name) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + name.toLowerCase()))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error API: " + response.statusCode());
        }

        return gson.fromJson(response.body(), Pokemon.class);
    }

    public static void main(String[] args) throws Exception {
        String PokeNombre = "ditto";

        PokeClient api = new PokeClient();
  
        Pokemon p = api.getPokemon(PokeNombre);

        System.out.println("Nombre: " + p.name);
        System.out.println("Altura: " + p.height);
        System.out.println("Peso: " + p.weight);

        System.out.println("Tipos:");
        for (TypeSlot ts : p.types) {
            System.out.println(" - " + ts.type.name);
        }
    }
}

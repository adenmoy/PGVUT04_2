public class Usuario {
    public String nombre;
    public String clave; // Más adelante en UT05: cifrada

        public Usuario() {
        this.nombre = "";
        this.clave = "";
    }

    public Usuario(String nombre, String clave) {
        this.nombre = nombre;
        this.clave = clave;
    }

    @Override
    public String toString() {
        return "Usuario{nombre='" + nombre + "', clave='" + clave + "'}";
    }
}

package ieselrincon.es.model;

// Representa una categoría de impacto ambiental (por ejemplo "Energía", "Transporte")
public class CategoriaImpacto {

    // Identificador único de la categoría
    private int id;

    // Nombre de la categoría
    private String nombre;

    // Descripción explicativa de la categoría
    private String descripcion;

    // Constructor con todos los campos
    public CategoriaImpacto(int id, String nombre, String descripcion) {
        this.id          = id;
        this.nombre      = nombre;
        this.descripcion = descripcion;
    }

    // Devuelve el id de la categoría
    public int getId() { return id; }

    // Devuelve el nombre de la categoría
    public String getNombre() { return nombre; }

    // Devuelve la descripción de la categoría
    public String getDescripcion() { return descripcion; }

    // Cambia el nombre de la categoría
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Cambia la descripción de la categoría
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // Muestra la categoría como texto legible
    @Override
    public String toString() {
        return nombre;
    }
}

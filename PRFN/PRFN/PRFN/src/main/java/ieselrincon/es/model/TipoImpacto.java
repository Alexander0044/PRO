package ieselrincon.es.model;

// Representa un tipo concreto de impacto ambiental (por ejemplo "Electricidad en kWh")
// Cada TipoImpacto pertenece a una CategoriaImpacto
public class TipoImpacto {

    // Identificador único del tipo de impacto
    private int id;

    // Nombre del tipo de impacto (por ejemplo "Electricidad")
    private String nombre;

    // Unidad de medida del consumo (por ejemplo "kWh", "km", "kg")
    private String unidad;

    // Categoría a la que pertenece este tipo de impacto
    private CategoriaImpacto categoria;

    // Constructor con todos los campos
    public TipoImpacto(int id, String nombre, String unidad, CategoriaImpacto categoria) {
        this.id        = id;
        this.nombre    = nombre;
        this.unidad    = unidad;
        this.categoria = categoria;
    }

    // Devuelve el id del tipo de impacto
    public int getId() { return id; }

    // Devuelve el nombre del tipo de impacto
    public String getNombre() { return nombre; }

    // Devuelve la unidad de medida
    public String getUnidad() { return unidad; }

    // Devuelve la categoría a la que pertenece
    public CategoriaImpacto getCategoria() { return categoria; }

    // Cambia el nombre del tipo de impacto
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Cambia la unidad de medida
    public void setUnidad(String unidad) { this.unidad = unidad; }

    // Cambia la categoría a la que pertenece
    public void setCategoria(CategoriaImpacto categoria) { this.categoria = categoria; }

    // Muestra el tipo de impacto como texto legible
    @Override
    public String toString() {
        return nombre + " (" + unidad + ")";
    }
}

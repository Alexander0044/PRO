package ieselrincon.es.model;

import java.util.HashSet;
import java.util.Set;

// Representa un grupo de alumnos (por ejemplo, "Grupo A" con su docente)
public class Grupo {

    // Identificador único del grupo
    private int id;

    // Nombre del grupo (por ejemplo "Grupo A")
    private String nombre;

    // Descripción opcional del grupo
    private String descripcion;

    // Nombre del docente responsable
    private String docente;

    // Conjunto de IDs de los alumnos que pertenecen a este grupo
    private final Set<Integer> alumnosIds = new HashSet<>();

    // Constructor con todos los campos
    public Grupo(int id, String nombre, String descripcion, String docente) {
        this.id          = id;
        this.nombre      = nombre;
        this.descripcion = descripcion;
        this.docente     = docente;
    }

    // Devuelve el id del grupo
    public int getId() { return id; }

    // Asigna el id (lo usa el DAO al recuperar el id generado por MySQL)
    public void setId(int id) { this.id = id; }

    // Devuelve el nombre del grupo
    public String getNombre() { return nombre; }

    // Devuelve la descripción del grupo
    public String getDescripcion() { return descripcion; }

    // Devuelve el nombre del docente responsable
    public String getDocente() { return docente; }

    // Devuelve el conjunto de IDs de alumnos del grupo
    public Set<Integer> getAlumnosIds() { return alumnosIds; }

    // Cambia el nombre del grupo
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Cambia la descripción del grupo
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // Cambia el docente responsable
    public void setDocente(String docente) { this.docente = docente; }

    // Muestra el grupo como texto legible
    @Override
    public String toString() {
        return nombre + (docente != null && !docente.isBlank() ? " — " + docente : "");
    }
}
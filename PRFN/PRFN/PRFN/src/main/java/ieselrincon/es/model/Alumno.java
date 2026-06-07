package ieselrincon.es.model;

// Representa a un alumno del sistema
public class Alumno {

    // Identificador único del alumno
    private int id;

    // Nombre y apellidos del alumno
    private String nombre;
    private String apellidos;

    // Correo electrónico del alumno
    private String email;

    // Número de matrícula (único por alumno)
    private String matricula;

    // Grupo al que pertenece el alumno (puede ser null si no está asignado)
    private Grupo grupo;

    // Constructor con todos los campos
    public Alumno(int id, String nombre, String apellidos, String email, String matricula) {
        this.id        = id;
        this.nombre    = nombre;
        this.apellidos = apellidos;
        this.email     = email;
        this.matricula = matricula;
    }

    // Devuelve el id del alumno
    public int getId() { return id; }

    // Asigna el id (lo usa el DAO al recuperar el id generado por MySQL)
    public void setId(int id) { this.id = id; }

    // Devuelve el nombre del alumno
    public String getNombre() { return nombre; }

    // Devuelve los apellidos del alumno
    public String getApellidos() { return apellidos; }

    // Devuelve el email del alumno
    public String getEmail() { return email; }

    // Devuelve la matrícula del alumno
    public String getMatricula() { return matricula; }

    // Devuelve el grupo del alumno
    public Grupo getGrupo() { return grupo; }

    // Cambia el nombre del alumno
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Cambia los apellidos del alumno
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    // Cambia el email del alumno
    public void setEmail(String email) { this.email = email; }

    // Cambia la matrícula del alumno
    public void setMatricula(String matricula) { this.matricula = matricula; }

    // Asigna el alumno a un grupo
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }

    // Muestra el alumno como texto legible
    @Override
    public String toString() {
        return apellidos + ", " + nombre + " (" + matricula + ")";
    }
}
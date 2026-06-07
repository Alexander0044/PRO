package ieselrincon.es.service;

import ieselrincon.es.dao.AlumnoDAO;
import ieselrincon.es.model.Alumno;

import java.sql.SQLException;
import java.util.List;

public class AlumnoService {

    private final AlumnoDAO alumnoDAO = new AlumnoDAO();

    // Crea un alumno nuevo, lo valida y lo guarda en MySQL a través del DAO
    public Alumno crearAlumno(String nombre, String apellidos, String email, String matricula) {
        validarCamposObligatorios(nombre, apellidos, matricula);
        try {
            if (alumnoDAO.buscarPorMatricula(matricula) != null) {
                throw new IllegalArgumentException("Ya existe un alumno con la matrícula: " + matricula);
            }
            // id=0 porque MySQL lo asigna automáticamente al insertar
            Alumno alumno = new Alumno(0, nombre, apellidos, email, matricula);
            alumnoDAO.insertar(alumno);
            return alumno;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear el alumno: " + e.getMessage(), e);
        }
    }

    // Devuelve el alumno con ese id, o null si no existe
    public Alumno obtenerAlumno(int id) {
        try {
            return alumnoDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el alumno: " + e.getMessage(), e);
        }
    }

    // Devuelve todos los alumnos de la base de datos
    public List<Alumno> listarAlumnos() {
        try {
            return alumnoDAO.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar alumnos: " + e.getMessage(), e);
        }
    }

    // Actualiza los datos de un alumno existente y los guarda en MySQL
    public Alumno actualizarAlumno(int id, String nombre, String apellidos, String email, String matricula) {
        validarCamposObligatorios(nombre, apellidos, matricula);
        try {
            Alumno alumno = alumnoDAO.buscarPorId(id);
            if (alumno == null) {
                throw new IllegalArgumentException("Alumno no encontrado con id: " + id);
            }
            // Comprobamos que la matrícula no la tenga otro alumno diferente
            Alumno otro = alumnoDAO.buscarPorMatricula(matricula);
            if (otro != null && otro.getId() != id) {
                throw new IllegalArgumentException("La matrícula " + matricula + " ya está en uso.");
            }
            alumno.setNombre(nombre);
            alumno.setApellidos(apellidos);
            alumno.setEmail(email);
            alumno.setMatricula(matricula);
            alumnoDAO.actualizar(alumno);
            return alumno;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el alumno: " + e.getMessage(), e);
        }
    }

    // Elimina el alumno con ese id de la base de datos
    public boolean eliminarAlumno(int id) {
        try {
            return alumnoDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el alumno: " + e.getMessage(), e);
        }
    }

    // Busca alumnos cuyo nombre o apellidos contengan el texto indicado
    public List<Alumno> buscarPorNombre(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El texto de búsqueda no puede estar vacío.");
        }
        try {
            return alumnoDAO.buscarPorNombre(texto);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar alumnos: " + e.getMessage(), e);
        }
    }

    // Busca un alumno por su número de matrícula exacto
    public Alumno buscarPorMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("La matrícula no puede estar vacía.");
        }
        try {
            return alumnoDAO.buscarPorMatricula(matricula);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por matrícula: " + e.getMessage(), e);
        }
    }

    private void validarCamposObligatorios(String nombre, String apellidos, String matricula) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del alumno es obligatorio.");
        }
        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException("Los apellidos del alumno son obligatorios.");
        }
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("La matrícula del alumno es obligatoria.");
        }
    }
}
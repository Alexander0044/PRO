package ieselrincon.es.service;

import ieselrincon.es.dao.AlumnoDAO;
import ieselrincon.es.dao.GrupoDAO;
import ieselrincon.es.model.Alumno;
import ieselrincon.es.model.Grupo;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class GrupoService {

    private final GrupoDAO  grupoDAO  = new GrupoDAO();
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();

    // Crea un grupo nuevo, lo valida y lo guarda en MySQL a través del DAO
    public Grupo crearGrupo(String nombre, String descripcion, String docente) {
        validarNombre(nombre);
        try {
            if (grupoDAO.existeNombre(nombre)) {
                throw new IllegalArgumentException("Ya existe un grupo con el nombre: " + nombre);
            }
            // id=0 porque MySQL lo asigna automáticamente al insertar
            Grupo grupo = new Grupo(0, nombre, descripcion, docente);
            grupoDAO.insertar(grupo);
            return grupo;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear el grupo: " + e.getMessage(), e);
        }
    }

    // Devuelve el grupo con ese id, o null si no existe
    public Grupo obtenerGrupo(int id) {
        try {
            return grupoDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el grupo: " + e.getMessage(), e);
        }
    }

    // Devuelve todos los grupos de la base de datos
    public List<Grupo> listarGrupos() {
        try {
            return grupoDAO.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar grupos: " + e.getMessage(), e);
        }
    }

    // Actualiza los datos de un grupo existente y los guarda en MySQL
    public Grupo actualizarGrupo(int id, String nombre, String descripcion, String docente) {
        validarNombre(nombre);
        try {
            Grupo grupo = grupoDAO.buscarPorId(id);
            if (grupo == null) {
                throw new IllegalArgumentException("Grupo no encontrado con id: " + id);
            }
            List<Grupo> todos = grupoDAO.listarTodos();
            boolean nombreOcupado = todos.stream()
                    .anyMatch(g -> g.getId() != id && g.getNombre().equalsIgnoreCase(nombre));
            if (nombreOcupado) {
                throw new IllegalArgumentException("El nombre '" + nombre + "' ya está en uso por otro grupo.");
            }
            grupo.setNombre(nombre);
            grupo.setDescripcion(descripcion);
            grupo.setDocente(docente);
            grupoDAO.actualizar(grupo);
            return grupo;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el grupo: " + e.getMessage(), e);
        }
    }

    // Elimina el grupo con ese id de la base de datos
    public boolean eliminarGrupo(int id) {
        try {
            return grupoDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el grupo: " + e.getMessage(), e);
        }
    }

    // Asigna un alumno a un grupo actualizando su grupo_id en la base de datos
    public void agregarAlumno(int grupoId, int alumnoId) {
        try {
            Grupo grupo = grupoDAO.buscarPorId(grupoId);
            if (grupo == null) {
                throw new IllegalArgumentException("Grupo no encontrado con id: " + grupoId);
            }
            Alumno alumno = alumnoDAO.buscarPorId(alumnoId);
            if (alumno == null) {
                throw new IllegalArgumentException("Alumno no encontrado con id: " + alumnoId);
            }
            if (alumno.getGrupo() != null && alumno.getGrupo().getId() == grupoId) {
                throw new IllegalArgumentException("El alumno " + alumnoId + " ya pertenece al grupo.");
            }
            alumno.setGrupo(grupo);
            alumnoDAO.actualizar(alumno);
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar alumno al grupo: " + e.getMessage(), e);
        }
    }

    // Retira un alumno de su grupo dejando grupo_id en NULL en la base de datos
    public boolean retirarAlumno(int grupoId, int alumnoId) {
        try {
            Alumno alumno = alumnoDAO.buscarPorId(alumnoId);
            if (alumno == null) return false;
            if (alumno.getGrupo() == null || alumno.getGrupo().getId() != grupoId) return false;
            alumno.setGrupo(null);
            alumnoDAO.actualizar(alumno);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al retirar alumno del grupo: " + e.getMessage(), e);
        }
    }

    // Devuelve todos los alumnos que pertenecen a un grupo concreto
    public List<Alumno> obtenerAlumnosDeGrupo(int grupoId) {
        try {
            return alumnoDAO.listarTodos().stream()
                    .filter(a -> a.getGrupo() != null && a.getGrupo().getId() == grupoId)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener alumnos del grupo: " + e.getMessage(), e);
        }
    }

    // Devuelve el grupo al que pertenece un alumno, o null si no tiene grupo asignado
    public Grupo grupoDeAlumno(int alumnoId) {
        try {
            Alumno alumno = alumnoDAO.buscarPorId(alumnoId);
            if (alumno == null || alumno.getGrupo() == null) return null;
            // Cargamos el grupo completo desde la BD para tener nombre y docente
            return grupoDAO.buscarPorId(alumno.getGrupo().getId());
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el grupo del alumno: " + e.getMessage(), e);
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del grupo es obligatorio.");
        }
    }
}
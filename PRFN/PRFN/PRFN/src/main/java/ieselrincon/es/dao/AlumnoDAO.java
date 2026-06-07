package ieselrincon.es.dao;

import ieselrincon.es.model.Alumno;
import ieselrincon.es.model.Grupo;
import ieselrincon.es.util.HibernateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Clase encargada de guardar y recuperar alumnos en la base de datos MySQL
public class AlumnoDAO {

    // Guarda un alumno nuevo en la base de datos y le asigna el id generado
    public void insertar(Alumno alumno) throws SQLException {
        String sql = "INSERT INTO alumnos (nombre, apellidos, email, matricula, grupo_id) VALUES (?, ?, ?, ?, ?)";
        // prepareStatement con RETURN_GENERATED_KEYS para obtener el id asignado por MySQL
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellidos());
            ps.setString(3, alumno.getEmail());
            ps.setString(4, alumno.getMatricula());
            // Si el alumno tiene grupo lo guardamos, si no ponemos NULL
            if (alumno.getGrupo() != null) {
                ps.setInt(5, alumno.getGrupo().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.executeUpdate();
            // Recuperamos el id que MySQL asignó automáticamente y lo metemos en el objeto
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) alumno.setId(rs.getInt(1));
            }
        }
    }

    // Actualiza los datos de un alumno que ya existe en la base de datos
    public void actualizar(Alumno alumno) throws SQLException {
        String sql = "UPDATE alumnos SET nombre=?, apellidos=?, email=?, matricula=?, grupo_id=? WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellidos());
            ps.setString(3, alumno.getEmail());
            ps.setString(4, alumno.getMatricula());
            if (alumno.getGrupo() != null) {
                ps.setInt(5, alumno.getGrupo().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setInt(6, alumno.getId());
            ps.executeUpdate();
        }
    }

    // Elimina el alumno con el id indicado de la base de datos
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM alumnos WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            // executeUpdate devuelve el número de filas afectadas; si es > 0 existía
            return ps.executeUpdate() > 0;
        }
    }

    // Devuelve el alumno con ese id, o null si no existe
    public Alumno buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM alumnos WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // Devuelve todos los alumnos de la base de datos
    public List<Alumno> listarTodos() throws SQLException {
        List<Alumno> lista = new ArrayList<>();
        String sql = "SELECT * FROM alumnos ORDER BY apellidos, nombre";
        try (Statement st = HibernateUtil.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // Busca alumnos cuyo nombre o apellidos contengan el texto indicado
    public List<Alumno> buscarPorNombre(String texto) throws SQLException {
        List<Alumno> lista = new ArrayList<>();
        String sql = "SELECT * FROM alumnos WHERE nombre LIKE ? OR apellidos LIKE ? ORDER BY apellidos, nombre";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            // El % es el comodín de SQL: busca cualquier texto que contenga la palabra
            String filtro = "%" + texto + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Busca un alumno por su número de matrícula exacto
    public Alumno buscarPorMatricula(String matricula) throws SQLException {
        String sql = "SELECT * FROM alumnos WHERE matricula=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // Convierte una fila del ResultSet en un objeto Alumno
    private Alumno mapear(ResultSet rs) throws SQLException {
        Alumno alumno = new Alumno(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellidos"),
                rs.getString("email"),
                rs.getString("matricula")
        );
        // Si tiene grupo_id lo cargamos como objeto Grupo básico (solo con el id)
        int grupoId = rs.getInt("grupo_id");
        if (!rs.wasNull()) {
            alumno.setGrupo(new Grupo(grupoId, "", "", ""));
        }
        return alumno;
    }
}
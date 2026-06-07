package ieselrincon.es.dao;

import ieselrincon.es.model.Grupo;
import ieselrincon.es.util.HibernateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Clase encargada de guardar y recuperar grupos en la base de datos MySQL
public class GrupoDAO {

    // Guarda un grupo nuevo en la base de datos y le asigna el id generado
    public void insertar(Grupo grupo) throws SQLException {
        String sql = "INSERT INTO grupos (nombre, descripcion, docente) VALUES (?, ?, ?)";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, grupo.getNombre());
            ps.setString(2, grupo.getDescripcion());
            ps.setString(3, grupo.getDocente());
            ps.executeUpdate();
            // Recuperamos el id asignado por MySQL y lo guardamos en el objeto
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) grupo.setId(rs.getInt(1));
            }
        }
    }

    // Actualiza los datos de un grupo que ya existe
    public void actualizar(Grupo grupo) throws SQLException {
        String sql = "UPDATE grupos SET nombre=?, descripcion=?, docente=? WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setString(1, grupo.getNombre());
            ps.setString(2, grupo.getDescripcion());
            ps.setString(3, grupo.getDocente());
            ps.setInt(4, grupo.getId());
            ps.executeUpdate();
        }
    }

    // Elimina el grupo con el id indicado
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM grupos WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Devuelve el grupo con ese id, o null si no existe
    public Grupo buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM grupos WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // Devuelve todos los grupos de la base de datos
    public List<Grupo> listarTodos() throws SQLException {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT * FROM grupos ORDER BY nombre";
        try (Statement st = HibernateUtil.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // Comprueba si ya existe un grupo con ese nombre
    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM grupos WHERE nombre=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Convierte una fila del ResultSet en un objeto Grupo
    private Grupo mapear(ResultSet rs) throws SQLException {
        return new Grupo(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getString("docente")
        );
    }
}
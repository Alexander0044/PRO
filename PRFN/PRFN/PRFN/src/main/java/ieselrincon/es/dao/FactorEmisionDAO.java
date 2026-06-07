package ieselrincon.es.dao;

import ieselrincon.es.model.FactorEmision;
import ieselrincon.es.model.TipoImpacto;
import ieselrincon.es.util.HibernateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Clase encargada de guardar y recuperar factores de emisión en MySQL
public class FactorEmisionDAO {

    // Guarda un factor de emisión nuevo en la base de datos
    public void insertar(FactorEmision factor) throws SQLException {
        String sql = "INSERT INTO factores_emision (nombre, valor, tipo_impacto_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, factor.getNombre());
            ps.setDouble(2, factor.getValor());
            ps.setInt(3, factor.getTipoImpacto().getId());
            ps.executeUpdate();
            // Guardamos el id generado por MySQL en el objeto
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) factor.setId(rs.getInt(1));
            }
        }
    }

    // Actualiza un factor de emisión existente
    public void actualizar(FactorEmision factor) throws SQLException {
        String sql = "UPDATE factores_emision SET nombre=?, valor=?, tipo_impacto_id=? WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setString(1, factor.getNombre());
            ps.setDouble(2, factor.getValor());
            ps.setInt(3, factor.getTipoImpacto().getId());
            ps.setInt(4, factor.getId());
            ps.executeUpdate();
        }
    }

    // Elimina el factor con el id indicado
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM factores_emision WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Devuelve el factor con ese id, o null si no existe
    public FactorEmision buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM factores_emision WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // Devuelve todos los factores del tipo de impacto indicado
    public List<FactorEmision> listarPorTipoImpacto(TipoImpacto tipoImpacto) throws SQLException {
        List<FactorEmision> lista = new ArrayList<>();
        String sql = "SELECT * FROM factores_emision WHERE tipo_impacto_id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, tipoImpacto.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Devuelve todos los factores de emisión de la base de datos
    public List<FactorEmision> listarTodos() throws SQLException {
        List<FactorEmision> lista = new ArrayList<>();
        String sql = "SELECT * FROM factores_emision ORDER BY nombre";
        try (Statement st = HibernateUtil.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // Convierte una fila del ResultSet en un objeto FactorEmision
    private FactorEmision mapear(ResultSet rs) throws SQLException {
        // Creamos un TipoImpacto con solo el id para mantener la referencia
        TipoImpacto tipo = new TipoImpacto(rs.getInt("tipo_impacto_id"), "", "", null);
        return new FactorEmision(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getDouble("valor"),
                tipo
        );
    }
}
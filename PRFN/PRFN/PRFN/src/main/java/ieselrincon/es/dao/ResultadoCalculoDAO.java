package ieselrincon.es.dao;

import ieselrincon.es.model.Alumno;
import ieselrincon.es.model.ResultadoCalculo;
import ieselrincon.es.util.HibernateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Clase encargada de guardar y recuperar resultados de cálculo de huella en MySQL
public class ResultadoCalculoDAO {

    // Guarda un resultado de cálculo nuevo en la base de datos
    public void insertar(ResultadoCalculo resultado) throws SQLException {
        String sql = """
                INSERT INTO resultados_calculo
                (alumno_id, fecha, huella_carbono, huella_electricidad, huella_gas_natural,
                 huella_mov_privada, huella_mov_publica, huella_vuelos, huella_alimentacion, huella_residuos)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, resultado.getAlumno().getId());
            ps.setDate(2, Date.valueOf(resultado.getFecha()));
            ps.setDouble(3, resultado.getHuellaCarbono());
            ps.setDouble(4, resultado.getHuellaElectricidad());
            ps.setDouble(5, resultado.getHuellaGasNatural());
            ps.setDouble(6, resultado.getHuellaMovilidadPrivada());
            ps.setDouble(7, resultado.getHuellaMovilidadPublica());
            ps.setDouble(8, resultado.getHuellaVuelos());
            ps.setDouble(9, resultado.getHuellaAlimentacion());
            ps.setDouble(10, resultado.getHuellaResiduos());
            ps.executeUpdate();
            // Guardamos el id generado por MySQL en el objeto
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) resultado.setId(rs.getInt(1));
            }
        }
    }

    // Devuelve el resultado con ese id, o null si no existe
    public ResultadoCalculo buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM resultados_calculo WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // Devuelve todos los resultados de un alumno, ordenados del más reciente al más antiguo
    public List<ResultadoCalculo> listarPorAlumno(int alumnoId) throws SQLException {
        List<ResultadoCalculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM resultados_calculo WHERE alumno_id=? ORDER BY fecha DESC";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Devuelve todos los resultados de la base de datos
    public List<ResultadoCalculo> listarTodos() throws SQLException {
        List<ResultadoCalculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM resultados_calculo ORDER BY fecha DESC";
        try (Statement st = HibernateUtil.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // Elimina el resultado con el id indicado
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM resultados_calculo WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Convierte una fila del ResultSet en un objeto ResultadoCalculo
    private ResultadoCalculo mapear(ResultSet rs) throws SQLException {
        // Creamos un Alumno con solo el id para mantener la referencia
        Alumno alumno = new Alumno(rs.getInt("alumno_id"), "", "", "", "");
        return new ResultadoCalculo(
                rs.getInt("id"),
                alumno,
                rs.getDate("fecha").toLocalDate(),
                rs.getDouble("huella_electricidad"),
                rs.getDouble("huella_gas_natural"),
                rs.getDouble("huella_mov_privada"),
                rs.getDouble("huella_mov_publica"),
                rs.getDouble("huella_vuelos"),
                rs.getDouble("huella_alimentacion"),
                rs.getDouble("huella_residuos")
        );
    }
}
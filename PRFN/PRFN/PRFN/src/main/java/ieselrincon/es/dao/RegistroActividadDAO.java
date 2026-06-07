package ieselrincon.es.dao;

import ieselrincon.es.model.Alumno;
import ieselrincon.es.model.RegistroActividad;
import ieselrincon.es.util.HibernateUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Clase encargada de guardar y recuperar registros de actividad en MySQL
public class RegistroActividadDAO {

    // Guarda un registro nuevo en la base de datos
    public void insertar(RegistroActividad r) throws SQLException {
        String sql = """
                INSERT INTO registros_actividad
                (alumno_id, fecha, consumo_electricidad, consumo_gas_natural,
                 km_coche_privado, km_transporte_publico, vuelos_cortos, vuelos_largos,
                 consumo_carne, residuos_kg)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getAlumno().getId());
            // Convertimos LocalDate a java.sql.Date que entiende MySQL
            ps.setDate(2, Date.valueOf(r.getFecha()));
            ps.setDouble(3, r.getConsumoElectricidad());
            ps.setDouble(4, r.getConsumoGasNatural());
            ps.setDouble(5, r.getKmCochePrivado());
            ps.setDouble(6, r.getKmTransportePublico());
            ps.setDouble(7, r.getVuelosCortos());
            ps.setDouble(8, r.getVuelosLargos());
            ps.setDouble(9, r.getConsumoCarne());
            ps.setDouble(10, r.getResiduosKg());
            ps.executeUpdate();
            // Guardamos el id generado por MySQL en el objeto
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
        }
    }

    // Actualiza los valores de consumo de un registro existente
    public void actualizar(RegistroActividad r) throws SQLException {
        String sql = """
                UPDATE registros_actividad SET
                consumo_electricidad=?, consumo_gas_natural=?,
                km_coche_privado=?, km_transporte_publico=?,
                vuelos_cortos=?, vuelos_largos=?,
                consumo_carne=?, residuos_kg=?, huella_total=?
                WHERE id=?
                """;
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setDouble(1, r.getConsumoElectricidad());
            ps.setDouble(2, r.getConsumoGasNatural());
            ps.setDouble(3, r.getKmCochePrivado());
            ps.setDouble(4, r.getKmTransportePublico());
            ps.setDouble(5, r.getVuelosCortos());
            ps.setDouble(6, r.getVuelosLargos());
            ps.setDouble(7, r.getConsumoCarne());
            ps.setDouble(8, r.getResiduosKg());
            // La huella puede ser null si todavía no se ha calculado
            if (r.getHuellaTotal() != null) {
                ps.setDouble(9, r.getHuellaTotal());
            } else {
                ps.setNull(9, Types.DOUBLE);
            }
            ps.setInt(10, r.getId());
            ps.executeUpdate();
        }
    }

    // Guarda el resultado de la huella calculada en un registro concreto
    public void guardarHuella(int registroId, double huellaTotal) throws SQLException {
        String sql = "UPDATE registros_actividad SET huella_total=? WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setDouble(1, huellaTotal);
            ps.setInt(2, registroId);
            ps.executeUpdate();
        }
    }

    // Elimina el registro con el id indicado
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM registros_actividad WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Devuelve el registro con ese id, o null si no existe
    public RegistroActividad buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM registros_actividad WHERE id=?";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // Devuelve todos los registros de un alumno concreto, ordenados por fecha
    public List<RegistroActividad> listarPorAlumno(int alumnoId) throws SQLException {
        List<RegistroActividad> lista = new ArrayList<>();
        String sql = "SELECT * FROM registros_actividad WHERE alumno_id=? ORDER BY fecha";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Devuelve los registros entre dos fechas (ambas inclusive), ordenados por fecha
    public List<RegistroActividad> listarPorRangoFechas(LocalDate desde, LocalDate hasta) throws SQLException {
        List<RegistroActividad> lista = new ArrayList<>();
        String sql = "SELECT * FROM registros_actividad WHERE fecha BETWEEN ? AND ? ORDER BY fecha";
        try (PreparedStatement ps = HibernateUtil.getConexion().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Devuelve todos los registros de la base de datos
    public List<RegistroActividad> listarTodos() throws SQLException {
        List<RegistroActividad> lista = new ArrayList<>();
        String sql = "SELECT * FROM registros_actividad ORDER BY fecha";
        try (Statement st = HibernateUtil.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // Convierte una fila del ResultSet en un objeto RegistroActividad
    private RegistroActividad mapear(ResultSet rs) throws SQLException {
        // Creamos un Alumno con solo el id para mantener la referencia sin cargar todos sus datos
        Alumno alumno = new Alumno(rs.getInt("alumno_id"), "", "", "", "");
        RegistroActividad r = new RegistroActividad(
                rs.getInt("id"),
                alumno,
                rs.getDate("fecha").toLocalDate(),
                rs.getDouble("consumo_electricidad"),
                rs.getDouble("consumo_gas_natural"),
                rs.getDouble("km_coche_privado"),
                rs.getDouble("km_transporte_publico"),
                rs.getDouble("vuelos_cortos"),
                rs.getDouble("vuelos_largos"),
                rs.getDouble("consumo_carne"),
                rs.getDouble("residuos_kg")
        );
        // La huella puede ser NULL en la base de datos si aún no se calculó
        double huella = rs.getDouble("huella_total");
        if (!rs.wasNull()) r.setHuellaTotal(huella);
        return r;
    }
}
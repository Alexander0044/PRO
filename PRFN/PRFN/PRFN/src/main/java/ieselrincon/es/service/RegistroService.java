package ieselrincon.es.service;

import ieselrincon.es.dao.RegistroActividadDAO;
import ieselrincon.es.model.Alumno;
import ieselrincon.es.model.RegistroActividad;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RegistroService {

    private final RegistroActividadDAO registroDAO = new RegistroActividadDAO();

    // Crea un registro de actividad nuevo y lo guarda en MySQL a través del DAO
    public RegistroActividad crearRegistro(int alumnoId, LocalDate fecha,
                                           double consumoElectricidad, double consumoGasNatural,
                                           double kmCochePrivado, double kmTransportePublico,
                                           double vuelosCortos, double vuelosLargos,
                                           double consumoCarne, double residuosKg) {
        if (alumnoId <= 0) throw new IllegalArgumentException("alumnoId inválido.");
        if (fecha == null)  throw new IllegalArgumentException("La fecha del registro es obligatoria.");
        try {
            // Creamos el Alumno con solo el id; el DAO solo necesita la referencia para guardar alumno_id
            Alumno alumno = new Alumno(alumnoId, "", "", "", "");
            RegistroActividad r = new RegistroActividad(
                    0, alumno, fecha,
                    consumoElectricidad, consumoGasNatural,
                    kmCochePrivado, kmTransportePublico,
                    vuelosCortos, vuelosLargos,
                    consumoCarne, residuosKg
            );
            registroDAO.insertar(r);
            return r;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear el registro: " + e.getMessage(), e);
        }
    }

    // Devuelve el registro con ese id, o null si no existe
    public RegistroActividad obtenerRegistro(int id) {
        try {
            return registroDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el registro: " + e.getMessage(), e);
        }
    }

    // Devuelve todos los registros de la base de datos
    public List<RegistroActividad> listarRegistros() {
        try {
            return registroDAO.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar registros: " + e.getMessage(), e);
        }
    }

    // Devuelve los registros de un alumno concreto, ordenados por fecha
    public List<RegistroActividad> listarPorAlumno(int alumnoId) {
        try {
            return registroDAO.listarPorAlumno(alumnoId);
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar registros del alumno: " + e.getMessage(), e);
        }
    }

    // Devuelve los registros comprendidos entre dos fechas (ambas inclusive)
    public List<RegistroActividad> listarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Las fechas de rango son obligatorias.");
        }
        try {
            return registroDAO.listarPorRangoFechas(desde, hasta);
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar registros por fechas: " + e.getMessage(), e);
        }
    }

    // Actualiza los valores de consumo de un registro existente y los guarda en MySQL
    public RegistroActividad actualizarRegistro(int id,
                                                double consumoElectricidad, double consumoGasNatural,
                                                double kmCochePrivado, double kmTransportePublico,
                                                double vuelosCortos, double vuelosLargos,
                                                double consumoCarne, double residuosKg) {
        try {
            RegistroActividad r = registroDAO.buscarPorId(id);
            if (r == null) {
                throw new IllegalArgumentException("Registro no encontrado con id: " + id);
            }
            r.setConsumoElectricidad(consumoElectricidad);
            r.setConsumoGasNatural(consumoGasNatural);
            r.setKmCochePrivado(kmCochePrivado);
            r.setKmTransportePublico(kmTransportePublico);
            r.setVuelosCortos(vuelosCortos);
            r.setVuelosLargos(vuelosLargos);
            r.setConsumoCarne(consumoCarne);
            r.setResiduosKg(residuosKg);
            // Al actualizar los datos de consumo, la huella queda obsoleta
            r.setHuellaTotal(null);
            registroDAO.actualizar(r);
            return r;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el registro: " + e.getMessage(), e);
        }
    }

    // Guarda el resultado de la huella calculada en un registro concreto
    public void guardarHuella(int registroId, double huellaTotal) {
        try {
            registroDAO.guardarHuella(registroId, huellaTotal);
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la huella: " + e.getMessage(), e);
        }
    }

    // Elimina el registro con ese id de la base de datos
    public boolean eliminarRegistro(int id) {
        try {
            return registroDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el registro: " + e.getMessage(), e);
        }
    }
}
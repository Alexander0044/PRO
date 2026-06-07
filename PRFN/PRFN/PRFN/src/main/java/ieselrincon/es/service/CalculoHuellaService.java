package ieselrincon.es.service;

import ieselrincon.es.dao.ResultadoCalculoDAO;
import ieselrincon.es.model.Alumno;
import ieselrincon.es.model.RegistroActividad;
import ieselrincon.es.model.ResultadoCalculo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalculoHuellaService {

    private static final double FE_ELECTRICIDAD       = 0.233;
    private static final double FE_GAS_NATURAL        = 2.204;
    private static final double FE_COCHE_KM           = 0.170;
    private static final double FE_TRANSPORTE_PUBLICO = 0.089;
    private static final double FE_VUELO_CORTO        = 255.0;
    private static final double FE_VUELO_LARGO        = 1350.0;
    private static final double FE_CARNE_SEMANAL      = 7.2;
    private static final double FE_RESIDUOS_SEMANAL   = 0.5;

    private static final int SEMANAS_ANO = 52;
    private static final int MESES_ANO   = 12;

    private final ResultadoCalculoDAO resultadoDAO = new ResultadoCalculoDAO();

    public static class ResultadoHuella {
        public final double huellaElectricidad;
        public final double huellaGasNatural;
        public final double huellaMovilidadPrivada;
        public final double huellaMovilidadPublica;
        public final double huellaVuelos;
        public final double huellaAlimentacion;
        public final double huellaResiduos;
        public final double huellaTotal;

        public ResultadoHuella(double electricidad, double gas, double movilPriv,
                               double movilPub, double vuelos, double alim, double residuos) {
            this.huellaElectricidad     = electricidad;
            this.huellaGasNatural       = gas;
            this.huellaMovilidadPrivada = movilPriv;
            this.huellaMovilidadPublica = movilPub;
            this.huellaVuelos           = vuelos;
            this.huellaAlimentacion     = alim;
            this.huellaResiduos         = residuos;
            this.huellaTotal            = electricidad + gas + movilPriv
                    + movilPub + vuelos + alim + residuos;
        }

        @Override
        public String toString() {
            return String.format(
                    "Huella total: %.2f kg CO2/anio%n" +
                            "  Electricidad:         %.2f%n" +
                            "  Gas natural:          %.2f%n" +
                            "  Movilidad (privada):  %.2f%n" +
                            "  Movilidad (publica):  %.2f%n" +
                            "  Vuelos:               %.2f%n" +
                            "  Alimentacion:         %.2f%n" +
                            "  Residuos:             %.2f%n",
                    huellaTotal, huellaElectricidad, huellaGasNatural,
                    huellaMovilidadPrivada, huellaMovilidadPublica,
                    huellaVuelos, huellaAlimentacion, huellaResiduos);
        }
    }

    // Calcula la huella de un registro, guarda la huella_total en registros_actividad,
    // persiste el desglose completo en resultados_calculo y devuelve el resultado
    public ResultadoHuella calcular(RegistroActividad registro,
                                    RegistroService registroService) {
        if (registro == null) throw new IllegalArgumentException("El registro no puede ser nulo.");
        ResultadoHuella resultado = calcularDesdeValores(
                registro.getConsumoElectricidad(),
                registro.getConsumoGasNatural(),
                registro.getKmCochePrivado(),
                registro.getKmTransportePublico(),
                registro.getVuelosCortos(),
                registro.getVuelosLargos(),
                registro.getConsumoCarne(),
                registro.getResiduosKg()
        );
        // Guardamos la huella total en el registro de actividad
        registroService.guardarHuella(registro.getId(), resultado.huellaTotal);
        // Persistimos el desglose completo en resultados_calculo
        persistirResultado(registro.getAlumno(), resultado);
        return resultado;
    }

    // Calcula la huella directamente a partir de los valores de consumo, sin persistir
    public ResultadoHuella calcularDesdeValores(double consumoElectricidad,
                                                double consumoGasNatural,
                                                double kmCochePrivado,
                                                double kmTransportePublico,
                                                double vuelosCortos,
                                                double vuelosLargos,
                                                double consumoCarne,
                                                double residuosKg) {
        double electricidad = consumoElectricidad * FE_ELECTRICIDAD       * MESES_ANO;
        double gas          = consumoGasNatural   * FE_GAS_NATURAL        * MESES_ANO;
        double movilPriv    = kmCochePrivado      * FE_COCHE_KM           * SEMANAS_ANO;
        double movilPub     = kmTransportePublico * FE_TRANSPORTE_PUBLICO * SEMANAS_ANO;
        double vuelos       = vuelosCortos * FE_VUELO_CORTO + vuelosLargos * FE_VUELO_LARGO;
        double alim         = consumoCarne * FE_CARNE_SEMANAL             * SEMANAS_ANO;
        double residuos     = residuosKg   * FE_RESIDUOS_SEMANAL         * SEMANAS_ANO;
        return new ResultadoHuella(electricidad, gas, movilPriv, movilPub, vuelos, alim, residuos);
    }

    // Devuelve todos los resultados guardados de un alumno, ordenados del más reciente al más antiguo
    public List<ResultadoCalculo> listarResultadosPorAlumno(int alumnoId) {
        try {
            return resultadoDAO.listarPorAlumno(alumnoId);
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar resultados del alumno: " + e.getMessage(), e);
        }
    }

    // Devuelve todos los resultados de la base de datos
    public List<ResultadoCalculo> listarTodosLosResultados() {
        try {
            return resultadoDAO.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar todos los resultados: " + e.getMessage(), e);
        }
    }

    // Calcula el porcentaje de cada categoría respecto al total
    public Map<String, Double> calcularPorcentajes(ResultadoHuella resultado) {
        Map<String, Double> pct = new LinkedHashMap<>();
        double total = resultado.huellaTotal;
        if (total == 0) return pct;
        pct.put("Electricidad",      pct100(resultado.huellaElectricidad,     total));
        pct.put("Gas natural",       pct100(resultado.huellaGasNatural,       total));
        pct.put("Movilidad privada", pct100(resultado.huellaMovilidadPrivada, total));
        pct.put("Movilidad publica", pct100(resultado.huellaMovilidadPublica, total));
        pct.put("Vuelos",            pct100(resultado.huellaVuelos,           total));
        pct.put("Alimentacion",      pct100(resultado.huellaAlimentacion,     total));
        pct.put("Residuos",          pct100(resultado.huellaResiduos,         total));
        return pct;
    }

    // Guarda el desglose completo de un cálculo en la tabla resultados_calculo
    private void persistirResultado(Alumno alumno, ResultadoHuella resultado) {
        try {
            ResultadoCalculo rc = new ResultadoCalculo(
                    0, alumno, LocalDate.now(),
                    resultado.huellaElectricidad,
                    resultado.huellaGasNatural,
                    resultado.huellaMovilidadPrivada,
                    resultado.huellaMovilidadPublica,
                    resultado.huellaVuelos,
                    resultado.huellaAlimentacion,
                    resultado.huellaResiduos
            );
            resultadoDAO.insertar(rc);
        } catch (SQLException e) {
            throw new RuntimeException("Error al persistir el resultado de cálculo: " + e.getMessage(), e);
        }
    }

    private double pct100(double parte, double total) {
        return Math.round((parte / total) * 10_000.0) / 100.0;
    }
}
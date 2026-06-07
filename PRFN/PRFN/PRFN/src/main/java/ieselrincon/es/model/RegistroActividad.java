package ieselrincon.es.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Representa un registro de consumo introducido por un alumno en una fecha concreta
public class RegistroActividad {

    // Identificador único del registro
    private int id;

    // Alumno que realizó el registro
    private Alumno alumno;

    // Fecha a la que corresponde el consumo registrado
    private LocalDate fecha;

    // Fecha y hora exacta en que se creó el registro en el sistema
    private LocalDateTime creadoEn;

    // Consumo eléctrico mensual en kWh
    private double consumoElectricidad;

    // Consumo de gas natural mensual en m³
    private double consumoGasNatural;

    // Kilómetros recorridos en coche privado por semana
    private double kmCochePrivado;

    // Kilómetros recorridos en transporte público por semana
    private double kmTransportePublico;

    // Número de vuelos cortos realizados en el año
    private double vuelosCortos;

    // Número de vuelos largos realizados en el año
    private double vuelosLargos;

    // Consumo de carne semanal en kg
    private double consumoCarne;

    // Residuos generados por semana en kg
    private double residuosKg;

    // Resultado de la huella total calculada (null si aún no se ha calculado)
    private Double huellaTotal;

    // Constructor con todos los datos de consumo
    public RegistroActividad(int id, Alumno alumno, LocalDate fecha,
                             double consumoElectricidad, double consumoGasNatural,
                             double kmCochePrivado, double kmTransportePublico,
                             double vuelosCortos, double vuelosLargos,
                             double consumoCarne, double residuosKg) {
        this.id                  = id;
        this.alumno              = alumno;
        this.fecha               = fecha;
        this.creadoEn            = LocalDateTime.now();
        this.consumoElectricidad = consumoElectricidad;
        this.consumoGasNatural   = consumoGasNatural;
        this.kmCochePrivado      = kmCochePrivado;
        this.kmTransportePublico = kmTransportePublico;
        this.vuelosCortos        = vuelosCortos;
        this.vuelosLargos        = vuelosLargos;
        this.consumoCarne        = consumoCarne;
        this.residuosKg          = residuosKg;
    }

    // Getters
    public int getId()                   { return id; }

    // Asigna el id (lo usa el DAO al recuperar el id generado por MySQL)
    public void setId(int id)            { this.id = id; }
    public Alumno getAlumno()            { return alumno; }
    public LocalDate getFecha()          { return fecha; }
    public LocalDateTime getCreadoEn()   { return creadoEn; }
    public double getConsumoElectricidad() { return consumoElectricidad; }
    public double getConsumoGasNatural()   { return consumoGasNatural; }
    public double getKmCochePrivado()      { return kmCochePrivado; }
    public double getKmTransportePublico() { return kmTransportePublico; }
    public double getVuelosCortos()        { return vuelosCortos; }
    public double getVuelosLargos()        { return vuelosLargos; }
    public double getConsumoCarne()        { return consumoCarne; }
    public double getResiduosKg()          { return residuosKg; }
    public Double getHuellaTotal()         { return huellaTotal; }

    // Setters para actualizar los valores de consumo
    public void setConsumoElectricidad(double v) { this.consumoElectricidad = v; }
    public void setConsumoGasNatural(double v)   { this.consumoGasNatural   = v; }
    public void setKmCochePrivado(double v)      { this.kmCochePrivado      = v; }
    public void setKmTransportePublico(double v) { this.kmTransportePublico = v; }
    public void setVuelosCortos(double v)        { this.vuelosCortos        = v; }
    public void setVuelosLargos(double v)        { this.vuelosLargos        = v; }
    public void setConsumoCarne(double v)        { this.consumoCarne        = v; }
    public void setResiduosKg(double v)          { this.residuosKg          = v; }

    // Guarda el resultado de la huella calculada en este registro
    public void setHuellaTotal(Double huellaTotal) { this.huellaTotal = huellaTotal; }

    // Muestra el registro como texto legible
    @Override
    public String toString() {
        return "Registro#" + id + " [alumno=" + (alumno != null ? alumno.getMatricula() : "?")
                + ", fecha=" + fecha
                + (huellaTotal != null ? ", huella=" + String.format("%.2f", huellaTotal) + " kg CO₂" : "")
                + "]";
    }
}
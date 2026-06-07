package ieselrincon.es.model;

import java.time.LocalDate;

// Representa el resultado del cálculo de huella de carbono de un alumno en una fecha
public class ResultadoCalculo {

    // Identificador único del resultado
    private int id;

    // Alumno al que pertenece este resultado
    private Alumno alumno;

    // Huella de carbono total calculada en kg CO2 al año
    private double huellaCarbono;

    // Fecha en que se realizó el cálculo
    private LocalDate fecha;

    // Desglose por categorías (cada valor en kg CO2/año)
    private double huellaElectricidad;
    private double huellaGasNatural;
    private double huellaMovilidadPrivada;
    private double huellaMovilidadPublica;
    private double huellaVuelos;
    private double huellaAlimentacion;
    private double huellaResiduos;

    // Constructor con el desglose completo
    public ResultadoCalculo(int id, Alumno alumno, LocalDate fecha,
                            double electricidad, double gas, double movilPrivada,
                            double movilPublica, double vuelos, double alimentacion,
                            double residuos) {
        this.id                   = id;
        this.alumno               = alumno;
        this.fecha                = fecha;
        this.huellaElectricidad   = electricidad;
        this.huellaGasNatural     = gas;
        this.huellaMovilidadPrivada = movilPrivada;
        this.huellaMovilidadPublica = movilPublica;
        this.huellaVuelos         = vuelos;
        this.huellaAlimentacion   = alimentacion;
        this.huellaResiduos       = residuos;
        // La huella total es la suma de todas las categorías
        this.huellaCarbono = electricidad + gas + movilPrivada + movilPublica
                + vuelos + alimentacion + residuos;
    }

    // Devuelve el id del resultado
    public int getId() { return id; }

    // Asigna el id (lo usa el DAO al recuperar el id generado por MySQL)
    public void setId(int id) { this.id = id; }

    // Devuelve el alumno propietario del resultado
    public Alumno getAlumno() { return alumno; }

    // Devuelve la huella total en kg CO2/año
    public double getHuellaCarbono() { return huellaCarbono; }

    // Devuelve la fecha del cálculo
    public LocalDate getFecha() { return fecha; }

    // Getters del desglose por categoría
    public double getHuellaElectricidad()    { return huellaElectricidad; }
    public double getHuellaGasNatural()      { return huellaGasNatural; }
    public double getHuellaMovilidadPrivada() { return huellaMovilidadPrivada; }
    public double getHuellaMovilidadPublica() { return huellaMovilidadPublica; }
    public double getHuellaVuelos()          { return huellaVuelos; }
    public double getHuellaAlimentacion()    { return huellaAlimentacion; }
    public double getHuellaResiduos()        { return huellaResiduos; }

    // Muestra el resultado como texto legible
    @Override
    public String toString() {
        return String.format("ResultadoCalculo#%d [alumno=%s, huella=%.2f kg CO2/año, fecha=%s]",
                id, alumno != null ? alumno.getMatricula() : "?", huellaCarbono, fecha);
    }
}
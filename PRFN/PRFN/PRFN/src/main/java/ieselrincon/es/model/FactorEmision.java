package ieselrincon.es.model;

// Representa el factor de emisión que convierte una cantidad de consumo a kg de CO2
// Por ejemplo: 1 kWh de electricidad → 0.233 kg CO2
public class FactorEmision {

    // Identificador único del factor de emisión
    private int id;

    // Nombre descriptivo del factor (por ejemplo "Factor electricidad España 2023")
    private String nombre;

    // Valor numérico del factor (kg CO2 por unidad de consumo)
    private double valor;

    // Tipo de impacto al que aplica este factor
    private TipoImpacto tipoImpacto;

    // Constructor con todos los campos
    public FactorEmision(int id, String nombre, double valor, TipoImpacto tipoImpacto) {
        this.id          = id;
        this.nombre      = nombre;
        this.valor       = valor;
        this.tipoImpacto = tipoImpacto;
    }

    // Devuelve el id del factor
    public int getId() { return id; }

    // Asigna el id (lo usa el DAO al recuperar el id generado por MySQL)
    public void setId(int id) { this.id = id; }

    // Devuelve el nombre del factor
    public String getNombre() { return nombre; }

    // Devuelve el valor del factor (kg CO2 por unidad)
    public double getValor() { return valor; }

    // Devuelve el tipo de impacto asociado
    public TipoImpacto getTipoImpacto() { return tipoImpacto; }

    // Cambia el nombre del factor
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Cambia el valor del factor
    public void setValor(double valor) { this.valor = valor; }

    // Cambia el tipo de impacto asociado
    public void setTipoImpacto(TipoImpacto tipoImpacto) { this.tipoImpacto = tipoImpacto; }

    // Muestra el factor como texto legible
    @Override
    public String toString() {
        return nombre + " = " + valor + " kg CO2/" + (tipoImpacto != null ? tipoImpacto.getUnidad() : "?");
    }
}
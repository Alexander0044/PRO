package ieselrincon.es.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoriaService {

    public enum Categoria {
        MUY_BAJA("Muy baja", "🌿", "#2ecc71"),
        BAJA    ("Baja",     "🌱", "#27ae60"),
        MEDIA   ("Media",    "🌤", "#f39c12"),
        ALTA    ("Alta",     "🔥", "#e67e22"),
        MUY_ALTA("Muy alta", "💀", "#e74c3c");

        private final String etiqueta;
        private final String emoji;
        private final String colorHex;

        Categoria(String etiqueta, String emoji, String colorHex) {
            this.etiqueta = etiqueta;
            this.emoji    = emoji;
            this.colorHex = colorHex;
        }

        public String getEtiqueta() { return etiqueta; }
        public String getEmoji()    { return emoji; }
        public String getColorHex() { return colorHex; }

        @Override
        public String toString() { return emoji + " " + etiqueta; }
    }

    private static final double UMBRAL_MUY_BAJA = 2_000;
    private static final double UMBRAL_BAJA     = 4_000;
    private static final double UMBRAL_MEDIA    = 7_000;
    private static final double UMBRAL_ALTA     = 12_000;

    public Categoria clasificar(double huellaKgAnio) {
        if (huellaKgAnio < 0) throw new IllegalArgumentException("La huella no puede ser negativa.");
        if (huellaKgAnio < UMBRAL_MUY_BAJA) return Categoria.MUY_BAJA;
        if (huellaKgAnio < UMBRAL_BAJA)     return Categoria.BAJA;
        if (huellaKgAnio < UMBRAL_MEDIA)    return Categoria.MEDIA;
        if (huellaKgAnio < UMBRAL_ALTA)     return Categoria.ALTA;
        return Categoria.MUY_ALTA;
    }

    public Categoria clasificar(CalculoHuellaService.ResultadoHuella resultado) {
        if (resultado == null) throw new IllegalArgumentException("El resultado no puede ser nulo.");
        return clasificar(resultado.huellaTotal);
    }

    public List<String> obtenerRecomendaciones(Categoria categoria,
                                               CalculoHuellaService.ResultadoHuella resultado) {
        List<String> recomendaciones = new ArrayList<>();
        switch (categoria) {
            case MUY_BAJA -> recomendaciones.add("Excelente! Tu huella esta muy por debajo de la media.");
            case BAJA     -> recomendaciones.add("Tu huella es baja. Con pequenos ajustes podrias alcanzar el nivel optimo.");
            case MEDIA    -> recomendaciones.add("Tu huella es similar a la media espanola. Hay margen de mejora.");
            case ALTA     -> recomendaciones.add("Tu huella supera la media nacional. Actua prioritariamente en tus principales focos.");
            case MUY_ALTA -> recomendaciones.add("Tu huella es muy elevada. Es importante adoptar cambios significativos.");
        }
        if (resultado != null) {
            Map<String, Double> focos = obtenerFocosOrdenados(resultado);
            int i = 0;
            for (Map.Entry<String, Double> entry : focos.entrySet()) {
                if (i++ >= 3) break;
                recomendaciones.add(recomendacionPorFoco(entry.getKey()));
            }
        }
        return recomendaciones;
    }

    public Map<Categoria, String> obtenerDescripcionCategorias() {
        Map<Categoria, String> mapa = new LinkedHashMap<>();
        mapa.put(Categoria.MUY_BAJA, "Menos de 2.000 kg CO2/anio");
        mapa.put(Categoria.BAJA,     "Entre 2.000 y 4.000 kg CO2/anio");
        mapa.put(Categoria.MEDIA,    "Entre 4.000 y 7.000 kg CO2/anio");
        mapa.put(Categoria.ALTA,     "Entre 7.000 y 12.000 kg CO2/anio");
        mapa.put(Categoria.MUY_ALTA, "Mas de 12.000 kg CO2/anio");
        return mapa;
    }

    private Map<String, Double> obtenerFocosOrdenados(CalculoHuellaService.ResultadoHuella r) {
        Map<String, Double> focos = new LinkedHashMap<>();
        focos.put("Electricidad",      r.huellaElectricidad);
        focos.put("Gas natural",       r.huellaGasNatural);
        focos.put("Movilidad privada", r.huellaMovilidadPrivada);
        focos.put("Movilidad publica", r.huellaMovilidadPublica);
        focos.put("Vuelos",            r.huellaVuelos);
        focos.put("Alimentacion",      r.huellaAlimentacion);
        focos.put("Residuos",          r.huellaResiduos);
        return focos.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    private String recomendacionPorFoco(String foco) {
        return switch (foco) {
            case "Electricidad"      -> "Reduce tu consumo electrico: usa LEDs y desconecta aparatos en standby.";
            case "Gas natural"       -> "Mejora el aislamiento de tu hogar y considera una bomba de calor.";
            case "Movilidad privada" -> "Reduce el uso del coche: comparte viajes o usa transporte publico.";
            case "Movilidad publica" -> "Valora desplazamientos a pie o en bici para tramos cortos.";
            case "Vuelos"            -> "Sustituye vuelos cortos por tren cuando sea posible.";
            case "Alimentacion"      -> "Reduce el consumo de carne roja y aumenta alimentos de origen vegetal.";
            case "Residuos"          -> "Separa tus residuos correctamente y reduce el desperdicio alimentario.";
            default                  -> "Revisa tus habitos de consumo e intenta reducir tu impacto ambiental.";
        };
    }
}
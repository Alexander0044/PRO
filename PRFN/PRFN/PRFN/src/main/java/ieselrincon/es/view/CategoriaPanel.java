package ieselrincon.es.view;

import ieselrincon.es.service.CategoriaService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class CategoriaPanel extends JPanel {

    private final CategoriaService categoriaService;

    public CategoriaPanel(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(crearEncabezado(),      BorderLayout.NORTH);
        add(crearTablaCategorias(), BorderLayout.CENTER);
        add(crearPanelContexto(),   BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Categorias de Huella de Carbono");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        JLabel subtitulo = new JLabel(
                "<html>Clasificacion basada en kg de CO2 equivalente por año.<br>" +
                        "Media española de referencia: <b>~7.600 kg CO2/año</b> &nbsp;|&nbsp; " +
                        "Objetivo Paris 2050: <b>&lt;2.000 kg CO2/año</b></html>");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(Color.DARK_GRAY);
        panel.add(titulo,    BorderLayout.NORTH);
        panel.add(subtitulo, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane crearTablaCategorias() {
        String[] columnas = {"", "Categoria", "Umbral (kg CO2/año)", "Descripcion"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        Map<CategoriaService.Categoria, String> descripciones =
                categoriaService.obtenerDescripcionCategorias();

        for (Map.Entry<CategoriaService.Categoria, String> entry : descripciones.entrySet()) {
            CategoriaService.Categoria cat = entry.getKey();
            modelo.addRow(new Object[]{
                    cat.getEmoji(),
                    cat.getEtiqueta(),
                    obtenerUmbral(cat),
                    entry.getValue()
            });
        }

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(36);
        tabla.setEnabled(false);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(400);

        FilaCategoriaRenderer renderer = new FilaCategoriaRenderer();
        for (int c = 0; c < tabla.getColumnCount(); c++) {
            tabla.getColumnModel().getColumn(c).setCellRenderer(renderer);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Tabla de clasificacion"));
        return scroll;
    }

    private JPanel crearPanelContexto() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Consejos generales"));

        String[] consejos = {
                "Elige una tarifa electrica de energia 100% renovable.",
                "Sustituye desplazamientos en coche por bicicleta o transporte publico.",
                "Evita vuelos innecesarios; opta por el tren en distancias cortas.",
                "Reduce el consumo de carne roja y productos de origen animal.",
                "Separa correctamente tus residuos y minimiza el desperdicio alimentario.",
                "Mejora el aislamiento termico de tu hogar.",
                "Cambia todas las bombillas a LED y desenchufa aparatos en standby.",
                "Consume productos locales y de temporada."
        };

        JPanel listaConsejos = new JPanel(new GridLayout(0, 2, 12, 4));
        listaConsejos.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        for (String consejo : consejos) {
            JLabel lbl = new JLabel("• " + consejo);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            listaConsejos.add(lbl);
        }

        JLabel nota = new JLabel(
                "<html><i>Factores de emision: IPCC / DEFRA · Mix electrico Espana 2023 (REE)</i></html>");
        nota.setFont(new Font("SansSerif", Font.ITALIC, 10));
        nota.setForeground(Color.GRAY);
        nota.setBorder(BorderFactory.createEmptyBorder(4, 8, 0, 8));

        panel.add(listaConsejos, BorderLayout.CENTER);
        panel.add(nota,          BorderLayout.SOUTH);
        return panel;
    }

    private String obtenerUmbral(CategoriaService.Categoria cat) {
        return switch (cat) {
            case MUY_BAJA -> "< 2.000";
            case BAJA     -> "2.000 - 4.000";
            case MEDIA    -> "4.000 - 7.000";
            case ALTA     -> "7.000 - 12.000";
            case MUY_ALTA -> "> 12.000";
        };
    }

    private static class FilaCategoriaRenderer extends DefaultTableCellRenderer {
        private static final Color[] COLORES_FILA = {
                new Color(0xd5f5e3),
                new Color(0xa9dfbf),
                new Color(0xfdebd0),
                new Color(0xf5cba7),
                new Color(0xfadbd8)
        };

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(new Font("SansSerif", Font.PLAIN, 13));
            if (!isSelected && row >= 0 && row < COLORES_FILA.length) {
                setBackground(COLORES_FILA[row]);
            } else if (isSelected) {
                setBackground(table.getSelectionBackground());
            }
            setHorizontalAlignment(column == 0 ? CENTER : LEFT);
            return this;
        }
    }
}
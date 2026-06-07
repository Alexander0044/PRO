package ieselrincon.es.view;

import ieselrincon.es.model.Alumno;
import ieselrincon.es.model.RegistroActividad;
import ieselrincon.es.service.AlumnoService;
import ieselrincon.es.service.CalculoHuellaService;
import ieselrincon.es.service.CategoriaService;
import ieselrincon.es.service.RegistroService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ResultadosPanel extends JPanel {

    private final RegistroService      registroService;
    private final CalculoHuellaService calculoHuellaService;
    private final CategoriaService     categoriaService;
    private final AlumnoService        alumnoService;

    private JTable            tablaResumen;
    private DefaultTableModel modeloResumen;
    private JTextArea         areaDetalle;

    private JComboBox<String> cmbFiltroCategoria;
    private JComboBox<Alumno> cmbFiltroAlumno;

    private static final String[] COLS = {
            "Alumno", "Matricula", "Ultimo registro", "Huella (kg CO2/año)", "Categoria"
    };
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ResultadosPanel(RegistroService registroService,
                           CalculoHuellaService calculoHuellaService,
                           CategoriaService categoriaService,
                           AlumnoService alumnoService) {
        this.registroService      = registroService;
        this.calculoHuellaService = calculoHuellaService;
        this.categoriaService     = categoriaService;
        this.alumnoService        = alumnoService;
        initUI();
        refrescar();
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(crearPanelFiltros(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
    }

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros"));

        cmbFiltroAlumno = new JComboBox<>();
        cmbFiltroAlumno.setPreferredSize(new Dimension(220, 26));
        cmbFiltroCategoria = new JComboBox<>(new String[]{
                "Todas las categorias",
                CategoriaService.Categoria.MUY_BAJA.toString(),
                CategoriaService.Categoria.BAJA.toString(),
                CategoriaService.Categoria.MEDIA.toString(),
                CategoriaService.Categoria.ALTA.toString(),
                CategoriaService.Categoria.MUY_ALTA.toString()
        });

        JButton btnAplicar   = new JButton("Filtrar");
        JButton btnRefrescar = new JButton("Refrescar");

        btnAplicar.addActionListener(e   -> aplicarFiltros());
        btnRefrescar.addActionListener(e -> refrescar());

        panel.add(new JLabel("Alumno:"));
        panel.add(cmbFiltroAlumno);
        panel.add(new JLabel("  Categoria:"));
        panel.add(cmbFiltroCategoria);
        panel.add(btnAplicar);
        panel.add(btnRefrescar);
        return panel;
    }

    private JSplitPane crearPanelCentral() {
        modeloResumen = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaResumen = new JTable(modeloResumen);
        tablaResumen.setRowHeight(24);
        tablaResumen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaResumen.getTableHeader().setReorderingAllowed(false);
        tablaResumen.getColumnModel().getColumn(0).setPreferredWidth(160);
        tablaResumen.getColumnModel().getColumn(1).setPreferredWidth(90);
        tablaResumen.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaResumen.getColumnModel().getColumn(3).setPreferredWidth(140);
        tablaResumen.getColumnModel().getColumn(4).setPreferredWidth(110);
        tablaResumen.getColumnModel().getColumn(4).setCellRenderer(new CategoriaRenderer());
        tablaResumen.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarDetalle();
        });

        JScrollPane scrollTabla = new JScrollPane(tablaResumen);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Resumen de huellas por alumno"));

        areaDetalle = new JTextArea(10, 35);
        areaDetalle.setEditable(false);
        areaDetalle.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaDetalle.setText("Selecciona un alumno para ver el desglose.");
        JScrollPane scrollDetalle = new JScrollPane(areaDetalle);
        scrollDetalle.setBorder(BorderFactory.createTitledBorder("Desglose del ultimo registro"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, scrollDetalle);
        split.setResizeWeight(0.65);
        return split;
    }

    public void refrescar() {
        Object selAnterior = cmbFiltroAlumno.getSelectedItem();
        cmbFiltroAlumno.removeAllItems();
        // El primer item null representa "todos los alumnos"
        cmbFiltroAlumno.addItem(null);
        for (Alumno a : alumnoService.listarAlumnos()) {
            cmbFiltroAlumno.addItem(a);
        }
        cmbFiltroAlumno.setSelectedItem(selAnterior);
        cargarTabla(alumnoService.listarAlumnos());
    }

    private void aplicarFiltros() {
        Alumno alumnoFiltro       = (Alumno) cmbFiltroAlumno.getSelectedItem();
        String categoriaFiltro    = (String) cmbFiltroCategoria.getSelectedItem();
        List<Alumno> alumnos = alumnoFiltro != null
                ? List.of(alumnoFiltro)
                : alumnoService.listarAlumnos();
        cargarTabla(alumnos, categoriaFiltro);
    }

    private void cargarTabla(List<Alumno> alumnos) {
        cargarTabla(alumnos, "Todas las categorias");
    }

    private void cargarTabla(List<Alumno> alumnos, String categoriaFiltro) {
        modeloResumen.setRowCount(0);
        for (Alumno alumno : alumnos) {
            List<RegistroActividad> registros = registroService.listarPorAlumno(alumno.getId());
            if (registros.isEmpty()) continue;
            // Buscamos el último registro que ya tenga huella calculada
            RegistroActividad ultimo = registros.stream()
                    .filter(r -> r.getHuellaTotal() != null)
                    .reduce((a, b) -> b)
                    .orElse(null);
            if (ultimo == null) continue;
            CategoriaService.Categoria cat = categoriaService.clasificar(ultimo.getHuellaTotal());
            if (!"Todas las categorias".equals(categoriaFiltro)
                    && !cat.toString().equals(categoriaFiltro)) continue;
            modeloResumen.addRow(new Object[]{
                    alumno.getNombre() + " " + alumno.getApellidos(),
                    alumno.getMatricula(),
                    ultimo.getFecha().format(FMT),
                    String.format("%.0f", ultimo.getHuellaTotal()),
                    cat.toString()
            });
        }
        areaDetalle.setText("Selecciona un alumno para ver el desglose.");
    }

    private void mostrarDetalle() {
        int fila = tablaResumen.getSelectedRow();
        if (fila < 0) return;
        String matricula = (String) modeloResumen.getValueAt(fila, 1);
        Alumno alumno = alumnoService.buscarPorMatricula(matricula);
        if (alumno == null) return;

        List<RegistroActividad> registros = registroService.listarPorAlumno(alumno.getId());
        RegistroActividad ultimo = registros.stream()
                .filter(r -> r.getHuellaTotal() != null)
                .reduce((a, b) -> b)
                .orElse(null);
        if (ultimo == null) return;

        // Solo calcula en memoria para mostrar el desglose — no persiste nada
        CalculoHuellaService.ResultadoHuella resultado = calculoHuellaService.calcularDesdeValores(
                ultimo.getConsumoElectricidad(), ultimo.getConsumoGasNatural(),
                ultimo.getKmCochePrivado(),      ultimo.getKmTransportePublico(),
                ultimo.getVuelosCortos(),        ultimo.getVuelosLargos(),
                ultimo.getConsumoCarne(),        ultimo.getResiduosKg());

        CategoriaService.Categoria cat = categoriaService.clasificar(resultado);
        Map<String, Double> pct = calculoHuellaService.calcularPorcentajes(resultado);
        List<String> recomendaciones = categoriaService.obtenerRecomendaciones(cat, resultado);

        StringBuilder sb = new StringBuilder();
        sb.append("DESGLOSE DE EMISIONES\n");
        sb.append(resultado);
        sb.append("\nDISTRIBUCION PORCENTUAL\n");
        pct.forEach((k, v) -> sb.append(String.format("  %-22s %5.1f %%%n", k + ":", v)));
        sb.append("\nCATEGORIA: ").append(cat).append("\n\n");
        sb.append("RECOMENDACIONES\n");
        recomendaciones.forEach(r -> sb.append("- ").append(r).append("\n"));

        areaDetalle.setText(sb.toString());
        areaDetalle.setCaretPosition(0);
    }

    private static class CategoriaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);
            if (!isSelected && value != null) {
                String texto = value.toString();
                if      (texto.contains("Muy baja")) setBackground(new Color(0xd5f5e3));
                else if (texto.contains("Baja"))     setBackground(new Color(0xa9dfbf));
                else if (texto.contains("Media"))    setBackground(new Color(0xfdebd0));
                else if (texto.contains("Muy alta")) setBackground(new Color(0xfadbd8));
                else if (texto.contains("Alta"))     setBackground(new Color(0xf5cba7));
                else                                 setBackground(table.getBackground());
            } else if (isSelected) {
                setBackground(table.getSelectionBackground());
            }
            return this;
        }
    }
}
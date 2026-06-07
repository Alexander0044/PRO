package ieselrincon.es.view;

import ieselrincon.es.model.Alumno;
import ieselrincon.es.model.RegistroActividad;
import ieselrincon.es.service.AlumnoService;
import ieselrincon.es.service.CalculoHuellaService;
import ieselrincon.es.service.RegistroService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class RegistroPanel extends JPanel {

    private final RegistroService      registroService;
    private final AlumnoService        alumnoService;
    private final CalculoHuellaService calculoHuellaService;
    private final MainFrame            mainFrame;

    private JComboBox<Alumno> cmbAlumno;

    private JTextField txtFecha;
    private JTextField txtElectricidad;
    private JTextField txtGas;
    private JTextField txtKmCoche;
    private JTextField txtKmPublico;
    private JTextField txtVuelosCortos;
    private JTextField txtVuelosLargos;
    private JTextField txtCarne;
    private JTextField txtResiduos;

    private JLabel lblHuellaPrevia;

    private JTable            tablaHistorial;
    private DefaultTableModel modeloHistorial;

    private static final String[] COLS_HISTORIAL =
            {"ID", "Fecha", "Electricidad", "Gas", "Km Coche", "Km Pub.", "Huella (kg CO2)"};
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public RegistroPanel(RegistroService registroService, AlumnoService alumnoService,
                         CalculoHuellaService calculoHuellaService, MainFrame mainFrame) {
        this.registroService      = registroService;
        this.alumnoService        = alumnoService;
        this.calculoHuellaService = calculoHuellaService;
        this.mainFrame            = mainFrame;
        initUI();
        refrescarAlumnos();
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                crearPanelFormulario(), crearPanelHistorial());
        split.setResizeWeight(0.55);
        split.setDividerLocation(380);
        add(split, BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Nuevo registro de consumo"));

        JPanel panelAlumno = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        cmbAlumno = new JComboBox<>();
        cmbAlumno.setPreferredSize(new Dimension(260, 26));
        cmbAlumno.addActionListener(e -> cargarHistorial());
        txtFecha = new JTextField(LocalDate.now().format(FMT), 10);
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> refrescarAlumnos());
        panelAlumno.add(new JLabel("Alumno:"));
        panelAlumno.add(cmbAlumno);
        panelAlumno.add(new JLabel("  Fecha (dd/MM/yyyy):"));
        panelAlumno.add(txtFecha);
        panelAlumno.add(btnRefrescar);

        JPanel grid = new JPanel(new GridLayout(0, 4, 8, 6));
        grid.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        txtElectricidad = agregarCampo(grid, "Electricidad (kWh/mes):", "0");
        txtGas          = agregarCampo(grid, "Gas natural (m3/mes):",   "0");
        txtKmCoche      = agregarCampo(grid, "Km coche/semana:",        "0");
        txtKmPublico    = agregarCampo(grid, "Km transp. publico/sem:", "0");
        txtVuelosCortos = agregarCampo(grid, "Vuelos cortos/año:",     "0");
        txtVuelosLargos = agregarCampo(grid, "Vuelos largos/año:",     "0");
        txtCarne        = agregarCampo(grid, "Raciones carne/semana:",  "0");
        txtResiduos     = agregarCampo(grid, "Kg residuos/semana:",     "0");

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        lblHuellaPrevia = new JLabel("Huella estimada: -");
        lblHuellaPrevia.setFont(lblHuellaPrevia.getFont().deriveFont(Font.BOLD, 13f));
        lblHuellaPrevia.setForeground(new Color(0x2c7a4b));

        JButton btnPrevisualizar = new JButton("Previsualizar");
        JButton btnGuardar       = new JButton("Guardar registro");
        JButton btnLimpiar       = new JButton("Limpiar");

        btnPrevisualizar.addActionListener(e -> previsualizar());
        btnGuardar.addActionListener(e       -> guardar());
        btnLimpiar.addActionListener(e       -> limpiarFormulario());

        panelInferior.add(lblHuellaPrevia);
        panelInferior.add(btnPrevisualizar);
        panelInferior.add(btnGuardar);
        panelInferior.add(btnLimpiar);

        panel.add(panelAlumno,   BorderLayout.NORTH);
        panel.add(grid,          BorderLayout.CENTER);
        panel.add(panelInferior, BorderLayout.SOUTH);
        return panel;
    }

    private JTextField agregarCampo(JPanel panel, String etiqueta, String valorDefecto) {
        panel.add(new JLabel(etiqueta));
        JTextField campo = new JTextField(valorDefecto, 8);
        panel.add(campo);
        return campo;
    }

    private JScrollPane crearPanelHistorial() {
        modeloHistorial = new DefaultTableModel(COLS_HISTORIAL, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setRowHeight(22);
        tablaHistorial.getTableHeader().setReorderingAllowed(false);
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(40);
        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.setBorder(BorderFactory.createTitledBorder("Historial de registros del alumno"));
        return scroll;
    }

    private void previsualizar() {
        try {
            CalculoHuellaService.ResultadoHuella r = calculoHuellaService.calcularDesdeValores(
                    parseCampo(txtElectricidad), parseCampo(txtGas),
                    parseCampo(txtKmCoche),      parseCampo(txtKmPublico),
                    parseCampo(txtVuelosCortos), parseCampo(txtVuelosLargos),
                    parseCampo(txtCarne),        parseCampo(txtResiduos));
            lblHuellaPrevia.setText(String.format("Huella estimada: %.0f kg CO2/año", r.huellaTotal));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduce valores numericos validos.",
                    "Formato incorrecto", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void guardar() {
        Alumno alumno = (Alumno) cmbAlumno.getSelectedItem();
        if (alumno == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un alumno.",
                    "Sin alumno", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim(), FMT);

            // Crea el registro en MySQL y devuelve el objeto con el id asignado
            RegistroActividad registro = registroService.crearRegistro(
                    alumno.getId(), fecha,
                    parseCampo(txtElectricidad), parseCampo(txtGas),
                    parseCampo(txtKmCoche),      parseCampo(txtKmPublico),
                    parseCampo(txtVuelosCortos), parseCampo(txtVuelosLargos),
                    parseCampo(txtCarne),        parseCampo(txtResiduos));

            // Calcula la huella, guarda huella_total en registros_actividad
            // y persiste el desglose en resultados_calculo
            CalculoHuellaService.ResultadoHuella resultado =
                    calculoHuellaService.calcular(registro, registroService);

            lblHuellaPrevia.setText(String.format("Huella calculada: %.0f kg CO2/año", resultado.huellaTotal));
            cargarHistorial();
            JOptionPane.showMessageDialog(this,
                    String.format("Registro guardado.%nHuella: %.0f kg CO2/año", resultado.huellaTotal),
                    "Registro guardado", JOptionPane.INFORMATION_MESSAGE);

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto (dd/MM/yyyy).",
                    "Error de fecha", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduce valores numericos validos.",
                    "Formato incorrecto", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        for (JTextField campo : new JTextField[]{
                txtElectricidad, txtGas, txtKmCoche, txtKmPublico,
                txtVuelosCortos, txtVuelosLargos, txtCarne, txtResiduos}) {
            campo.setText("0");
        }
        txtFecha.setText(LocalDate.now().format(FMT));
        lblHuellaPrevia.setText("Huella estimada: -");
    }

    public void refrescarAlumnos() {
        Alumno seleccionado = (Alumno) cmbAlumno.getSelectedItem();
        cmbAlumno.removeAllItems();
        for (Alumno a : alumnoService.listarAlumnos()) {
            cmbAlumno.addItem(a);
        }
        if (seleccionado != null) cmbAlumno.setSelectedItem(seleccionado);
        cargarHistorial();
    }

    private void cargarHistorial() {
        modeloHistorial.setRowCount(0);
        Alumno alumno = (Alumno) cmbAlumno.getSelectedItem();
        if (alumno == null) return;
        List<RegistroActividad> lista = registroService.listarPorAlumno(alumno.getId());
        for (RegistroActividad r : lista) {
            modeloHistorial.addRow(new Object[]{
                    r.getId(),
                    r.getFecha().format(FMT),
                    String.format("%.1f", r.getConsumoElectricidad()),
                    String.format("%.1f", r.getConsumoGasNatural()),
                    String.format("%.1f", r.getKmCochePrivado()),
                    String.format("%.1f", r.getKmTransportePublico()),
                    r.getHuellaTotal() != null ? String.format("%.0f", r.getHuellaTotal()) : "-"
            });
        }
    }

    private double parseCampo(JTextField campo) {
        String texto = campo.getText().trim().replace(',', '.');
        return Double.parseDouble(texto.isEmpty() ? "0" : texto);
    }
}
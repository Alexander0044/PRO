package ieselrincon.es.view;

import ieselrincon.es.model.Alumno;
import ieselrincon.es.service.AlumnoService;
import ieselrincon.es.service.GrupoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AlumnoPanel extends JPanel {

    private final AlumnoService alumnoService;
    private final GrupoService  grupoService;
    private final MainFrame     mainFrame;

    private JTable            tabla;
    private DefaultTableModel modeloTabla;
    private JTextField        txtBuscar;
    private JButton           btnNuevo;
    private JButton           btnEditar;
    private JButton           btnEliminar;
    private JButton           btnBuscar;
    private JButton           btnLimpiar;

    private static final String[] COLUMNAS = {"ID", "Nombre", "Apellidos", "Email", "Matricula"};

    public AlumnoPanel(AlumnoService alumnoService, GrupoService grupoService, MainFrame mainFrame) {
        this.alumnoService = alumnoService;
        this.grupoService  = grupoService;
        this.mainFrame     = mainFrame;
        initUI();
        refrescarTabla();
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelTabla(),    BorderLayout.CENTER);
        add(crearPanelBotones(),  BorderLayout.SOUTH);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Buscar alumno"));
        txtBuscar  = new JTextField(25);
        btnBuscar  = new JButton("Buscar");
        btnLimpiar = new JButton("Limpiar");
        btnBuscar.addActionListener(e  -> buscar());
        btnLimpiar.addActionListener(e -> { txtBuscar.setText(""); refrescarTabla(); });
        txtBuscar.addActionListener(e  -> buscar());
        panel.add(new JLabel("Nombre / Apellidos:"));
        panel.add(txtBuscar);
        panel.add(btnBuscar);
        panel.add(btnLimpiar);
        return panel;
    }

    private JScrollPane crearPanelTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editarSeleccionado();
            }
        });
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Alumnos registrados"));
        return scroll;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        btnNuevo    = new JButton("Nuevo");
        btnEditar   = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnNuevo.addActionListener(e    -> abrirDialogoNuevo());
        btnEditar.addActionListener(e   -> editarSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        panel.add(btnNuevo);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        return panel;
    }

    private void abrirDialogoNuevo() {
        AlumnoFormDialog dialog = new AlumnoFormDialog(mainFrame, alumnoService, null);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) refrescarTabla();
    }

    private void editarSeleccionado() {
        Alumno alumno = obtenerAlumnoSeleccionado();
        if (alumno == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un alumno para editar.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AlumnoFormDialog dialog = new AlumnoFormDialog(mainFrame, alumnoService, alumno);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) refrescarTabla();
    }

    private void eliminarSeleccionado() {
        Alumno alumno = obtenerAlumnoSeleccionado();
        if (alumno == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un alumno para eliminar.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "Eliminar al alumno " + alumno + "?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirmacion == JOptionPane.YES_OPTION) {
            alumnoService.eliminarAlumno(alumno.getId());
            refrescarTabla();
        }
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) { refrescarTabla(); return; }
        try {
            cargarFilas(alumnoService.buscarPorNombre(texto));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refrescarTabla() {
        cargarFilas(alumnoService.listarAlumnos());
    }

    private void cargarFilas(List<Alumno> lista) {
        modeloTabla.setRowCount(0);
        for (Alumno a : lista) {
            modeloTabla.addRow(new Object[]{
                    a.getId(), a.getNombre(), a.getApellidos(), a.getEmail(), a.getMatricula()
            });
        }
    }

    // Devuelve el objeto Alumno de la fila seleccionada, o null si no hay selección
    private Alumno obtenerAlumnoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return null;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        return alumnoService.obtenerAlumno(id);
    }
}
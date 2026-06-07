package ieselrincon.es.view;

import ieselrincon.es.model.Alumno;
import ieselrincon.es.service.AlumnoService;

import javax.swing.*;
import java.awt.*;

public class AlumnoFormDialog extends JDialog {

    private final AlumnoService alumnoService;
    private final Alumno        alumnoExistente;

    private JTextField txtNombre;
    private JTextField txtApellidos;
    private JTextField txtEmail;
    private JTextField txtMatricula;

    private boolean confirmado = false;

    public AlumnoFormDialog(Frame parent, AlumnoService alumnoService, Alumno alumnoExistente) {
        super(parent, alumnoExistente == null ? "Nuevo alumno" : "Editar alumno", true);
        this.alumnoService   = alumnoService;
        this.alumnoExistente = alumnoExistente;
        initUI();
        if (alumnoExistente != null) preRellenar();
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelBotones(),    BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del alumno"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtNombre    = new JTextField(22);
        txtApellidos = new JTextField(22);
        txtEmail     = new JTextField(22);
        txtMatricula = new JTextField(22);

        agregarFila(panel, gbc, 0, "Nombre *:",    txtNombre);
        agregarFila(panel, gbc, 1, "Apellidos *:", txtApellidos);
        agregarFila(panel, gbc, 2, "Email:",       txtEmail);
        agregarFila(panel, gbc, 3, "Matricula *:", txtMatricula);

        JLabel nota = new JLabel("  * campos obligatorios");
        nota.setFont(nota.getFont().deriveFont(Font.ITALIC, 11f));
        nota.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(nota, gbc);

        return panel;
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc,
                             int fila, String etiqueta, JTextField campo) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(campo, gbc);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnAceptar  = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        btnAceptar.setPreferredSize(new Dimension(120, 30));
        btnCancelar.setPreferredSize(new Dimension(120, 30));
        getRootPane().setDefaultButton(btnAceptar);
        btnAceptar.addActionListener(e  -> aceptar());
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        panel.add(btnAceptar);
        panel.add(btnCancelar);
        return panel;
    }

    private void preRellenar() {
        txtNombre.setText(alumnoExistente.getNombre());
        txtApellidos.setText(alumnoExistente.getApellidos());
        txtEmail.setText(alumnoExistente.getEmail() != null ? alumnoExistente.getEmail() : "");
        txtMatricula.setText(alumnoExistente.getMatricula());
    }

    private void aceptar() {
        String nombre    = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String email     = txtEmail.getText().trim();
        String matricula = txtMatricula.getText().trim();
        try {
            if (alumnoExistente == null) {
                alumnoService.crearAlumno(nombre, apellidos,
                        email.isEmpty() ? null : email, matricula);
            } else {
                alumnoService.actualizarAlumno(alumnoExistente.getId(),
                        nombre, apellidos,
                        email.isEmpty() ? null : email, matricula);
            }
            confirmado = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error de validacion", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
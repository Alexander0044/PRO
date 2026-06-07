package ieselrincon.es.view;

import ieselrincon.es.model.Alumno;
import ieselrincon.es.service.AlumnoService;
import ieselrincon.es.service.CalculoHuellaService;
import ieselrincon.es.service.CategoriaService;
import ieselrincon.es.service.GrupoService;
import ieselrincon.es.service.RegistroService;
import ieselrincon.es.util.HibernateUtil;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final AlumnoService        alumnoService;
    private final GrupoService         grupoService;
    private final RegistroService      registroService;
    private final CalculoHuellaService calculoHuellaService;
    private final CategoriaService     categoriaService;

    private AlumnoPanel     alumnoPanel;
    private RegistroPanel   registroPanel;
    private ResultadosPanel resultadosPanel;
    private CategoriaPanel  categoriaPanel;

    private static final String APP_TITLE = "Calculadora de Huella de Carbono";
    private static final int    WIDTH     = 1024;
    private static final int    HEIGHT    = 720;

    public MainFrame() {
        alumnoService        = new AlumnoService();
        grupoService         = new GrupoService();
        registroService      = new RegistroService();
        calculoHuellaService = new CalculoHuellaService();
        categoriaService     = new CategoriaService();
        initUI();
    }

    private void initUI() {
        setTitle(APP_TITLE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(800, 600));

        // Al cerrar la ventana, cerramos también la conexión MySQL
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                HibernateUtil.cerrar();
                dispose();
                System.exit(0);
            }
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setJMenuBar(crearMenuBar());

        alumnoPanel     = new AlumnoPanel(alumnoService, grupoService, this);
        registroPanel   = new RegistroPanel(registroService, alumnoService, calculoHuellaService, this);
        resultadosPanel = new ResultadosPanel(registroService, calculoHuellaService, categoriaService, alumnoService);
        categoriaPanel  = new CategoriaPanel(categoriaService);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addTab("Alumnos",    null, alumnoPanel,     "Gestion de alumnos");
        tabbedPane.addTab("Registros",  null, registroPanel,   "Registrar consumo");
        tabbedPane.addTab("Resultados", null, resultadosPanel, "Ver resultados y huella");
        tabbedPane.addTab("Categorias", null, categoriaPanel,  "Informacion de categorias");
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel statusBar = new JLabel("  Listo");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 12));

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(statusBar,  BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JMenuBar crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic('A');

        JMenuItem miNuevoAlumno = new JMenuItem("Nuevo alumno...");
        miNuevoAlumno.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        miNuevoAlumno.addActionListener(e -> { if (alumnoPanel != null) abrirDialogoNuevoAlumno(); });

        JMenuItem miSalir = new JMenuItem("Salir");
        miSalir.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
        miSalir.addActionListener(e -> {
            HibernateUtil.cerrar();
            System.exit(0);
        });

        menuArchivo.add(miNuevoAlumno);
        menuArchivo.addSeparator();
        menuArchivo.add(miSalir);

        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic('H');

        JMenuItem miAcerca = new JMenuItem("Acerca de...");
        miAcerca.addActionListener(e -> mostrarAcercaDe());
        menuAyuda.add(miAcerca);

        menuBar.add(menuArchivo);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuAyuda);

        return menuBar;
    }

    public boolean abrirDialogoNuevoAlumno() {
        AlumnoFormDialog dialog = new AlumnoFormDialog(this, alumnoService, null);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) {
            alumnoPanel.refrescarTabla();
        }
        return dialog.isConfirmado();
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
                "<html><b>Calculadora de Huella de Carbono</b><br>Version 1.0</html>",
                "Acerca de",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void notificarCambios() {
        if (alumnoPanel     != null) alumnoPanel.refrescarTabla();
        if (registroPanel   != null) registroPanel.refrescarAlumnos();
        if (resultadosPanel != null) resultadosPanel.refrescar();
    }

    public AlumnoService        getAlumnoService()        { return alumnoService; }
    public GrupoService         getGrupoService()         { return grupoService; }
    public RegistroService      getRegistroService()      { return registroService; }
    public CalculoHuellaService getCalculoHuellaService() { return calculoHuellaService; }
    public CategoriaService     getCategoriaService()     { return categoriaService; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
import com.google.gson.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.DuplicateFormatFlagsException;
import java.util.Random;
import java.io.File;
import java.util.UUID;

public class MainWindow extends JFrame implements LangListener, ActionListener{

    private CtrlPresentacio ctrl;

    //private JFrame mainFrame = new JFrame("Main window");
    private JButton buttonImport = new JButton("Import");
    private JPanel panelContent = new JPanel();
    private JPanel panelButtons = new JPanel();
    private JTabbedPane tabbedPanel;
    private JMenuItem open, save, reset;

    // Menus
    private JMenuBar menuBarVista = new JMenuBar();
    private JMenu menuFile = new JMenu("File");
    private JMenuItem menuItemQuit = new JMenuItem("Quit");
    private JMenu menuOptions = new JMenu("Options");
    private JMenuItem menuItemPreferences = new JMenuItem("Preferences");
    private VistaLlibreria tableLlibreria;
    private VistaAutors tableAutors;
    private VistaRelevance vistaRelevance;
    private VistaRelevanceWords vistaRelevanceWords;

    public MainWindow (CtrlPresentacio ctrl) {
        super("Library manager");
        this.ctrl = ctrl;
        tableLlibreria = new VistaLlibreria(this.ctrl, this);
        tableAutors = new VistaAutors(this.ctrl);
        vistaRelevance = new VistaRelevance(this.ctrl, this);
        vistaRelevanceWords = new VistaRelevanceWords(this.ctrl, this);
        initializeComponents();
        assignListenersComponents();
        hacerVisible();
    }

    public void hacerVisible() {
        this.pack();
        this.setVisible(true);
        activar();
    }

    public void activar() {
        this.setEnabled(true);
    }

    public void updateAutors(Boolean required) {
        tableAutors.updateTable(required);
        vistaRelevance.updateAutorList();
        tableAutors.repaint();
        tableLlibreria.repaint();
    }

    public void updateLlibreria(Boolean required) {
        tableLlibreria.updateTable(required);
        vistaRelevance.updateAutorList();
        tableAutors.repaint();
        tableLlibreria.repaint();
    }

    public void desactivar() {
        this.setEnabled(false);
    }
    public void actionPerformed_buttonImport (ActionEvent event) {
        System.out.println("button has been clicked");
    }


    private void assignListenersComponents() {

        buttonImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPerformed_buttonImport(e);
            }
        });

        // Listeners para las opciones de menu
        menuItemQuit.addActionListener
                (new ActionListener() {
                    public void actionPerformed (ActionEvent event) {
                        System.exit(0);
                    }
                });

    }

    private void initializeComponents() {
        initMenuBarVista();
        initPanelContent();
        initPanelButtons();
        initTabbedPanel();
        assignListenersComponents();
        initMainFrame();
    }


    private void initMainFrame() {
        updateLlibreria(false);
        updateAutors(false);
        // SIze
        this.setMinimumSize(new Dimension(800,500));
        this.setPreferredSize(this.getMinimumSize());
        this.setResizable(true);
        // Position
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setJMenuBar(menuBarVista);
        this.add(tabbedPanel);
    }


    private void initMenuBarVista() {
        reset = new JMenuItem("New");
        reset.addActionListener(this);
        reset.setActionCommand("new");
        open = new JMenuItem("Open");
        open.addActionListener(this);
        open.setActionCommand("open");
        save = new JMenuItem("Save");
        save.addActionListener(this);
        save.setActionCommand("save");

        menuFile.add(reset);
        menuFile.add(open);
        menuFile.add(save);
        menuFile.add(menuItemQuit);
        menuOptions.add(menuItemPreferences);
        menuBarVista.add(menuFile);
        menuBarVista.add(menuOptions);
    }

    private void initTabbedPanel(){
        tabbedPanel = new JTabbedPane();
        tabbedPanel.add("Library", tableLlibreria);
        tabbedPanel.add("Authors", tableAutors);
        tabbedPanel.add("Relevance", vistaRelevance);
        tabbedPanel.add("WordsRelevance", vistaRelevanceWords);
    }

    private void initPanelContent() {
        // Layout
        panelContent.setLayout(new BorderLayout());
        // Paneles
        panelContent.add(panelButtons,BorderLayout.NORTH);
        // panelContenidos.add(panelInformacion,BorderLayout.CENTER);
    }


    private void initPanelButtons() {
        // Layout
        panelButtons.setLayout(new FlowLayout());
        // Componentes
        panelButtons.add(buttonImport);
//        buttonAbrirDialog.setToolTipText("Abre un Dialogo modal simple");
    }

    @Override
    public void onLanguageChanged() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("save")) {
            JFrame parentFrame = new JFrame();
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save as");
            fc.setCurrentDirectory(new java.io.File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("lm", "lm");
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(filter);
            int userSelection = fc.showSaveDialog(parentFrame);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String fileToSave = fc.getSelectedFile().getPath();
                if (fileToSave.endsWith(".lm")) ctrl.save(fileToSave);
                else ctrl.save(fileToSave + ".lm");
            }
        }
        else if (e.getActionCommand().equals("open")) {
            int n = JOptionPane.showOptionDialog(null, "Do you want to save before opening file?", "Save",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null, null,JOptionPane.YES_OPTION);
            if (n == JOptionPane.YES_OPTION){
                actionPerformed(new ActionEvent(e.getSource(), new Random().nextInt(), "save"));
            }
            if(n == -1) return;
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Open");
            fc.setCurrentDirectory(new java.io.File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("lm","lm");
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(filter);
            int p = fc.showOpenDialog(open);
            try {
                if (p == JFileChooser.APPROVE_OPTION) {
                    String path = fc.getSelectedFile().getPath();
                    System.out.println(path);
                    ctrl.open(path);
                }
            } catch (Exception ex) {
                ctrl.errorManagement("Error opening");
                ex.printStackTrace();
            }
        }
        else if (e.getActionCommand().equals("new")) {
            int n = JOptionPane.showOptionDialog(null, "Do you want to save before opening new?", "Save",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null, null,JOptionPane.YES_OPTION);
            if (n == JOptionPane.CANCEL_OPTION) return;
            if (n == JOptionPane.YES_OPTION){
                actionPerformed(new ActionEvent(e.getSource(), new Random().nextInt(), "save"));
            }
            if(n == -1) return;
            ctrl.reset();
        }
    }
}

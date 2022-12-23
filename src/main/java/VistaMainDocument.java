import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import static java.awt.GridBagConstraints.*;

public class VistaMainDocument extends JFrame {
    final private CtrlPresentacio ctrl;
    private GridBagConstraints g;
    private JPanel main, buttonPanel;
    private String titol, autor;
    private JButton save, saveas, cancel, modify;
    private JTextField autorField, titleField;
    private JTextArea contentField;
    private JScrollPane scroll;

    public VistaMainDocument(CtrlPresentacio ctrl) {
        this.setName("Document manager");
        this.ctrl = ctrl;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void setDocument(String titol, String autor) {
        this.setLayout(new BorderLayout());
        this.titol = titol;
        this.autor = autor;
        g = new GridBagConstraints();
        initButtonPanel();
        main = new JPanel(new GridBagLayout());
        initName();
        initAutor();
        initContent();
        this.add(main, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(500, 600));
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.setSize(this.getPreferredSize());
        this.setResizable(false);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    private void initName() {
        g.gridx = 0;
        g.gridy = 0;
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;
        String title = ctrl.getDocumentFromNameAutor(titol, autor).getTitol().toString();
        JLabel name = new JLabel("Title: ");
        titleField = new JTextField(10);
        titleField.setText(title);
        titleField.setEditable(false);
        JScrollPane titleScroll = new JScrollPane(titleField);
        main.add(name, g);
        g.gridx++;
        main.add(titleScroll, g);
    }

    private void initAutor() {
        String autor = ctrl.getDocumentFromNameAutor(titol, this.autor).getAutor().toString();
        JLabel name = new JLabel("Author: ");
        autorField = new JTextField(10);
        autorField.setText(autor);
        JScrollPane autorScroll = new JScrollPane(autorField);
        autorField.setEditable(false);
        g.gridx ++;
        main.add(name, g);
        g.gridx ++;
        main.add(autorScroll, g);
    }

    private void initContent() {
        String content = ctrl.getDocumentFromNameAutor(titol, autor).getContingut().toString();
        JLabel name = new JLabel("Content: ");
        contentField = new JTextArea(5, 20);

        contentField.setText(content);
        contentField.setEditable(false);
        scroll = new JScrollPane(contentField);
        scroll.setPreferredSize(new Dimension(300, 400));
        g.gridx = 0;
        g.gridy = 1;
        g.anchor = GridBagConstraints.NORTHWEST;
        main.add(name, g);
        ++g.gridx;
        g.gridwidth = 3;
        g.fill = GridBagConstraints.HORIZONTAL;
        main.add(scroll, g);
    }

    private void initButtonPanel() {
        buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2,2,2,2);
        c.gridx = 0;
        c.gridy = 0;
        modify = new JButton("Modify");
        modify.addActionListener(MODIFY);
        save = new JButton("Save");
        save.addActionListener(SAVE);
        saveas = new JButton("Save as");
        saveas.addActionListener(SAVEAS);
        cancel = new JButton("Cancel");
        cancel.addActionListener(CANCEL);
        buttonPanel.add(modify, c);
        ++c.gridx;
        buttonPanel.add(save, c);
        ++c.gridx;
        buttonPanel.add(saveas, c);
        ++c.gridx;
        buttonPanel.add(cancel);
        save.setVisible(false);
        cancel.setVisible(false);
        saveas.setVisible(false);
    }

    ActionListener MODIFY = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            modify.setVisible(false);
            save.setVisible(true);
            cancel.setVisible(true);
            saveas.setVisible(true);
            autorField.setEditable(true);
            titleField.setEditable(true);
            contentField.setEditable(true);
        }
    };

    ActionListener SAVE = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!autor.equals(autorField.getText())) {
                ctrl.changeAutorDocument(
                        titol,
                        autor,
                        autorField.getText()
                );
                autor = autorField.getText();
            }
            if (!titol.toString().equals(titleField.getText())) {
                ctrl.changeTitolDocument(
                        titol,
                        autor,
                        titleField.getText()
                );
                titol = titleField.getText();
            }
            if (!ctrl.hasEqualContent(titol, autor, contentField.getText())){
                ctrl.setContent(titol, autor, contentField.getText());
            }
            try {
                if(!ctrl.saveDocument(titol, autor)) saveDocAS(titol, autor);
            } catch (Exception E) {
                ctrl.errorManagement("No existeix el document encara.");
            }
            dispose();
        }
    };

    ActionListener SAVEAS = new ActionListener() {
        @Override
        public void actionPerformed (ActionEvent e) {
            try {
                saveDocAS(autor, titol);
            } catch (Exception E){
                ctrl.errorManagement("Error happened when saving.");
            }
        }
    };

    private void saveDocAS(String autor, String titol) throws Exception{
        JFrame parentFrame = new JFrame();
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save document as");
        fc.setCurrentDirectory(new java.io.File("."));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("txt", "txt"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("xml", "xml"));
        fc.setAcceptAllFileFilterUsed(false);
        int userSelection = fc.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String fileToSave = fc.getSelectedFile().getPath();
            String extension = fc.getFileFilter().getDescription();
            if (fileToSave.endsWith(".txt") | fileToSave.endsWith("xml")) {
                ctrl.saveDocument(titol, autor, fileToSave);
            }
            else {
                ctrl.saveDocument(titol, autor, fileToSave + "." + extension);
            }
        }
    }

    ActionListener CANCEL = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    };
}

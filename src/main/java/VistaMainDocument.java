import javax.swing.*;
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
    private Document d;
    private JButton save, cancel, modify;
    private JTextField autorField, titleField;
    private JTextArea contentField;
    private JScrollPane scroll;

    public VistaMainDocument(CtrlPresentacio ctrl) {
        this.ctrl = ctrl;
    }

    public void setDocument(Document d) {
        this.setLayout(new BorderLayout());
        this.d = d;
        g = new GridBagConstraints();
        initButtonPanel();
        main = new JPanel(new GridBagLayout());
        initName();
        initAutor();
        initContent();
        this.add(main, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(500, 400));
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.setSize(this.getPreferredSize());
        this.setResizable(true);
        this.setVisible(true);
    }

    private void initName() {
        g.gridx = 0;
        g.gridy = 0;
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;
        String title = d.getTitol().toString();
        JLabel name = new JLabel("Title: ");
        titleField = new JTextField(10);
        titleField.setText(title);
        titleField.setEditable(false);
        main.add(name, g);
        g.gridx++;
        main.add(titleField, g);
    }

    private void initAutor() {
        String autor = d.getAutor().toString();
        JLabel name = new JLabel("Author: ");
        autorField = new JTextField(10);
        autorField.setText(autor);
        autorField.setEditable(false);
        g.gridx ++;
        main.add(name, g);
        g.gridx ++;
        main.add(autorField, g);
    }

    private void initContent() {
        String content = d.getContingut().toString();
        JLabel name = new JLabel("Content: ");
        contentField = new JTextArea(5, 20);

        contentField.setText(content);
        contentField.setEditable(false);
        scroll = new JScrollPane(contentField);
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
        cancel = new JButton("Cancel");
        cancel.addActionListener(CANCEL);
        buttonPanel.add(modify, c);
        ++c.gridx;
        buttonPanel.add(save, c);
        ++c.gridx;
        buttonPanel.add(cancel);
        save.setVisible(false);
        cancel.setVisible(false);
    }

    ActionListener MODIFY = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            modify.setVisible(false);
            save.setVisible(true);
            cancel.setVisible(true);
            autorField.setEditable(true);
            titleField.setEditable(true);
            contentField.setEditable(true);
        }
    };

    ActionListener SAVE = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!d.getAutor().toString().equals(autorField.getText())) {
                System.out.println("autor");
                ctrl.changeAutorDocument(
                        d,
                        new Frase(autorField.getText())
                );
            }
            if (!d.getTitol().toString().equals(titleField.getText())) {
                System.out.println("titol");
                ctrl.changeTitolDocument(
                        d,
                        new Frase(titleField.getText())
                );
            }
            if (!d.getContingut().equalsString(contentField.getText())){
                System.out.println("content");
                ctrl.setContent(d, contentField.getText());
            }
            dispose();
        }
    };

    ActionListener CANCEL = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    };
}

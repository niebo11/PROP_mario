import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class VistaAutor extends JFrame
{
    private CtrlPresentacio ctrl;
    private JPanel mainPanel;
    private String autor;
    private JLabel title;
    private GridBagConstraints c;
    private JList documentList;
    private JScrollPane listScroller;
    private VistaMainDocument vmd;

    public VistaAutor(){}

    public VistaAutor(CtrlPresentacio ctrl, String autor) {
        vmd = new VistaMainDocument(ctrl);
        mainPanel = new JPanel(new BorderLayout());
        this.ctrl = ctrl;
        this.autor = autor;
        initVistaAutor();
        initList();
        setPreferredSize(new Dimension(500, 400));
        setSize(getPreferredSize());
        setResizable(true);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initVistaAutor(){
        c = new GridBagConstraints();
        initTitle();
        initList();
        this.add(mainPanel);
    }

    private void initTitle() {
        String name = autor;
        title = new JLabel("Autor: " + name, JLabel.CENTER);
        title.setFont(new Font("Verdana", Font.PLAIN, 15));
        mainPanel.add(title, BorderLayout.NORTH);
    }

    private void initList() {
        documentList = new JList();
        updateList();
        listScroller = new JScrollPane(documentList);
        listScroller.setPreferredSize(new Dimension(500, 300));
        mainPanel.add(listScroller, BorderLayout.CENTER);
        documentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    if (!vmd.isVisible()) {
                        JList source = (JList) e.getSource();
                        int index = source.locationToIndex(e.getPoint());
                        String documentName = (String) source.getModel().getElementAt(index);
                        vmd = new VistaMainDocument(ctrl);
                        vmd.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent e) {
                                updateList();
                            }
                        });
                        vmd.setDocument(documentName, autor);
                    }
                }
            }
        });
    }

    private void updateList() {
        String[] documentsName = ctrl.getDocumentListFromAutor(autor);
        Arrays.sort(documentsName);
        DefaultListModel<String> aux = new DefaultListModel<String>();
        for (String name: documentsName) {aux.addElement(name);}
        documentList.setModel(aux);
    }
}

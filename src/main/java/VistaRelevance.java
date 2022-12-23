import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class VistaRelevance extends JPanel
{
    private CtrlPresentacio ctrl;
    private MainWindow mainWindow;
    private JTextField k;
    private JComboBox autor;
    private JComboBox document;
    private JButton search;
    private GridBagConstraints c;
    private JTable table;
    private VistaMainDocument documentDialog;
    private String[][] docs;
    public VistaRelevance(CtrlPresentacio ctrl, MainWindow mainWindow) {
        documentDialog = new VistaMainDocument(ctrl);
        this.ctrl = ctrl;
        this.mainWindow = mainWindow;
        initVistaRelevance();
    }

    private void initVistaRelevance(){
        c = new GridBagConstraints();
        docs = new String[0][];
        setLayout(new GridBagLayout());
        initKTextField();
        initAutorComboBox();
        initDocument();
        initButton();
        initTable();
    }

    private void initKTextField() {
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 70;
        c.insets = new Insets(4, 4,4 ,10);
        JPanel kText = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Nº documents:");
        kText.add(label, BorderLayout.WEST);
        k = new JTextField(6);
        kText.add(k, BorderLayout.CENTER);
        this.add(kText, c);
    }

    private void initAutorComboBox() {
        c.gridx = 1;
        c.ipadx = 0;
        c.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        JPanel autorPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Author:");
        autorPanel.add(label, BorderLayout.WEST);
        this.autor = new JComboBox();
        this.autor.setPreferredSize(new Dimension(200,20));
        this.autor.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        this.autor.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {
                JComboBox comboBox = (JComboBox) e.getSource();
                Object popup = comboBox.getUI().getAccessibleChild(comboBox, 0);
                Component c = ((Container) popup).getComponent(0);
                if (c instanceof JScrollPane)
                {
                    JScrollPane scrollpane = (JScrollPane) c;
                    JScrollBar scrollBar = scrollpane.getVerticalScrollBar();
                    Dimension scrollBarDim = new Dimension(500, scrollBar
                            .getPreferredSize().height);
                    scrollBar.setPreferredSize(scrollBarDim);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
        this.autor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JComboBox comboBox = (JComboBox) event.getSource();
                String autorName = comboBox.getSelectedItem().toString();
                ArrayList<Document> documents = ctrl.getDocumentFromAutor(new Frase(autorName));
                String[] documentName = new String[documents.size()];
                for (int i = 0; i < documents.size(); ++i) {
                    documentName[i] = documents.get(i).toString();
                }
                document.setModel(new SortedComboBoxModel<String>(documentName));
            }
        });
        autorPanel.add(this.autor, BorderLayout.CENTER);
        this.add(autorPanel, c);
    }

    private void initDocument() {
        c.gridx = 2;
        c.ipadx = 60;
        JPanel documentPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Document:");
        documentPanel.add(label, BorderLayout.WEST);
        this.document = new JComboBox();
        documentPanel.add(this.document, BorderLayout.CENTER);
        this.add(documentPanel, c);
    }

    private void initButton(){
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(8, 0,8 ,0);
        search = new JButton("Search");
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (Integer.parseInt(k.getText()) <= 0) {
                        throw new NumberFormatException("nope");
                    } else {
                        computeDocumentList();
                    }
                } catch (NumberFormatException E) {
                    ctrl.errorManagement("K is not a valid integer.");
                }
            }
        });
        this.add(search, c);
    }

    private void computeDocumentList() {
        int number_doc = Integer.parseInt(k.getText());
        docs = ctrl.getMostSimilarDocument(
                document.getSelectedItem().toString(),
                autor.getSelectedItem().toString(),
                number_doc);
        updateTableValues();
    }

    private void initTable() {
        String[] columnsNames = {"Document name", "Author", "Relevance"};
        Object [][] data = ctrl.getDocumentosData();
        MultiLineTableCellRenderer renderer = new MultiLineTableCellRenderer(ctrl);
        table = new JTable(new MyTableModel(data, columnsNames));
        table.setDefaultRenderer(String[].class,renderer);
        table.setDefaultEditor( String[].class, renderer);

        JPanel panel = new JPanel(new BorderLayout());
        table.getTableHeader().setReorderingAllowed(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(table.getRowHeight()+9);
        table.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        table.setShowGrid(true);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(table, BorderLayout.CENTER);
        c.gridx = 0;
        c.gridy = 2;
        c.ipady = 40;
        c.fill = c.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.insets = new Insets(6, 6, 6, 6);
        this.add(panel, c);
        panel.setVisible(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mayShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mayShowPopup(e);
            }

            @Override
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    JTable source  = (JTable) e.getSource();
                    VistaRelevance vr = (VistaRelevance) source.getParent().getParent();
                    int row = source.rowAtPoint( e.getPoint() );
                    int column = source.columnAtPoint( e.getPoint() );

                    if (!source.isRowSelected(row))
                        source.changeSelection(row, column, false, false);
                    if(row != -1) {
                        String name = (String) table.getValueAt(row, 0).toString();
                        String autor = (String) table.getValueAt(row, 1).toString();
                        if (!documentDialog.isActive()){
                            documentDialog = new VistaMainDocument(ctrl);
                            documentDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                                @Override
                                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                                    vr.updateAll();
                                }
                            });
                            documentDialog.setDocument(name, autor);
                        }
                    }
                }
            }

            private void mayShowPopup(MouseEvent e) {
            }

        });
        updateTableValues();
    }

    public class MyTableModel extends DefaultTableModel {
        private MyTableModel(Object[][] tableData, Object[] columnsNames){
            super(tableData, columnsNames);
        }

        public boolean isCellEditable(int row, int column) { return false;}
    }

    public void updateTable(){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        Object[][] auxi = ctrl.getDocumentosData();
        for (Object[] anAuxi : auxi) { model.addRow(anAuxi); }
        table.setModel(model);
        updateRowHeights();
    }

    public void updateTableValues() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (int i = 0; i < docs.length; i++) {
            model.addRow(docs[i]);
        }
        table.setModel(model);
        updateRowHeights();
    }

    public void updateAll() {
        computeDocumentList();
        mainWindow.updateAutors(true);
    }

    private void updateRowHeights() {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();
            for (int column = 0; column < table.getColumnCount(); column++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }
            table.setRowHeight(row, rowHeight);
        }
    }

    public void updateAutorList(){
        Object[][] autors = ctrl.getAutorsData();
        String[] autorsName = new String[autors.length];
        for(int i = 0; i < autors.length; ++i) {
            autorsName[i] = (String) autors[i][0];
        }
        this.autor.setModel(new SortedComboBoxModel<String>(autorsName));
        updateDocumentList();
    }

    public void updateDocumentList() {
        if (autor.getSelectedItem() != null) {
            String autorName = autor.getSelectedItem().toString();
            ArrayList<Document> documents = ctrl.getDocumentFromAutor(new Frase(autorName));
            String[] documentName = new String[documents.size()];
            for (int i = 0; i < documents.size(); ++i) {
                documentName[i] = documents.get(i).toString();
            }
            document.setModel(new SortedComboBoxModel<String>(documentName));
        }
    }
}

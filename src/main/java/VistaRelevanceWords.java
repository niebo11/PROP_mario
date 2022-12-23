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

public class VistaRelevanceWords extends JPanel
{
    private CtrlPresentacio ctrl;
    private JTextField k;
    private JTextField words;
    private JComboBox document;
    private JButton search;
    private GridBagConstraints c;
    private JTable table;
    private VistaMainDocument documentDialog;
    private String[][] docs;
    private MainWindow mw;
    public VistaRelevanceWords(CtrlPresentacio ctrl, MainWindow mw) {
        documentDialog = new VistaMainDocument(ctrl);
        this.mw = mw;
        this.ctrl = ctrl;
        initVistaRelevance();
    }

    private void initVistaRelevance(){
        c = new GridBagConstraints();
        docs = new String[0][];
        setLayout(new GridBagLayout());
        initKTextField();
        initAutorComboBox();
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
        c.ipadx = 70;
        c.insets = new Insets(4, 4, 4, 10);
        JPanel wordsPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Words");
        wordsPanel.add(label, BorderLayout.WEST);
        words = new JTextField(15);
        wordsPanel.add(words, BorderLayout.CENTER);
        this.add(wordsPanel, c);
    }


    private void initButton() {
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

    private void computeDocumentList(){
        int number_doc = Integer.parseInt(k.getText());
        String words_to_search = words.getText();
        docs = ctrl.getPwords(words_to_search, number_doc);
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
                    VistaRelevanceWords vrw = (VistaRelevanceWords) source.getParent().getParent();
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
                                    vrw.updateAll();
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
        mw.updateAutors(true);
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
}

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.beans.*;
import java.io.*;

public class VistaLlibreria extends JPanel implements LangListener{

    private CtrlPresentacio ctrl;
    private JTable table;
    private JFrame mainFrame;
    private JPanel buttonPanel;
    private JPopupMenu popup;
    private JTextField Filtro = new JTextField(20);
    private JComboBox filterType;
    private VistaMainDocument documentDialog;
    private String expression = null;

    public VistaLlibreria(CtrlPresentacio ctrl, JFrame mainFrame) {
        documentDialog = new VistaMainDocument(ctrl);
        this.ctrl = ctrl;
        this.mainFrame = mainFrame;
        GridBagConstraints g = new GridBagConstraints();
        this.setLayout(new BorderLayout());
        initTable();
        initFilterType();

        JPanel actionsPanel = initSearch();

        initButtonPanel();
        initPopup();
        JPanel actions = new JPanel();
        actions.add(actionsPanel);
        actions.add(buttonPanel, g);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        this.add(actions, BorderLayout.SOUTH);
    }

    private void initFilterType(){
        String[] types = {"Document name", "Author", "Content"};
        filterType = new JComboBox(types);
    }

    private void initTable() {
        String[] columnsNames = {"Document name", "Author"};
        Object [][] data = ctrl.getDocumentosData();
        MultiLineTableCellRenderer renderer = new MultiLineTableCellRenderer(ctrl);
        table = new JTable(new MyTableModel(data, columnsNames));
        table.setDefaultRenderer(String[].class,renderer);
        table.setDefaultEditor( String[].class, renderer);

        JPanel panel = new JPanel(new GridBagLayout());
        table.getTableHeader().setReorderingAllowed(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(table.getRowHeight()+9);
        table.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        table.getColumnModel().getColumn(1).setCellRenderer(new MultiLineTableCellRenderer(ctrl));
        table.getColumnModel().getColumn(1).setCellEditor(new MultiLineTableCellRenderer(ctrl));
        table.setShowGrid(true);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        this.add(table.getTableHeader(), BorderLayout.NORTH);
        this.add(table, BorderLayout.CENTER);
        this.add(panel, BorderLayout.SOUTH);
    }

    private JPanel initSearch() {
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(rowSorter);
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel(Lang.getString("busca")),
                BorderLayout.WEST);
        searchPanel.add(Filtro, BorderLayout.CENTER);
        searchPanel.add(filterType, BorderLayout.EAST);
        JPanel actionsPanel = new JPanel(new CardLayout());
        actionsPanel.add(searchPanel);
        actionsPanel.setVisible(false);
        Action SEARCH = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(actionsPanel.isVisible()) actionsPanel.setVisible(false);
                else actionsPanel.setVisible(true);
            }
        };

        RowFilter<Object, Object> booleanFilter = new RowFilter<Object, Object>() {
            public boolean include(Entry<? extends Object, ? extends Object> entry) {
                String name = entry.getStringValue(0);
                String autor = entry.getStringValue(1);
                Document to_search = ctrl.getDocumentFromNameAutor(name, autor);
                return ctrl.parserExpression(expression, to_search);
            }
        };

        Filtro.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent e) {
                expression = Filtro.getText();
                String type = (String) filterType.getSelectedItem();
                if (expression.trim().length() == 0) { rowSorter.setRowFilter(null); }
                else if (type.equals("Content")) {
                    rowSorter.setRowFilter(booleanFilter);
                }
                else { rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + expression,0)); }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                expression = Filtro.getText();
                String type = (String) filterType.getSelectedItem();
                if (expression.trim().length() == 0) { rowSorter.setRowFilter(null); }
                else if (type.equals("Content")) {
                    rowSorter.setRowFilter(booleanFilter);
                }
                else { rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + expression,0)); }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

            }

        });

        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK),"search");
        this.getActionMap().put("search",SEARCH);
        return actionsPanel;
    }

    private void initPopup() {
        JMenuItem delete = new JMenuItem(Lang.getString("delete"));
        delete.addActionListener(DELETE);
        popup = new JPopupMenu();
        popup.add(delete);
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
                    VistaLlibreria vl = (VistaLlibreria) source.getParent();
                    int row = source.rowAtPoint( e.getPoint() );
                    int column = source.columnAtPoint( e.getPoint() );

                    if (!source.isRowSelected(row))
                        source.changeSelection(row, column, false, false);
                    if(row != -1) {
                        String name = (String) table.getValueAt(row, 0).toString();
                        String autor = (String) table.getValueAt(row, 1).toString();
                        Document d = ctrl.getDocumentFromNameAutor(name, autor);
                        if (!documentDialog.isActive()){
                            documentDialog = new VistaMainDocument(ctrl);
                            documentDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                                @Override
                                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                                    vl.updateTable(true);
                                }
                            });
                            documentDialog.setDocument(d);
                        }
                    }
                }
            }

            private void mayShowPopup(MouseEvent e) {
                if (e.isPopupTrigger())
                {
                    JTable source = (JTable)e.getSource();
                    int row = source.rowAtPoint( e.getPoint() );
                    int column = source.columnAtPoint( e.getPoint() );

                    if (! source.isRowSelected(row))
                        source.changeSelection(row, column, false, false);
                    if(row != -1) popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });
    }

    private void initButtonPanel() {
        buttonPanel = new JPanel(new GridBagLayout());
        JButton add = new JButton(Lang.getString("add"));
        add.addActionListener(ADD);
        JButton imprt = new JButton(Lang.getString("import"));
        imprt.addActionListener(IMPORT);
        buttonPanel.add(add);
        buttonPanel.add(imprt);
        buttonPanel.setVisible(true);
    }


    public class MyTableModel extends DefaultTableModel{
        private MyTableModel(Object[][] tableData, Object[] columnsNames){
            super(tableData, columnsNames);
        }

        public boolean isCellEditable(int row, int column) { return false;}
    }

    public void updateTable(Boolean required){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        Object[][] auxi = ctrl.getDocumentosData();
        for (Object[] anAuxi : auxi) { model.addRow(anAuxi); }
        table.setModel(model);
        updateRowHeights();
        if(required) {updateAutors();};
    }

    public void updateAutors(){
        JTabbedPane tp = (JTabbedPane) getParent();
        MainWindow mw = (MainWindow) SwingUtilities.getRoot(tp);
        mw.updateAutors(false);
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    ActionListener DELETE = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JPanel panel = new JPanel(new GridLayout(0,1));
            panel.add(new JLabel(Lang.getString("confirmacioneliminar")));
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int[] row = table.getSelectedRows();
            int result = JOptionPane.showConfirmDialog(null,Lang.getString("confirmacioneliminar"),Lang.getString("delete"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(result == JOptionPane.YES_OPTION) {
                for(int r : row) {
                    String name = table.getValueAt(r,0).toString();
                    String autor = table.getValueAt(r, 1).toString();
                    Document to_remove = new Document(
                            new Frase(name),
                            new Frase(autor)
                    );
                    ctrl.removeDocument(to_remove);
                }
            }
            updateTable(true);
        }
    };

    Action ADD = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JButton add = (JButton) actionEvent.getSource();
            JPanel jp = (JPanel) add.getParent();
            VistaLlibreria frame = (VistaLlibreria) jp.getParent(); //TODO
            VistaDocument vd = new VistaDocument();
            JOptionPane optionPane = new JOptionPane(
                    vd,
                    JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION
            );

            JDialog dialog = new JDialog(frame.getMainFrame(),
                    "Add Document",
                    true);
            dialog.setContentPane(optionPane);
            optionPane.addPropertyChangeListener(
                    new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent e) {
                            String prop = e.getPropertyName();
                            if (dialog.isVisible()
                                    && (e.getSource() == optionPane)
                                    && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                                dialog.setVisible(false);
                            }
                        }
                    });
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);

            int result = ((Integer)optionPane.getValue()).intValue();
            if (result == 0) {
                Contingut c = new Contingut(vd.getContent());
                Document documentToAdd = new Document(
                        new Frase(vd.getName()),
                        new Frase(vd.getAuthor()),
                        c
                );
                try{
                    if (ctrl.hasDocument(documentToAdd)) { throw new DuplicateFormatFlagsException("duplicate");}
                    else { ctrl.addDocument(documentToAdd); }
                } catch (DuplicateFormatFlagsException E) {ctrl.errorManagement("duplicate");}
            }
            updateTable(true);
        }
    };

    ActionListener IMPORT = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(true);
            fc.setDialogTitle(Lang.getString("open"));
            fc.setCurrentDirectory(new java.io.File("."));
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(new FileNameExtensionFilter("txt", "txt"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("xml", "xml"));
            int p = fc.showOpenDialog((Component) actionEvent.getSource());
            try {
                if (p == JFileChooser.APPROVE_OPTION) {
                    File[] files = fc.getSelectedFiles();
                    for (File f: files) {
                        String path = f.getPath();
                        try {
                            ctrl.importDocumentPlainText(path);
                        } catch (Exception ex) {
                            String errorMessage = path + " failed to import: document exists";
                            ctrl.errorManagement(errorMessage);
                        } //TODO INVALID FORMAT
                    }
                }
            } catch (Exception ex) {
                //ctrl.gestionError(Lang.getString("error"));
                ex.printStackTrace();
            }
            updateTable(true);
        }
    };

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

    public void onLanguageChanged() {
    }
}

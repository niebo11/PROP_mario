import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class VistaAutors extends JPanel
{
    private CtrlPresentacio ctrl;
    private JTable autorsTable;
    public VistaAutors(CtrlPresentacio ctrl) {
        this.ctrl = ctrl;
        initVistaAutors();
    }

    public void initVistaAutors(){
        this.setLayout(new BorderLayout());
        initTable();
    }

    private void initTable() {
        String[] columnsNames = {"Author name", "Number of Documents", "Documents list"};
        Object [][] data = ctrl.getAutorsData(); //TODO AUTORS
        autorsTable = new JTable(new MyTableModel(data, columnsNames));

        JPanel panel = new JPanel(new GridBagLayout());
        autorsTable.getTableHeader().setReorderingAllowed(true);
        autorsTable.getTableHeader().setReorderingAllowed(false);
        autorsTable.setRowHeight(autorsTable.getRowHeight()+9);
        autorsTable.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        //autorsTable.getColumnModel().getColumn(1).setCellRenderer(new MultiLineTableCellRenderer(ctrl));
        //autorsTable.getColumnModel().getColumn(1).setCellEditor(new MultiLineTableCellRenderer(ctrl));
        autorsTable.setShowGrid(true);
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(autorsTable.getModel());
        autorsTable.setRowSorter(rowSorter);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        this.add(autorsTable.getTableHeader(), BorderLayout.NORTH);
        this.add(autorsTable, BorderLayout.CENTER);
        this.add(panel, BorderLayout.SOUTH);
    }

    public class MyTableModel extends DefaultTableModel {
        private MyTableModel(Object[][] tableData, Object[] columnsNames){
            super(tableData, columnsNames);
        }

        public boolean isCellEditable(int row, int column) { return false;}
    }

    public void updateTable(boolean required){
        DefaultTableModel model = (DefaultTableModel) autorsTable.getModel();
        model.setRowCount(0);
        Object[][] auxi = ctrl.getAutorsData();
        for (Object[] anAuxi : auxi) { model.addRow(anAuxi); }
        autorsTable.setModel(model);
        updateRowHeights();
        if (required) {updateLlibreria();}
    }

    public void updateLlibreria(){
        JTabbedPane parent = (JTabbedPane) getParent();
        MainWindow mw = (MainWindow) SwingUtilities.getRoot(parent);
        mw.updateLlibreria(false);
    }

    private void updateRowHeights() {
        for (int row = 0; row < autorsTable.getRowCount(); row++) {
            int rowHeight = autorsTable.getRowHeight();
            for (int column = 0; column < autorsTable.getColumnCount(); column++) {
                Component comp = autorsTable.prepareRenderer(autorsTable.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }
            autorsTable.setRowHeight(row, rowHeight);
        }
    }
}

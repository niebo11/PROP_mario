import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Arrays;

public class MultiLineTableCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    CtrlPresentacio ctrl;

    MultiLineTableCellRenderer(CtrlPresentacio ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof String) return generateLabel((String) value, isSelected);
        return generateLabel((Frase) value, isSelected);
    }

    private JLabel generateLabel(Frase value, boolean isSelected) {
        JLabel l = new JLabel();
        //make multi line where the cell value is String[]
        l.setText(value.toString());
        //cell backgroud color when selected
        if (isSelected) {
            l.setBackground(UIManager.getColor("Table.selectionBackground"));
        } else {
            l.setBackground(UIManager.getColor("Table.background"));
        }

        return l;
    }

    private JLabel generateLabel(String value, boolean isSelected) {
        JLabel l = new JLabel();
        //make multi line where the cell value is String[]
        l.setText(value);
        //cell backgroud color when selected
        if (isSelected) {
            l.setBackground(UIManager.getColor("Table.selectionBackground"));
        } else {
            l.setBackground(UIManager.getColor("Table.background"));
        }
        return l;
    }

    String[] s;

    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        DefaultTableModel dtm = (DefaultTableModel)jTable.getModel();
        String documentName = (String)dtm.getValueAt(i,0);
        String AutorName = (String)dtm.getValueAt(i,1);
        return new JLabel(String.join(",", new String[]{documentName, AutorName}));
    }

    @Override
    public Object getCellEditorValue() {
        return s;
    }
}

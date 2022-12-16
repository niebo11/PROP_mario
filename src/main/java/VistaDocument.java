import java.awt.*;
import static java.awt.GridBagConstraints.*;
import javax.swing.*;

public class VistaDocument extends JPanel {

    //GB arguments:
    private int gridx, gridy, gridwidth, gridheight, fill, anchor, ipadx, ipady;
    private double weightx, weighty;
    private Insets insets;
    // GB Insets:
    private int top, left, bottom, right;
    private final Insets insetsTop = new Insets(top = 5, left = 0, bottom = 15, right = 0);
    private final Insets insetsLabel = new Insets(top = 0, left = 10, bottom = 6, right = 5);
    private final Insets insetsText = new Insets(top = 0, left = 0, bottom = 6, right = 10);
    private final Insets insetsBottom = new Insets(top = 10, left = 0, bottom = 10, right = 0);
    //input fields:
    private JTextField name;
    private JTextField author;
    private JTextArea content;

    public VistaDocument() {
        setLayout(new GridBagLayout());
        initLayout();
    }

    public String getName(){
        return name.getText();
    }

    public String getAuthor(){
        return author.getText();
    }

    public String getContent(){
        return content.getText();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(320, 240);
    }

    private void initLayout() {
        //header row:
        setDefaultValuesGB();// default values for all GridBagConstraints
        addGB(new JLabel("Add Document"), gridx = 1, gridy = 1,
                gridwidth = 2, gridheight, fill,
                weightx = 1.0, weighty, anchor,
                insets = insetsTop);
        //name row (label):
        setDefaultValuesGB();// default values for all GridBagConstraints
        addGB(new JLabel("Document name"), gridx = 1, gridy = 2,
                gridwidth, gridheight, fill,
                weightx, weighty, anchor = LINE_START,
                insets = insetsLabel);
        //name row (textfield):
        name = new JTextField();
        setDefaultValuesGB();// default values for all GridBagConstraints
        addGB(name, gridx = 2, gridy = 2,
                gridwidth, gridheight, fill = HORIZONTAL,
                weightx = 1.0, weighty, anchor,
                insets = insetsText);
        //age row (label):
        setDefaultValuesGB();// default values for all GridBagConstraints
        addGB(new JLabel("Author name"), gridx = 1, gridy = 3,
                gridwidth, gridheight, fill,
                weightx, weighty, anchor = LINE_START,
                insets = insetsLabel);
        //age row (textfield):
        author = new JTextField();
        setDefaultValuesGB();// default values for all GridBagConstraints
        addGB(author, gridx = 2, gridy = 3,
                gridwidth, gridheight, fill = HORIZONTAL,
                weightx = 1.0, weighty, anchor,
                insets = insetsText);
        //comment row (label):
        setDefaultValuesGB();// default values for all GridBagConstraints
        addGB(new JLabel("Content"), gridx = 1, gridy = 4,
                gridwidth, gridheight, fill,
                weightx, weighty, anchor = FIRST_LINE_START,
                insets = insetsLabel);
        //comment row (textfield):
        content = new JTextArea();
        setDefaultValuesGB();// default values for all GridBagConstraints
        addGB(new JScrollPane(content), gridx = 2, gridy = 4,
                gridwidth, gridheight, fill = BOTH,
                weightx = 1.0, weighty = 1.0, anchor,
                insets = insetsText);
    }

    // Convenience method, used to add components without internal padding:
    private void addGB(final Component component, final int gridx, final int gridy,
                       final int gridwidth, final int gridheight,
                       final int fill, final double weightx, final double weighty,
                       final int anchor, final Insets insets) {
        addGB(component, gridx, gridy, gridwidth, gridheight, fill, weightx, weighty, anchor, insets, ipadx, ipady);
    }

    private void addGB(final Component component, final int gridx, final int gridy,
                       final int gridwidth, final int gridheight,
                       final int fill, final double weightx, final double weighty,
                       final int anchor, final Insets insets,
                       final int ipadx, final int ipady) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.fill = fill;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        constraints.anchor = anchor;
        constraints.insets = insets;
        constraints.ipadx = ipadx;
        constraints.ipady = ipady;
        add(component, constraints);
    }

    private void setDefaultValuesGB() {
        // This method sets the default values for all GridBagConstraints,
        // so we don't have to specify them with each addGB(...)
        gridx = RELATIVE;
        gridy = RELATIVE;
        gridwidth = 1;
        gridheight = 1;
        fill = NONE;
        weightx = 0.0;
        weighty = 0.0;
        anchor = CENTER;
        insets = new Insets(0, 0, 0, 0);
        ipadx = 0;
        ipady = 0;
    }
}
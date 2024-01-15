package lab3;

import fuzzy.FuzzyDefuzzy;
import fuzzy.FuzzyToken;
import fuzzy.FuzzyValue;
import fuzzy.TwoXTwoTable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Lab3GUI {

    private JSlider sliderX1;
    private JSlider sliderX2;
    private JSpinner spinnerW1;
    private JTabbedPane tabs;
    private JButton executeButton;
    private JTable tableinverter;
    private JTable tableaddsub;
    private JSpinner spinnerW2;
    private JLabel x1plabel;
    private JLabel x2plabel;
    private JLabel x1label;
    private JLabel x2label;
    private JTextPane computationPane;
    private JPanel panel;

    private double x1p, x2p;
    private TwoXTwoTable currentTable = InversorExemple.createInversor();

    private Lab3GUI() {
        super();

        ChangeListener inputChange = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                DecimalFormat df = new DecimalFormat("#.####");
                double x1 = sliderX1.getValue() / 100.0 * 2 - 1;
                double x2 = sliderX2.getValue() / 100.0 * 2 - 1;
                double w1 = (double) spinnerW1.getValue();
                double w2 = (double) spinnerW2.getValue();

                x1p = x1 * w1;
                x2p = x2 * w2;

                x1label.setText("x1 = " + df.format(x1));
                x2label.setText("x2 = " + df.format(x2));
                x1plabel.setText("x1' = " + df.format(x1p));
                x2plabel.setText("x2' = " + df.format(x2p));
            }
        };

        spinnerW1.setModel(new SpinnerNumberModel(1.0, -5.0, 5.0, 0.1));
        spinnerW2.setModel(new SpinnerNumberModel(1.0, -5.0, 5.0, 0.1));

        sliderX1.addChangeListener(inputChange);
        sliderX2.addChangeListener(inputChange);
        spinnerW1.addChangeListener(inputChange);
        spinnerW2.addChangeListener(inputChange);

        populateOperationTable(tableinverter, InversorExemple.createInversor());
        populateOperationTable(tableaddsub, AdderSubtractor.createTable());

        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int tabIndex = tabs.getSelectedIndex();
                if(tabIndex == 0) currentTable = InversorExemple.createInversor();
                if(tabIndex == 1) currentTable = AdderSubtractor.createTable();
            }
        });

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FuzzyDefuzzy fuzDefuz = new FuzzyDefuzzy(-1, -0.5, 0, 0.5, 1);
                FuzzyToken inpToken1 = fuzDefuz.fuzzyfie(x1p);
                FuzzyToken inpToken2 = fuzDefuz.fuzzyfie(x2p);

                FuzzyToken[] out = currentTable.execute(inpToken1, inpToken2);

                DecimalFormat df = new DecimalFormat("#.####");

                computationPane.setText(
                        "fuzzify(x1') = " + inpToken1 + "\n" +
                        "fuzzify(x2') = " + inpToken2 + "\n" +
                        "x3 = " + out[0] + " --> " + df.format(fuzDefuz.defuzzify(out[0])) + "\n" +
                        "x4 = " + out[1] + " --> " + df.format(fuzDefuz.defuzzify(out[1])));
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Petri GUI");
        frame.setContentPane(new Lab3GUI().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void populateOperationTable(JTable table, TwoXTwoTable operation) {
        DefaultTableModel model = new DefaultTableModel();

        String[] headerValues = new String[6];
        headerValues[0] = "";
        model.addColumn("");
        for(int i = 0; i < 5; i++) {
            headerValues[i+1] = FuzzyValue.values()[i].name();
            model.addColumn(FuzzyValue.values()[i].name());
        }
        model.addRow(headerValues);

        for(FuzzyValue row : FuzzyValue.values()) {
            String[] rowValues = new String[6];
            rowValues[0] = row.name();
            for(int i = 0; i < 5; i++) {
                FuzzyValue col = FuzzyValue.values()[i];
                FuzzyValue[] res = operation.get(row, col);
                rowValues[i+1] = res[0].name() + ", " + res[1].name();
            }
            model.addRow(rowValues);
        }
        table.setModel(model);
    }
}

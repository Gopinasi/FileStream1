import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductMaker extends JFrame {
    private JTextField idField, nameField, descField, costField, recordCountField;
    private JButton addButton, resetButton, quitButton;
    private int recordCount = 0;

    private File productFile = new File("productData.dat");
    private RandomAccessFile randomAccessFile;

    public RandProductMaker() {
        super("Product Maker");

        try {
            randomAccessFile = new RandomAccessFile(productFile, "rw");
            randomAccessFile.setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        setLayout(new GridLayout(15, 3));

        idField = new JTextField(6);
        nameField = new JTextField(35);
        descField = new JTextField(75);
        costField = new JTextField(11);
        recordCountField = new JTextField("0", 5);
        recordCountField.setEditable(false);

        addButton = new JButton("Add Product");
        resetButton = new JButton("Reset");
        quitButton = new JButton("Quit");

        add(new JLabel("ID:"));
        add(idField);
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Description:"));
        add(descField);
        add(new JLabel("Cost:"));
        add(costField);

        add(addButton);

        add(new JLabel("Record Count:"));
        add(recordCountField);

        add(resetButton);
        add(quitButton);

        addButton.addActionListener(new AddProductListener());
        resetButton.addActionListener(new ResetListener());
        quitButton.addActionListener(e -> System.exit(0));

        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class AddProductListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String id = formatString(idField.getText(), 6);
                String name = formatString(nameField.getText(), 35);
                String desc = formatString(descField.getText(), 75);
                double cost = Double.parseDouble(costField.getText());

                Product product = new Product(id, name, desc, cost);
                product.writeToFile(randomAccessFile);

                recordCount++;
                recordCountField.setText(String.valueOf(recordCount));

                // Clear fields
                idField.setText("");
                nameField.setText("");
                descField.setText("");
                costField.setText("");

            } catch (IOException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding product. Please check the inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ResetListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                randomAccessFile.setLength(0);

                recordCount = 0;
                recordCountField.setText("0");

                idField.setText("");
                nameField.setText("");
                descField.setText("");
                costField.setText("");

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error resetting data file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String formatString(String text, int length) {
        return String.format("%-" + length + "s", text).substring(0, length);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RandProductMaker::new);
    }
}
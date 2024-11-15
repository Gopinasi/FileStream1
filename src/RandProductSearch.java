import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductSearch extends JFrame {
    private JTextField searchField;
    private JTextArea resultArea;
    private JButton searchButton;
    private JButton clearButton;
    private JButton quitButton;
    private final static int NAME_LENGTH = 35;
    private final static int DESC_LENGTH = 75;
    private final static int ID_LENGTH = 11;
    private final static int COST_LENGTH = 7;

    private File productFile = new File("productData.dat");  // Ensure this matches the filename used in RandProductMaker
    private RandomAccessFile randomAccessFile;

    public RandProductSearch() {
        super("Product Search");

        try {
            randomAccessFile = new RandomAccessFile(productFile, "r");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        clearButton = new JButton("Clear");
        quitButton = new JButton("Quit");

        topPanel.add(new JLabel("Enter partial product name:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(clearButton);
        topPanel.add(quitButton);

        resultArea = new JTextArea(5, 20);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(new SearchListener());

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                resultArea.setText("");
            }
        });

        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);  // Close the application
            }
        });

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class SearchListener implements ActionListener {
        private static final int RECORD_LENGTH = 129;

        private String readString(RandomAccessFile randomAccessFile, int length) throws IOException {
            byte[] bytes = new byte[length];
            randomAccessFile.read(bytes);
            return new String(bytes).trim();
        }

        private Product getRecordByIndex(RandomAccessFile randomAccessFile, int index) throws IOException {
            long position = index * RECORD_LENGTH;
            randomAccessFile.seek(position);

            String id = readString(randomAccessFile, ID_LENGTH);
            String name = readString(randomAccessFile, NAME_LENGTH);
            String desc = readString(randomAccessFile, DESC_LENGTH);
            String costStr = readString(randomAccessFile, COST_LENGTH).trim();
            double cost = Double.parseDouble(costStr);

            if (!costStr.isEmpty()) {
                try {
                    cost = Double.parseDouble(costStr);
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid cost value: " + costStr);
                }
            }

            return new Product(id, name, desc, cost);
        }

        public void actionPerformed(ActionEvent e) {
            String partialName = searchField.getText().trim().toLowerCase();
            if (partialName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter partial product name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                randomAccessFile.seek(0);
                resultArea.setText("");

                boolean found = false;
                while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                    long startPointer = randomAccessFile.getFilePointer();

                    Product product = getRecordByIndex(randomAccessFile, (int) (randomAccessFile.getFilePointer() / RECORD_LENGTH));
                    String name = product.getName().toLowerCase();

                    if (name.contains(partialName)) {
                        resultArea.append("ID: " + product.getID() + "\n");
                        resultArea.append("Name: " + product.getName() + "\n");
                        resultArea.append("Description: " + product.getDesc() + "\n");
                        resultArea.append("Cost: " + product.getCost() + "\n");
                        resultArea.append("----------------------------------------\n");
                        found = true;
                    }

                    randomAccessFile.seek(startPointer + RECORD_LENGTH);
                }

                if (!found) {
                    resultArea.append("No products found matching '" + partialName + "'.\n");
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error reading the product data file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RandProductSearch::new);
    }
}
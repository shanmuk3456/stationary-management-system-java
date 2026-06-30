import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SalesWindow extends Frame {
    private Database db;
    private Choice itemChoice;
    private TextField quantityField, priceField, totalField;
    private TextArea salesArea;
    private Label messageLabel, stockLabel;
    
    public SalesWindow(Database db) {
        this.db = db;
        setTitle("Sales Management");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        // Top panel for input
        Panel inputPanel = new Panel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Item selection
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new Label("Select Item:"), gbc);
        itemChoice = new Choice();
        itemChoice.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        inputPanel.add(itemChoice, gbc);
        
        stockLabel = new Label("", Label.LEFT);
        stockLabel.setForeground(Color.BLUE);
        gbc.gridx = 2;
        inputPanel.add(stockLabel, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new Label("Quantity:"), gbc);
        quantityField = new TextField(10);
        gbc.gridx = 1;
        inputPanel.add(quantityField, gbc);
        
        // Price per unit
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new Label("Price per Unit:"), gbc);
        priceField = new TextField(10);
        priceField.setEditable(false);
        gbc.gridx = 1;
        inputPanel.add(priceField, gbc);
        
        // Total
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new Label("Total Price:"), gbc);
        totalField = new TextField(10);
        totalField.setEditable(false);
        gbc.gridx = 1;
        inputPanel.add(totalField, gbc);
        
        // Buttons
        Panel buttonPanel = new Panel(new FlowLayout());
        Button recordButton = new Button("Record Sale");
        Button clearButton = new Button("Clear");
        Button refreshButton = new Button("Refresh");
        
        buttonPanel.add(recordButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);
        
        // Message label
        messageLabel = new Label("", Label.CENTER);
        messageLabel.setForeground(Color.RED);
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        inputPanel.add(messageLabel, gbc);
        
        // Sales display area
        salesArea = new TextArea(15, 80);
        salesArea.setEditable(false);
        salesArea.setFont(new Font("Courier", Font.PLAIN, 12));
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(salesArea);
        
        // Layout
        Panel topPanel = new Panel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(new Label("Sales History:", Label.LEFT), BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        Button backButton = new Button("Back to Main Menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(backButton, BorderLayout.SOUTH);
        
        // Item choice listener
        itemChoice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateItemDetails();
            }
        });
        
        // Quantity field listener for auto-calculate total
        quantityField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateTotal();
            }
        });
        
        quantityField.addTextListener(new TextListener() {
            @Override
            public void textValueChanged(TextEvent e) {
                calculateTotal();
            }
        });
        
        // Button actions
        recordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recordSale();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshItems();
                refreshSales();
            }
        });
        
        // Window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        
        refreshItems();
        refreshSales();
        setVisible(true);
    }
    
    private void refreshItems() {
        itemChoice.removeAll();
        List<String[]> items = db.getAllItems();
        
        for (String[] item : items) {
            itemChoice.add(item[0] + " - " + item[1] + " (Qty: " + item[3] + ")");
        }
        
        if (items.size() > 0) {
            updateItemDetails();
        }
    }
    
    private void updateItemDetails() {
        if (itemChoice.getItemCount() == 0) {
            return;
        }
        
        String selected = itemChoice.getSelectedItem();
        if (selected != null) {
            int itemId = Integer.parseInt(selected.split(" - ")[0]);
            String[] item = db.getItemById(itemId);
            
            if (item != null) {
                priceField.setText(item[5]);
                stockLabel.setText("Stock: " + item[3] + " | Status: " + item[6]);
                
                if (item[6].equals("Out of Stock")) {
                    stockLabel.setForeground(Color.RED);
                } else if (item[6].equals("Low Stock")) {
                    stockLabel.setForeground(Color.ORANGE);
                } else {
                    stockLabel.setForeground(Color.GREEN);
                }
                
                calculateTotal();
            }
        }
    }
    
    private void calculateTotal() {
        try {
            String quantityText = quantityField.getText().trim();
            String priceText = priceField.getText().trim();
            
            if (!quantityText.isEmpty() && !priceText.isEmpty()) {
                int quantity = Integer.parseInt(quantityText);
                double price = Double.parseDouble(priceText);
                double total = quantity * price;
                totalField.setText(String.format("%.2f", total));
            } else {
                totalField.setText("");
            }
        } catch (NumberFormatException e) {
            totalField.setText("");
        }
    }
    
    private void recordSale() {
        try {
            if (itemChoice.getItemCount() == 0) {
                showMessage("No items available!", Color.RED);
                return;
            }
            
            String selected = itemChoice.getSelectedItem();
            if (selected == null) {
                showMessage("Please select an item!", Color.RED);
                return;
            }
            
            int itemId = Integer.parseInt(selected.split(" - ")[0]);
            String quantityText = quantityField.getText().trim();
            
            if (quantityText.isEmpty()) {
                showMessage("Please enter quantity!", Color.RED);
                return;
            }
            
            int quantity = Integer.parseInt(quantityText);
            double totalPrice = Double.parseDouble(totalField.getText().trim());
            
            if (quantity <= 0) {
                showMessage("Quantity must be greater than 0!", Color.RED);
                return;
            }
            
            if (db.recordSale(itemId, quantity, totalPrice)) {
                showMessage("Sale recorded successfully!", Color.GREEN);
                clearFields();
                refreshItems();
                refreshSales();
            } else {
                showMessage("Failed to record sale! Check stock availability.", Color.RED);
            }
        } catch (NumberFormatException e) {
            showMessage("Invalid input format!", Color.RED);
        }
    }
    
    private void clearFields() {
        quantityField.setText("");
        totalField.setText("");
        messageLabel.setText("");
        if (itemChoice.getItemCount() > 0) {
            updateItemDetails();
        }
    }
    
    private void refreshSales() {
        List<String[]> sales = db.getAllSales();
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s %-20s %-10s %-12s %-20s%n",
            "Sale ID", "Item Name", "Quantity", "Total Price", "Sale Date"));
        sb.append("--------------------------------------------------------------------------------\n");
        
        for (String[] sale : sales) {
            sb.append(String.format("%-8s %-20s %-10s %-12s %-20s%n",
                sale[0], sale[1], sale[2], sale[3], sale[4]));
        }
        
        salesArea.setText(sb.toString());
    }
    
    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }
}



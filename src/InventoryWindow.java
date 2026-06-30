import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class InventoryWindow extends Frame {
    private Database db;
    private TextField idField, nameField, categoryField, quantityField, thresholdField, priceField;
    private TextArea itemsArea;
    private Label messageLabel;
    
    public InventoryWindow(Database db) {
        this.db = db;
        setTitle("Inventory Management");
        setSize(900, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        // Top panel for input fields
        Panel inputPanel = new Panel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // ID
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new Label("Item ID:"), gbc);
        idField = new TextField(10);
        idField.setEditable(false);
        gbc.gridx = 1;
        inputPanel.add(idField, gbc);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new Label("Name:"), gbc);
        nameField = new TextField(20);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new Label("Category:"), gbc);
        categoryField = new TextField(20);
        gbc.gridx = 1;
        inputPanel.add(categoryField, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new Label("Quantity:"), gbc);
        quantityField = new TextField(10);
        gbc.gridx = 1;
        inputPanel.add(quantityField, gbc);
        
        // Threshold
        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new Label("Threshold:"), gbc);
        thresholdField = new TextField(10);
        gbc.gridx = 1;
        inputPanel.add(thresholdField, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 5;
        inputPanel.add(new Label("Price:"), gbc);
        priceField = new TextField(10);
        gbc.gridx = 1;
        inputPanel.add(priceField, gbc);
        
        // Buttons
        Panel buttonPanel = new Panel(new FlowLayout());
        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        Button clearButton = new Button("Clear");
        Button refreshButton = new Button("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);
        
        // Message label
        messageLabel = new Label("", Label.CENTER);
        messageLabel.setForeground(Color.RED);
        gbc.gridy = 7;
        inputPanel.add(messageLabel, gbc);
        
        // Items display area
        itemsArea = new TextArea(15, 80);
        itemsArea.setEditable(false);
        itemsArea.setFont(new Font("Courier", Font.PLAIN, 12));
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(itemsArea);
        
        // Layout
        Panel topPanel = new Panel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(new Label("Items List:", Label.LEFT), BorderLayout.CENTER);
        
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
        
        // Button actions
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateItem();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteItem();
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
            }
        });
        
        // Double-click on items area to select item
        itemsArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectItemFromList();
                }
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
        setVisible(true);
    }
    
    private void addItem() {
        try {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            
            if (name.isEmpty() || category.isEmpty()) {
                showMessage("Please fill all fields!", Color.RED);
                return;
            }
            
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int threshold = Integer.parseInt(thresholdField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            
            // Check if item already exists
            if (db.itemExists(name)) {
                String[] existingItem = db.getItemByName(name);
                if (existingItem != null) {
                    // Show dialog to merge or update
                    Dialog mergeDialog = new Dialog(this, "Item Already Exists", true);
                    mergeDialog.setLayout(new BorderLayout());
                    mergeDialog.setSize(400, 200);
                    mergeDialog.setLocationRelativeTo(this);
                    
                    Panel messagePanel = new Panel();
                    messagePanel.setLayout(new GridLayout(3, 1));
                    messagePanel.add(new Label("Item '" + name + "' already exists!", Label.CENTER));
                    messagePanel.add(new Label("Current quantity: " + existingItem[3], Label.CENTER));
                    messagePanel.add(new Label("What would you like to do?", Label.CENTER));
                    
                    Panel buttonPanel = new Panel(new FlowLayout());
                    Button mergeButton = new Button("Merge Quantities (Add " + quantity + ")");
                    Button updateButton = new Button("Load for Update");
                    Button cancelButton = new Button("Cancel");
                    
                    mergeButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String result = db.mergeItemQuantity(name, quantity);
                            if (result == null) {
                                showMessage("Quantities merged successfully! New quantity: " + 
                                    (Integer.parseInt(existingItem[3]) + quantity), Color.GREEN);
                                clearFields();
                                refreshItems();
                            } else {
                                showMessage(result, Color.RED);
                            }
                            mergeDialog.dispose();
                        }
                    });
                    
                    updateButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            idField.setText(existingItem[0]);
                            nameField.setText(existingItem[1]);
                            categoryField.setText(existingItem[2]);
                            quantityField.setText(existingItem[3]);
                            thresholdField.setText(existingItem[4]);
                            priceField.setText(existingItem[5]);
                            showMessage("Item loaded for update. Modify and click Update button.", Color.BLUE);
                            mergeDialog.dispose();
                        }
                    });
                    
                    cancelButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            mergeDialog.dispose();
                        }
                    });
                    
                    buttonPanel.add(mergeButton);
                    buttonPanel.add(updateButton);
                    buttonPanel.add(cancelButton);
                    
                    mergeDialog.add(messagePanel, BorderLayout.CENTER);
                    mergeDialog.add(buttonPanel, BorderLayout.SOUTH);
                    mergeDialog.setVisible(true);
                    return;
                }
            }
            
            // Item doesn't exist, add as new
            String result = db.addItem(name, category, quantity, threshold, price);
            if (result == null) {
                // Success
                showMessage("Item added successfully!", Color.GREEN);
                clearFields();
                refreshItems();
            } else {
                // Error message returned
                showMessage(result, Color.RED);
            }
        } catch (NumberFormatException e) {
            showMessage("Invalid number format!", Color.RED);
        }
    }
    
    private void updateItem() {
        try {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                showMessage("Please select an item to update!", Color.RED);
                return;
            }
            
            int itemId = Integer.parseInt(idText);
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int threshold = Integer.parseInt(thresholdField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            
            if (name.isEmpty() || category.isEmpty()) {
                showMessage("Please fill all fields!", Color.RED);
                return;
            }
            
            String result = db.updateItem(itemId, name, category, quantity, threshold, price);
            if (result == null) {
                // Success
                showMessage("Item updated successfully!", Color.GREEN);
                clearFields();
                refreshItems();
            } else {
                // Error message returned
                showMessage(result, Color.RED);
            }
        } catch (NumberFormatException e) {
            showMessage("Invalid number format!", Color.RED);
        }
    }
    
    private void deleteItem() {
        try {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                showMessage("Please select an item to delete!", Color.RED);
                return;
            }
            
            int itemId = Integer.parseInt(idText);
            
            // Confirm deletion
            Dialog confirmDialog = new Dialog(this, "Confirm Delete", true);
            confirmDialog.setLayout(new FlowLayout());
            confirmDialog.add(new Label("Are you sure you want to delete this item?"));
            Button yesButton = new Button("Yes");
            Button noButton = new Button("No");
            
            yesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (db.deleteItem(itemId)) {
                        showMessage("Item deleted successfully!", Color.GREEN);
                        clearFields();
                        refreshItems();
                    } else {
                        showMessage("Failed to delete item!", Color.RED);
                    }
                    confirmDialog.dispose();
                }
            });
            
            noButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    confirmDialog.dispose();
                }
            });
            
            confirmDialog.add(yesButton);
            confirmDialog.add(noButton);
            confirmDialog.setSize(300, 100);
            confirmDialog.setLocationRelativeTo(this);
            confirmDialog.setVisible(true);
        } catch (NumberFormatException e) {
            showMessage("Invalid item ID!", Color.RED);
        }
    }
    
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        categoryField.setText("");
        quantityField.setText("");
        thresholdField.setText("");
        priceField.setText("");
        messageLabel.setText("");
    }
    
    private void refreshItems() {
        db.checkStockLevels(); // Update stock status
        List<String[]> items = db.getAllItems();
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s %-20s %-15s %-10s %-10s %-10s %-15s%n",
            "ID", "Name", "Category", "Quantity", "Threshold", "Price", "Status"));
        sb.append("----------------------------------------------------------------------------------------\n");
        
        for (String[] item : items) {
            sb.append(String.format("%-8s %-20s %-15s %-10s %-10s %-10s %-15s%n",
                item[0], item[1], item[2], item[3], item[4], item[5], item[6]));
        }
        
        itemsArea.setText(sb.toString());
    }
    
    private void selectItemFromList() {
        String selectedText = itemsArea.getSelectedText();
        if (selectedText != null && !selectedText.trim().isEmpty()) {
            String[] parts = selectedText.trim().split("\\s+");
            if (parts.length > 0) {
                try {
                    int itemId = Integer.parseInt(parts[0]);
                    String[] item = db.getItemById(itemId);
                    if (item != null) {
                        idField.setText(item[0]);
                        nameField.setText(item[1]);
                        categoryField.setText(item[2]);
                        quantityField.setText(item[3]);
                        thresholdField.setText(item[4]);
                        priceField.setText(item[5]);
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
    }
    
    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }
}



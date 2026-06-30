import java.awt.*;
import java.awt.event.*;

public class MainMenuWindow extends Frame {
    private Database db;
    
    public MainMenuWindow(Database db) {
        this.db = db;
        setTitle("Stationary Management System - Main Menu");
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        // Title panel
        Panel titlePanel = new Panel();
        titlePanel.setBackground(new Color(70, 130, 180));
        Label titleLabel = new Label("Stationary Management System", Label.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Menu panel
        Panel menuPanel = new Panel();
        menuPanel.setLayout(new GridLayout(4, 1, 10, 10));
        
        Button inventoryButton = new Button("Manage Inventory");
        inventoryButton.setFont(new Font("Arial", Font.BOLD, 14));
        inventoryButton.setPreferredSize(new Dimension(200, 50));
        
        Button salesButton = new Button("Record Sales");
        salesButton.setFont(new Font("Arial", Font.BOLD, 14));
        salesButton.setPreferredSize(new Dimension(200, 50));
        
        Button reportsButton = new Button("Stock Reports");
        reportsButton.setFont(new Font("Arial", Font.BOLD, 14));
        reportsButton.setPreferredSize(new Dimension(200, 50));
        
        Button logoutButton = new Button("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setPreferredSize(new Dimension(200, 50));
        
        menuPanel.add(inventoryButton);
        menuPanel.add(salesButton);
        menuPanel.add(reportsButton);
        menuPanel.add(logoutButton);
        
        add(menuPanel, BorderLayout.CENTER);
        
        // Button actions
        inventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InventoryWindow(db);
            }
        });
        
        salesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SalesWindow(db);
            }
        });
        
        reportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStockReport();
            }
        });
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginWindow(db);
            }
        });
        
        // Window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                db.close();
                System.exit(0);
            }
        });
        
        setVisible(true);
    }
    
    private void showStockReport() {
        Frame reportFrame = new Frame("Stock Report - Low/Out of Stock Items");
        reportFrame.setSize(700, 400);
        reportFrame.setLocationRelativeTo(null);
        reportFrame.setLayout(new BorderLayout());
        
        // Update stock levels before showing report
        db.checkStockLevels();
        
        java.util.List<String[]> lowStockItems = db.getLowStockItems();
        
        // Header
        Panel headerPanel = new Panel(new GridLayout(1, 7));
        headerPanel.add(new Label("ID", Label.CENTER));
        headerPanel.add(new Label("Name", Label.CENTER));
        headerPanel.add(new Label("Category", Label.CENTER));
        headerPanel.add(new Label("Quantity", Label.CENTER));
        headerPanel.add(new Label("Threshold", Label.CENTER));
        headerPanel.add(new Label("Price", Label.CENTER));
        headerPanel.add(new Label("Status", Label.CENTER));
        
        // Items panel
        Panel itemsPanel = new Panel();
        itemsPanel.setLayout(new GridLayout(Math.max(lowStockItems.size(), 1), 7));
        
        if (lowStockItems.isEmpty()) {
            Label noItemsLabel = new Label("No low stock or out of stock items!", Label.CENTER);
            noItemsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            itemsPanel.add(noItemsLabel);
            for (int i = 0; i < 6; i++) {
                itemsPanel.add(new Label(""));
            }
        } else {
            for (String[] item : lowStockItems) {
                for (int i = 0; i < 7; i++) {
                    Label label = new Label(item[i], Label.CENTER);
                    if (i == 6) { // Status column
                        if (item[i].equals("Out of Stock")) {
                            label.setForeground(Color.RED);
                        } else if (item[i].equals("Low Stock")) {
                            label.setForeground(Color.ORANGE);
                        }
                    }
                    itemsPanel.add(label);
                }
            }
        }
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(itemsPanel);
        
        reportFrame.add(headerPanel, BorderLayout.NORTH);
        reportFrame.add(scrollPane, BorderLayout.CENTER);
        
        Button closeButton = new Button("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reportFrame.dispose();
            }
        });
        
        Panel buttonPanel = new Panel();
        buttonPanel.add(closeButton);
        reportFrame.add(buttonPanel, BorderLayout.SOUTH);
        
        reportFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                reportFrame.dispose();
            }
        });
        
        reportFrame.setVisible(true);
    }
}



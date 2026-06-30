import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stationary_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Nani"; // Change this to your MySQL password
    
    private Connection connection;
    
    public Database() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed!");
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    // Login verification
    public boolean verifyLogin(String username, String password) {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return false;
        }
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Check stock levels and update status using ResultSet cursor
    public void checkStockLevels() {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return;
        }
        String query = "SELECT item_id, quantity, threshold FROM items";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Using ResultSet as cursor to iterate through records
            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                int quantity = rs.getInt("quantity");
                int threshold = rs.getInt("threshold");
                
                String status;
                if (quantity == 0) {
                    status = "Out of Stock";
                } else if (quantity < threshold) {
                    status = "Low Stock";
                } else {
                    status = "Available";
                }
                
                updateItemStatus(itemId, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Update item status
    public void updateItemStatus(int itemId, String status) {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return;
        }
        String query = "UPDATE items SET status = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Check if item with same name exists
    public boolean itemExists(String name) {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return false;
        }
        String query = "SELECT COUNT(*) FROM items WHERE LOWER(TRIM(name)) = LOWER(TRIM(?))";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Get item by name (case-insensitive)
    public String[] getItemByName(String name) {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return null;
        }
        String query = "SELECT * FROM items WHERE LOWER(TRIM(name)) = LOWER(TRIM(?)) LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt("item_id")),
                    rs.getString("name"),
                    rs.getString("category"),
                    String.valueOf(rs.getInt("quantity")),
                    String.valueOf(rs.getInt("threshold")),
                    String.valueOf(rs.getDouble("price")),
                    rs.getString("status")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Merge quantities for duplicate items (adds quantity to existing item)
    public String mergeItemQuantity(String name, int additionalQuantity) {
        String[] existingItem = getItemByName(name);
        if (existingItem == null) {
            return "Item not found!";
        }
        
        int itemId = Integer.parseInt(existingItem[0]);
        int currentQuantity = Integer.parseInt(existingItem[3]);
        int newQuantity = currentQuantity + additionalQuantity;
        int threshold = Integer.parseInt(existingItem[4]);
        
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return "Database connection is not available!";
        }
        String status = newQuantity >= threshold ? "Available" : (newQuantity > 0 ? "Low Stock" : "Out of Stock");
        String query = "UPDATE items SET quantity = ?, status = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, newQuantity);
            stmt.setString(2, status);
            stmt.setInt(3, itemId);
            stmt.executeUpdate();
            return null; // Success
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }
    
    // Add new item (returns error message if duplicate, null if success)
    public String addItem(String name, String category, int quantity, int threshold, double price) {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return "Database connection is not available!";
        }
        // Check if item with same name already exists
        if (itemExists(name)) {
            return "Item with name '" + name + "' already exists! Please update the existing item instead.";
        }
        
        String status = quantity >= threshold ? "Available" : (quantity > 0 ? "Low Stock" : "Out of Stock");
        String query = "INSERT INTO items (name, category, quantity, threshold, price, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name.trim());
            stmt.setString(2, category.trim());
            stmt.setInt(3, quantity);
            stmt.setInt(4, threshold);
            stmt.setDouble(5, price);
            stmt.setString(6, status);
            stmt.executeUpdate();
            return null; // Success - no error message
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }
    
    // Get all items
    public List<String[]> getAllItems() {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return new ArrayList<>();
        }
        List<String[]> items = new ArrayList<>();
        String query = "SELECT * FROM items ORDER BY item_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String[] item = {
                    String.valueOf(rs.getInt("item_id")),
                    rs.getString("name"),
                    rs.getString("category"),
                    String.valueOf(rs.getInt("quantity")),
                    String.valueOf(rs.getInt("threshold")),
                    String.valueOf(rs.getDouble("price")),
                    rs.getString("status")
                };
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    // Check if item name exists for a different item ID
    public boolean itemNameExistsForOtherItem(int itemId, String name) {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return false;
        }
        String query = "SELECT COUNT(*) FROM items WHERE LOWER(TRIM(name)) = LOWER(TRIM(?)) AND item_id != ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setInt(2, itemId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Update item (returns error message if duplicate name, null if success)
    public String updateItem(int itemId, String name, String category, int quantity, int threshold, double price) {
        // Check if another item with the same name exists
        if (itemNameExistsForOtherItem(itemId, name)) {
            return "Item with name '" + name + "' already exists! Please choose a different name.";
        }
        
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return "Database connection is not available!";
        }
        String status = quantity >= threshold ? "Available" : (quantity > 0 ? "Low Stock" : "Out of Stock");
        String query = "UPDATE items SET name = ?, category = ?, quantity = ?, threshold = ?, price = ?, status = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name.trim());
            stmt.setString(2, category.trim());
            stmt.setInt(3, quantity);
            stmt.setInt(4, threshold);
            stmt.setDouble(5, price);
            stmt.setString(6, status);
            stmt.setInt(7, itemId);
            stmt.executeUpdate();
            return null; // Success - no error message
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }
    
    // Delete item
    public boolean deleteItem(int itemId) {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return false;
        }
        String query = "DELETE FROM items WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get item by ID
    public String[] getItemById(int itemId) {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return null;
        }
        String query = "SELECT * FROM items WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt("item_id")),
                    rs.getString("name"),
                    rs.getString("category"),
                    String.valueOf(rs.getInt("quantity")),
                    String.valueOf(rs.getInt("threshold")),
                    String.valueOf(rs.getDouble("price")),
                    rs.getString("status")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Record sale
    public boolean recordSale(int itemId, int quantity, double totalPrice) {
        // First, check if item has enough stock
        String[] item = getItemById(itemId);
        if (item == null) {
            return false;
        }
        
        int currentQuantity = Integer.parseInt(item[3]);
        if (currentQuantity < quantity) {
            return false; // Not enough stock
        }
        
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return false;
        }
        // Update item quantity
        int newQuantity = currentQuantity - quantity;
        String updateQuery = "UPDATE items SET quantity = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        // Insert sale record
        String insertQuery = "INSERT INTO sales (item_id, quantity, total_price, sale_date) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, itemId);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, totalPrice);
            stmt.executeUpdate();
            
            // Update stock status after sale
            checkStockLevels();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get low stock items
    public List<String[]> getLowStockItems() {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return new ArrayList<>();
        }
        List<String[]> items = new ArrayList<>();
        String query = "SELECT * FROM items WHERE status IN ('Low Stock', 'Out of Stock') ORDER BY quantity ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String[] item = {
                    String.valueOf(rs.getInt("item_id")),
                    rs.getString("name"),
                    rs.getString("category"),
                    String.valueOf(rs.getInt("quantity")),
                    String.valueOf(rs.getInt("threshold")),
                    String.valueOf(rs.getDouble("price")),
                    rs.getString("status")
                };
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    // Get all sales
    public List<String[]> getAllSales() {
        if (connection == null) {
            System.err.println("Database connection is not available!");
            return new ArrayList<>();
        }
        List<String[]> sales = new ArrayList<>();
        String query = "SELECT s.sale_id, i.name, s.quantity, s.total_price, s.sale_date " +
                      "FROM sales s JOIN items i ON s.item_id = i.item_id ORDER BY s.sale_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String[] sale = {
                    String.valueOf(rs.getInt("sale_id")),
                    rs.getString("name"),
                    String.valueOf(rs.getInt("quantity")),
                    String.valueOf(rs.getDouble("total_price")),
                    rs.getString("sale_date")
                };
                sales.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


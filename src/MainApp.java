public class MainApp {
    public static void main(String[] args) {
        // Initialize database connection
        Database db = new Database();
        
        // Show login window
        new LoginWindow(db);
    }
}



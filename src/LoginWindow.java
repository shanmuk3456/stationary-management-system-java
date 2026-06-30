import java.awt.*;
import java.awt.event.*;

public class LoginWindow extends Frame {
    private Database db;
    private TextField usernameField;
    private TextField passwordField;
    private Label messageLabel;
    
    public LoginWindow(Database db) {
        this.db = db;
        setTitle("Stationary Management System - Login");
        setSize(400, 250);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(null);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        Label titleLabel = new Label("Stationary Management System", Label.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new Label("Username:"), gbc);
        
        usernameField = new TextField(20);
        gbc.gridx = 1;
        add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new Label("Password:"), gbc);
        
        passwordField = new TextField(20);
        passwordField.setEchoChar('*');
        gbc.gridx = 1;
        add(passwordField, gbc);
        
        // Message label
        messageLabel = new Label("", Label.CENTER);
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(messageLabel, gbc);
        
        // Login button
        Button loginButton = new Button("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(loginButton, gbc);
        
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = passwordField.getText().trim();
                
                if (username.isEmpty() || password.isEmpty()) {
                    messageLabel.setText("Please enter username and password!");
                    return;
                }
                
                if (db.verifyLogin(username, password)) {
                    messageLabel.setText("Login successful!");
                    messageLabel.setForeground(Color.GREEN);
                    dispose();
                    new MainMenuWindow(db);
                } else {
                    messageLabel.setText("Invalid username or password!");
                    messageLabel.setForeground(Color.RED);
                    passwordField.setText("");
                }
            }
        });
        
        // Enter key support
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Trigger login action
                String username = usernameField.getText().trim();
                String password = passwordField.getText().trim();
                
                if (username.isEmpty() || password.isEmpty()) {
                    messageLabel.setText("Please enter username and password!");
                    return;
                }
                
                if (db.verifyLogin(username, password)) {
                    messageLabel.setText("Login successful!");
                    messageLabel.setForeground(Color.GREEN);
                    dispose();
                    new MainMenuWindow(db);
                } else {
                    messageLabel.setText("Invalid username or password!");
                    messageLabel.setForeground(Color.RED);
                    passwordField.setText("");
                }
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
}



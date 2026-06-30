# Java Stationary Management System

## Setup Instructions

### Prerequisites
- Java JDK installed (Java 8 or higher)
- MySQL Server running
- MySQL database `stationary_db` created

### Database Configuration
Update the database connection details in `src/Database.java`:
- `DB_URL`: Database connection URL (default: `jdbc:mysql://localhost:3306/stationary_db`)
- `DB_USER`: MySQL username (default: `root`)
- `DB_PASSWORD`: MySQL password (default: `Nani`)

### Building and Running

#### Option 1: Using the provided scripts (Recommended)

1. **Build the project:**
   ```
   build.bat
   ```
   This will compile all Java files with the MySQL JDBC driver on the classpath.

2. **Run the application:**
   ```
   run.bat
   ```
   This will run the application with the MySQL JDBC driver on the classpath.

#### Option 2: Manual compilation and execution

1. **Compile:**
   ```
   javac -cp "lib\mysql-connector-j-9.5.0.jar" -d . src\*.java
   ```

2. **Run:**
   ```
   java -cp ".;lib\mysql-connector-j-9.5.0.jar" MainApp
   ```

### Troubleshooting

#### ClassNotFoundException: com.mysql.cj.jdbc.Driver
- Make sure the MySQL JDBC driver JAR is in the `lib` directory
- Ensure you're using the `-cp` flag to include the JAR in the classpath when compiling and running

#### NullPointerException: Cannot invoke "java.sql.Connection.prepareStatement(String)"
- This means the database connection failed
- Check that MySQL server is running
- Verify database credentials in `Database.java`
- Ensure the database `stationary_db` exists
- Check network connectivity to the database server

### Project Structure
```
.
├── src/                    # Source Java files
│   ├── Database.java       # Database connection and operations
│   ├── MainApp.java        # Application entry point
│   ├── LoginWindow.java    # Login interface
│   └── ...                 # Other window classes
├── lib/                    # External libraries
│   └── mysql-connector-j-9.5.0.jar  # MySQL JDBC driver
├── build.bat               # Build script
├── run.bat                 # Run script
└── README.md               # This file
```


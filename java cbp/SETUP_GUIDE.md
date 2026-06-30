# Stationary Management System - Setup Guide

## Step 1: Set Up MySQL Database

1. **Open MySQL Command Line or MySQL Workbench**
   - Make sure MySQL is installed and running on your system

2. **Run the SQL Script**
   - **Recommended:** Use `stationary_data.sql` (includes 200 test items and 80 sales records)
   - Open `stationary_data.sql` file
   - Copy and paste the entire content into MySQL
   - Execute the script
   
   OR use command line:
   ```bash
   mysql -u root -p < stationary_data.sql
   ```
   
   **Note:** The `stationary_data.sql` file will:
   - Create the database and all tables
   - Insert admin user (username: `admin`, password: `admin123`)
   - Generate 200 random stationary items across 16 categories
   - Generate 80 sample sales records
   - Perfect for testing the application with realistic data!

3. **Verify Database Creation**
   ```sql
   USE stationary_db;
   SHOW TABLES;
   SELECT COUNT(*) FROM users;    -- Should show 1
   SELECT COUNT(*) FROM items;    -- Should show 200
   SELECT COUNT(*) FROM sales;    -- Should show 80
   ```

## Step 2: Update Database Credentials

1. **Open `src/Database.java`**
2. **Update line 8** with your MySQL password:
   ```java
   private static final String DB_PASSWORD = "your_actual_mysql_password";
   ```
3. **If your MySQL username is not "root"**, update line 7:
   ```java
   private static final String DB_USER = "your_username";
   ```

## Step 3: MySQL JDBC Driver

✅ **Driver already added:** `mysql-connector-j-9.5.0.jar`
   - Location: `mysql-connector-j-9.5.0/mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar`

2. **Alternative: Using Maven** (if you prefer)
   - Add to `pom.xml`:
   ```xml
   <dependency>
       <groupId>com.mysql</groupId>
       <artifactId>mysql-connector-j</artifactId>
       <version>8.0.33</version>
   </dependency>
   ```

## Step 4: Compile the Java Files

**Windows (PowerShell):**
```powershell
cd "C:\java cbp"
javac -cp ".;mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar" src/*.java
```

**Windows (Command Prompt):**
```cmd
cd "C:\java cbp"
javac -cp ".;mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar" src\*.java
```

**Linux/Mac:**
```bash
cd "/path/to/java cbp"
javac -cp ".:mysql-connector-j-9.5.0/mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar" src/*.java
```

## Step 5: Run the Application

**Windows (PowerShell):**
```powershell
java -cp ".;mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;src" MainApp
```

**Windows (Command Prompt):**
```cmd
java -cp ".;mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;src" MainApp
```

**Linux/Mac:**
```bash
java -cp ".:mysql-connector-j-9.5.0/mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar:src" MainApp
```

## Step 6: Login to the Application

- **Username:** `admin`
- **Password:** `admin123`

## Troubleshooting

### Issue: "MySQL JDBC Driver not found!"
**Solution:** Make sure the MySQL connector JAR file is in the classpath and the filename matches exactly.

### Issue: "Connection failed!"
**Solution:** 
- Check if MySQL is running
- Verify database credentials in `Database.java`
- Ensure database `stationary_db` exists
- Check MySQL port (default is 3306)

### Issue: "Access denied for user"
**Solution:**
- Verify MySQL username and password
- Check if user has privileges to access the database

### Issue: Compilation errors
**Solution:**
- Make sure all files are in the `src/` directory
- Check Java version (requires Java 8 or higher)
- Verify all imports are correct

## Project Structure

```
java cbp/
├── src/
│   ├── Database.java          (Database connection & operations)
│   ├── MainApp.java           (Entry point)
│   ├── LoginWindow.java       (Login interface)
│   ├── MainMenuWindow.java    (Main menu)
│   ├── InventoryWindow.java   (Item management)
│   └── SalesWindow.java       (Sales recording)
├── database/
│   └── schema.sql             (Basic schema - minimal data)
├── stationary_data.sql        (Recommended: Full schema with 200 items & 80 sales)
└── mysql-connector-j-9.5.0/   (MySQL JDBC Driver 9.5.0)
    └── mysql-connector-j-9.5.0.jar
```

## Features Available

✅ Admin login system
✅ Add, View, Update, Delete items
✅ Automatic stock status updates (Available/Low Stock/Out of Stock)
✅ Record sales with automatic stock reduction
✅ View stock reports for low/out-of-stock items
✅ Sales history tracking

## Next Steps After Setup

1. Test the login with default credentials
2. Add new items through Inventory Management
3. Record a test sale
4. Check stock reports to see status updates
5. Customize the application as needed



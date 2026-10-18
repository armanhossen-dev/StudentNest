# Running MySQL & PHP on macOS Without XAMPP

A complete reference guide for managing local databases, PHP projects, and commands entirely from your terminal using native Homebrew services.

---

## 1. Overview & Current Environment

Your Mac is equipped with native tools installed via **Homebrew** (`/opt/homebrew`):

- **MySQL Client & Server**: `/opt/homebrew/bin/mysql`
- **PHP**: `/opt/homebrew/bin/php`
- **Default Port**: `3306`
- **Default User**: `root` (no password by default)

> [!NOTE]
> Because native MySQL runs in the background on port `3306`, running XAMPP at the same time causes port conflicts and socket errors (e.g. `ERROR 2002: Can't connect to local server through socket '/Applications/XAMPP/xamppfiles/var/mysql/mysql.sock'`).

---

## 2. Managing Services via Terminal (`brew services`)

Check, start, stop, and restart background services using simple terminal commands:

```bash
# Check status of all active background services
brew services list

# Start MySQL server (starts automatically on boot)
brew services start mysql

# Stop MySQL server
brew services stop mysql

# Restart MySQL server
brew services restart mysql

# Start / Stop PHP service if needed
brew services start php
brew services stop php
```

---

## 3. MySQL Terminal Commands

### Connect to MySQL
```bash
mysql -u root
```
*(If you set a password later, use `mysql -u root -p`)*

### Common SQL Commands (inside the MySQL shell)
```sql
-- Show all databases
SHOW DATABASES;

-- Switch to a database
USE studentnest_db;

-- Show tables in current database
SHOW TABLES;

-- Query records
SELECT * FROM users;
SELECT * FROM rooms;

-- Exit the shell
EXIT;
```

---

## 4. Database Import & Backup (One-Line Commands)

### Import a Database from a `.sql` file:
```bash
# Import into an existing database
mysql -u root studentnest_db < v1/database.sql

# Or create database first if it doesn't exist, then import
mysql -u root -e "CREATE DATABASE IF NOT EXISTS studentnest_db;"
mysql -u root studentnest_db < v1/database.sql
```

### Export / Backup a Database:
```bash
# Export full database with schema and data
mysqldump -u root studentnest_db > studentnest_backup.sql

# Export all databases at once
mysqldump -u root --all-databases > all_databases_backup.sql
```

---

## 5. Running PHP Projects Without XAMPP Apache

You do not need Apache or XAMPP `htdocs` to run PHP websites. PHP has a high-speed built-in development server.

Navigate to any project directory and start the server:
```bash
# Navigate to your project folder
cd /path/to/your/php-project

# Start the built-in PHP local development server on port 8000
php -S localhost:8000
```
Open your browser at: **`http://localhost:8000`**

---

## 6. What to Do Before Removing `/Applications/XAMPP`

> [!CAUTION]
> Before deleting `/Applications/XAMPP`, make sure you don't lose projects saved inside `/Applications/XAMPP/xamppfiles/htdocs/`.

### Move Any Existing Project (e.g., `BMS`):
```bash
# Safely copy your project to your GitHub directory
cp -R /Applications/XAMPP/xamppfiles/htdocs/BMS /Users/armanhossenripon/Documents/GitHub/BMS
```

### To Completely Remove XAMPP and Reclaim ~1 GB Disk Space:
```bash
sudo rm -rf /Applications/XAMPP
```

---

## 7. Status for StudentNest

- **Database**: `studentnest_db` (tables `users` and `rooms`) is imported into your native Homebrew MySQL server.
- **Connection**: StudentNest connects to `localhost:3306` with user `root`.
- **Run Application**:
  ```bash
  cd v1
  ./mvnw javafx:run
  ```
  Or in VS Code press **`Cmd + Shift + B`**.
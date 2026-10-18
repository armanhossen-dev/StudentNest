# 🚀 Running StudentNest in Visual Studio Code (VS Code)

This guide explains how to set up, build, and run the **StudentNest (v1)** JavaFX application directly within Visual Studio Code.

---

## 📋 Prerequisites

Before running the application in VS Code, ensure you have:

1. **Java Development Kit (JDK):** Version 17, 21, or higher installed.
2. **VS Code Extensions:**
   - [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) *(by Microsoft)*.
3. **Database (XAMPP / MySQL):**
   - Start **Apache** and **MySQL** in the XAMPP Control Panel.
   - Open **phpMyAdmin** at [http://localhost/phpmyadmin](http://localhost/phpmyadmin).
   - Import or execute [database.sql](file:///Users/armanhossenripon/Documents/GitHub/StudentNest/v1/database.sql) to create `studentnest_db` and sample data.

---

## 🛠️ Execution Methods

### Method 1: VS Code Build Task (Fastest — 1 Keyboard Shortcut)

The project includes pre-configured tasks in [.vscode/tasks.json](file:///Users/armanhossenripon/Documents/GitHub/StudentNest/.vscode/tasks.json).

1. Open the **StudentNest** project folder in VS Code.
2. Press the shortcut:
   - **macOS:** `Cmd` + `Shift` + `B`
   - **Windows / Linux:** `Ctrl` + `Shift` + `B`
3. Select **`Run StudentNest (JavaFX)`**.
4. The application will compile and launch the login window immediately.

---

### Method 2: Integrated Terminal (Command Line)

1. Open the built-in terminal in VS Code:
   - Press **`Ctrl` + `` ` ``** *(Windows/Linux)* or **`Cmd` + `` ` ``** *(macOS)*.
2. Navigate into the `v1` directory and execute:
   ```bash
   cd v1
   ./mvnw javafx:run
   ```
   > **Windows (PowerShell / CMD):**
   > ```powershell
   > cd v1
   > .\mvnw.cmd javafx:run
   > ```

---

### Method 3: Maven Sidebar Panel (GUI)

1. Open the **Maven** explorer tab from the left activity bar in VS Code.
2. Expand the project tree:
   - **`studentnest`** → **`Plugins`** → **`javafx`**
3. Hover over **`javafx:run`** and click the ▶ **Run** icon.

---

## 🔑 Default Login Credentials

Use any of the pre-loaded accounts to log in and test different user roles:

| Role | Username | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Student** | `hasib_student` | `password123` | Browse rooms, filter by price/location, view owner contact |
| **House Owner** | `arman_owner` | `password123` | Add, update, and delete room listings |
| **Admin** | `admin` | `admin123` | Manage users, posted rooms, and view statistics |

---

## ❓ Troubleshooting

* **Permission Denied (`./mvnw: Permission denied` on macOS/Linux):**
  Run this command in the terminal to grant execution permissions:
  ```bash
  chmod +x v1/mvnw
  ```
* **Database Connection Error:**
  Ensure MySQL is active on port `3306` with default credentials (`root` and empty password `""`), and that the `studentnest_db` database has been imported from [database.sql](file:///Users/armanhossenripon/Documents/GitHub/StudentNest/v1/database.sql).
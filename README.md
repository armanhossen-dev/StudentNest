# 🏡 StudentNest – Student Home Finder

**StudentNest** is a smart solution designed for students of **Daffodil International University (DIU)** to easily discover available homes, rooms, or seats around the campus. Students can search by **location** and **price range** to find a suitable place to live while studying.

---

## 🌟 Key Features

- 🔍 **Location-Based Search** (e.g., Khagan, Candgaon, Charabag, Kumkumari, Dattopara, Shadhupara)
- 💰 **Budget Filter** – Find rooms that match your financial range
- 🏠 **Room/Seat Availability** with verified listings
- 📍 **Map Integration** (optional future feature)
- 📊 User-friendly Interface for both mobile and desktop

---

## 💻 Technologies Used

- Java (JavaFX 21+)
- Maven (with Maven Wrapper `mvnw`)
- MySQL (with XAMPP)
- JDBC (Java Database Connectivity)
- IntelliJ IDEA / NetBeans / Eclipse (IDE)
- Git & GitHub for version control

---

## 🚀 Quick Start (Running v1)

### 1. Database Setup
1. Start **Apache** and **MySQL** in the **XAMPP Control Panel**.
2. Open phpMyAdmin (`http://localhost/phpmyadmin`).
3. Import or execute [database.sql](file:///Users/armanhossenripon/Documents/GitHub/StudentNest/v1/database.sql) (or copy from [v1 - sql commands for xamp.txt](file:///Users/armanhossenripon/Documents/GitHub/StudentNest/v1%20-%20sql%20commands%20for%20xamp.txt)).

### 2. Run the Application (Terminal)
Open a terminal in the `v1` folder and run:
```bash
cd v1
./mvnw javafx:run
```
*(On Windows PowerShell / CMD, use: `cd v1` then `.\mvnw.cmd javafx:run`)*

---

## 💻 How to Run in Visual Studio Code (VS Code)

### Prerequisites
1. Install **VS Code**.
2. Install the **Extension Pack for Java** extension (from Microsoft).
3. Ensure JDK 17, 21, or newer is installed.

### Method 1: Via VS Code Build Task (Fastest - 1 Click / Shortcut)
1. Open the repository root folder in VS Code.
2. Press **`Cmd + Shift + B`** (on macOS) or **`Ctrl + Shift + B`** (on Windows/Linux).
3. VS Code will automatically execute the pre-configured task **`Run StudentNest (JavaFX)`**.

### Method 2: Via VS Code Integrated Terminal
1. Open the integrated terminal in VS Code:
   - Press **``Ctrl + ` ``** (Windows/Linux) or **``Cmd + ` ``** (macOS).
2. Enter the `v1` directory and run:
   ```bash
   cd v1
   ./mvnw javafx:run
   ```
   *(On Windows Command Prompt or PowerShell, run: `cd v1` then `.\mvnw.cmd javafx:run`)*

### Method 3: Via Maven Sidebar Panel
1. In the VS Code left Activity Bar, open the **Maven** view (or Explorer -> Maven section).
2. Expand **studentnest** → **Plugins** → **javafx**.
3. Click the ▶ **Run** button next to **`javafx:run`**.

---

### 🔑 Default Login Credentials
- **Student:** Username: `hasib_student`, Password: `password123`
- **House Owner:** Username: `arman_owner`, Password: `password123`
- **Admin:** Username: `admin`, Password: `admin123`

---

## 👥 Contributors

We proudly present our dedicated team behind **StudentNest** – each member played an important role in building this project:

| Name                    | GitHub Username             |
|-------------------------|---------------------------------|
| Md. Arman Hossen Ripon  |[@armanhossen-dev](https://github.com/armanhossen-dev) |
| Sabbir Hosine           |[@sabbir-hossine](https://github.com/sabbir-hossine) |
| Md. Hasibur Rahman      |[@Hasib55-bit](https://github.com/Hasib55-bit) |
| Md. Rahul Hossain       |[@Rahul003-phy](https://github.com/Rahul003-phy) |
| Md. Wahidur Rahman      |[@Saief24](https://github.com/Saief24) |

# How to Run StudentNest (v4)

This guide provides step-by-step commands to run the **StudentNest v4** desktop JavaFX application on **Windows**, **macOS**, and **Linux**.

---

## 📋 Prerequisites (All Platforms)

1. **Java Development Kit (JDK) 21 LTS** installed.
   Verify by running:
   ```bash
   java -version
   ```
   *(Ensure the output shows `openjdk version "21.x.x"` or Java 21).*

2. **Database (Optional / Zero Config)**:
   - **SQLite (Default fallback)**: No setup or installation required! The app automatically creates `studentnest.db` and seeds initial demo listings on first launch.
   - **MySQL (Optional)**: If you have MySQL running locally on port 3306 with user `root` and database `studentnest`, it connects automatically. If MySQL is not running, it gracefully falls back to SQLite without crashing.

---

## 🪟 Windows (Command Prompt & PowerShell)

### Using Command Prompt (`cmd.exe`):

1. Navigate to the `v4` project directory:
   ```cmd
   cd path\to\StudentNest\v4
   ```

2. Run the application using the included Maven Wrapper:
   ```cmd
   mvnw.cmd javafx:run
   ```

   *(Or if you have Apache Maven installed globally):*
   ```cmd
   mvn javafx:run
   ```

### Using PowerShell:

1. Navigate to the `v4` directory:
   ```powershell
   cd path\to\StudentNest\v4
   ```

2. Run the application:
   ```powershell
   .\mvnw.cmd javafx:run
   ```

   *(Or with global Maven):*
   ```powershell
   mvn javafx:run
   ```

### Other Useful Windows Commands:
- **Run all automated tests**:
  ```cmd
  mvnw.cmd test
  ```
- **Build / compile the project JAR**:
  ```cmd
  mvnw.cmd clean package -DskipTests
  ```

---

## 🍎 macOS (Terminal)

1. Open **Terminal** and navigate to the `v4` directory:
   ```bash
   cd path/to/StudentNest/v4
   ```

2. Make sure the Maven wrapper has execute permissions (first time only):
   ```bash
   chmod +x mvnw
   ```

3. Launch the application:
   ```bash
   ./mvnw javafx:run
   ```

   *(Or if you have Apache Maven installed via Homebrew):*
   ```bash
   mvn javafx:run
   ```

### Other Useful macOS Commands:
- **Run all automated tests**:
  ```bash
  ./mvnw test
  ```
- **Build / compile the project**:
  ```bash
  ./mvnw clean package -DskipTests
  ```

---

## 🐧 Linux (Ubuntu, Debian, Fedora, Arch, etc.)

> **Note**: StudentNest is a JavaFX desktop GUI application. It requires a desktop display server (X11 or Wayland) with standard GTK libraries installed.

1. Ensure required graphical dependencies and JDK 21 are installed:
   - **Ubuntu / Debian**:
     ```bash
     sudo apt update
     sudo apt install -y openjdk-21-jdk libgtk-3-0 libasound2
     ```
   - **Fedora**:
     ```bash
     sudo dnf install -y java-21-openjdk-devel gtk3 alsa-lib
     ```
   - **Arch Linux**:
     ```bash
     sudo pacman -S jdk21-openjdk gtk3 alsa-lib
     ```

2. Open your terminal and navigate to `v4`:
   ```bash
   cd path/to/StudentNest/v4
   ```

3. Ensure execution permissions on the wrapper script:
   ```bash
   chmod +x mvnw
   ```

4. Launch the application:
   ```bash
   ./mvnw javafx:run
   ```

   *(Or if global Maven is installed):*
   ```bash
   mvn javafx:run
   ```

### Headless / Virtual Display (CI / Remote Testing on Linux):
If testing on a headless server without a physical monitor:
```bash
sudo apt install -y xvfb
xvfb-run ./mvnw javafx:run
```

---

## 🔑 Demo Accounts (Development & Testing)

On first launch, initial demo accounts are automatically prepared:

| Role | Username | Password | Notes |
|---|---|---|---|
| **Student** | `student` | `student123` | Can browse, favorite, and submit inquiries |
| **House Owner** | `owner` | `owner123` | Can post new room listings and manage bookings |
| **Administrator** | `admin` | `admin123` | Can access KPI analytics, charts, verify & moderate listings |
| **Guest** | *(No login required)* | *(None)* | Click **Continue as guest** to browse listings and save favorites |

---

## 🛠️ Troubleshooting & Tips

- **`JAVA_HOME` not found or wrong Java version**:
  Ensure Java 21 is set in your path:
  - Windows: `set JAVA_HOME=C:\Program Files\Java\jdk-21` & `set PATH=%JAVA_HOME%\bin;%PATH%`
  - macOS/Linux: `export JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null || echo "/usr/lib/jvm/java-21-openjdk")`
- **Port or Database Lock**:
  If tests or app fail due to a database lock on `studentnest.db`, ensure any previous running instance of the app is closed before running `./mvnw test` or restarting.
- **Offline / Local mode**:
  SQLite works completely offline without network or external database dependencies.

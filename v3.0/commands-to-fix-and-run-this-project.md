# StudentNest v3.0 - Setup, Fix & Run Commands

Complete reference of the terminal commands and process used to configure, fix, build, and run the `v3.0` project.

---

## 1. Prerequisites Check

Verify your Java and MySQL environments:

```bash
# Check installed Java version (Java 21+ supported)
java -version

# Ensure MySQL server is running (port 3306)
mysql -u root -e "SHOW DATABASES LIKE 'studentnest_db';"
```

---

## 2. Permissions Fix

Grant execute permissions to the Maven Wrapper executable:

```bash
cd /Users/armanhossenripon/Documents/GitHub/StudentNest/v3.0
chmod +x mvnw
```

---

## 3. Project Configuration Fixes Applied

The following changes were made to configure the project properly:

1. **`pom.xml` Main Class Fix:**
   - Changed `<mainClass>` from `com.studentnest/com.studentnest.HelloApplication` (non-existent) to `studentnest/com.studentnest.Main`.
2. **`pom.xml` Dependencies:**
   - Added `mysql-connector-j` version `9.1.0`.
   - Updated JavaFX dependencies (`javafx-controls`, `javafx-fxml`) to version `21.0.5`.
   - Set compiler `<source>` and `<target>` to `21`.
3. **`module-info.java` Reflection:**
   - Added `opens com.studentnest.models to javafx.fxml, javafx.base;` to allow TableView property binding without reflection errors.
4. **`StudentDashboardController.java`:**
   - Added `room.setRoomType(rs.getString("room_type"))` to load all room details correctly.
   - Enhanced `loadRoomImage` with relative and local fallback directory resolution.

---

## 4. Build & Compile

Clean previous target artifacts and compile all sources:

```bash
cd /Users/armanhossenripon/Documents/GitHub/StudentNest/v3.0
./mvnw clean compile
```

---

## 5. Run the Application

Launch the JavaFX desktop application:

```bash
cd /Users/armanhossenripon/Documents/GitHub/StudentNest/v3.0
./mvnw javafx:run
```

---

## 6. Git Operations

Stage, commit, and push updates:

```bash
cd /Users/armanhossenripon/Documents/GitHub/StudentNest
git status
git add v3.0/ git.md
git commit -m "fix(v3.0): resolve mainClass, add MySQL driver, update JavaFX to 21, and add command guide"
git push origin main
```

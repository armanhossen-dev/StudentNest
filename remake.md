# StudentNest — Master Blueprint & Remake Prompt
### Next-Generation University Student Home Finding Desktop Application
**Platform:** Java 21 LTS · JavaFX 21 · FXML · Modern 7-Color Minimal Design · Dual Database (MySQL 3306 + Embedded SQLite)

---

## 1. Quick Copy-Paste Remake Prompt for AI / Developers

If you want an AI agent (Antigravity, Claude, ChatGPT, etc.) or a developer to build this application from scratch in one go, copy and paste the prompt block below:

```markdown
Build a complete, production-grade desktop application called "StudentNest" using Java 21 LTS and JavaFX 21 (FXML + MVC + CSS).

### 🎯 Core Purpose:
StudentNest is a student housing and apartment finding desktop application built for university students (tailored specifically for Daffodil International University - DIU campus areas: Khagan, Candgaon, Charabag, Kumkumari, Dattopara, Shadhupara). It connects students directly with verified house owners, eliminating middleman fees, and provides interactive search, verified listings, distance mapping, direct calling, saved favorites, owner property management, student inquiries, admin moderation, and community reviews.

### 🎨 Strict UI & Design Constraints:
1. ZERO EMOJIS: Do NOT use any emojis anywhere in the user interface (no 🏡, 📍, 📞, ❤️, ⚡, 🛡️, etc.). Use clean modern typography, clear descriptive labels, pill badges ("Verified", "Saved", "Copy Phone", "Google Maps"), or clean vector/SVG shapes.
2. STRICT 7-COLOR PALETTE SYSTEM:
   - 1. White (#FFFFFF): Primary surface, clean card canvas, light window background.
   - 2. Dark Slate (#0F172A / #1E293B): Crisp high-contrast typography, structural borders, dark theme surface.
   - 3. Blue (#2563EB): Brand primary, active sidebar navigation, primary CTAs, search buttons, Student role badge.
   - 4. Yellow (#EAB308): Star ratings, review highlights, warning alerts, notice badges.
   - 5. Pink (#EC4899): Saved favorites pill, favorite bookmark counter, community highlights.
   - 6. Red (#EF4444): Danger buttons (delete/remove), Admin role badge, occupied property status.
   - 7. Green (#10B981): Verified badge, House Owner role badge, available property status, success toast notifications.
3. MODERN AESTHETIC:
   - 8px to 12px border radius on cards, panels, and buttons.
   - 999px pill radius for badges and tag chips.
   - Subtle modern elevation/drop shadows (0 4px 16px rgba(0,0,0,0.06)).
   - Native system typography (-apple-system, Segoe UI, Roboto, Inter).
   - Light and Dark mode toggle support.

### 🗄️ Dual-Mode Self-Healing Database:
- Auto-detects local MySQL on localhost:3306 (database: `studentnest_db`, user: `root`, password: `""`).
- If MySQL is offline or not installed, automatically and seamlessly falls back to embedded zero-config SQLite (`studentnest.db`).
- Auto-executes `CREATE TABLE IF NOT EXISTS` for:
  - `users` (id, name, email, phone, username, password, user_type)
  - `rooms` (id, title, description, location, price, contact_number, owner_id, owner_name, is_available, is_verified, room_type, bedrooms, amenities, map_link, image1_path, image2_path, image3_path, created_at)
  - `inquiries` (id, room_id, room_title, student_name, student_contact, message, status, created_at)
  - `feedback` (id, user_id, user_name, user_role, rating, comment, status, created_at)
- Auto-seeds realistic demo properties (Khagan, Candgaon, Charabag, Kumkumari), demo users (student, owner, admin), and community reviews if tables are empty.

### 👥 User Roles & Permissions:
1. Guest: Search, filter, view details, calculate campus distance, copy phone number, bookmark favorites locally.
2. Student: Sign in / sign up, manage saved favorites, submit direct inquiry messages to hosts, write feedback with star ratings.
3. House Owner: Add new property listings with amenities and images, edit listings, toggle room availability (Available / Occupied), view and manage student inquiries.
4. Admin: View system KPI metrics, interactive analytics charts (User Roles PieChart, Listings by Campus Area BarChart), moderate users (delete), moderate listings (toggle verified badge, delete), moderate community feedback.

### 🖥️ Views to Implement:
1. Shell Navigation: Top bar (Brand, Version pill, DB connection status badge, Pink favorites counter pill, Theme toggle, User profile chip with colored role badge, Sign In / Logout button); Left sidebar navigation (Home, Browse, Favorites, List Property, My Listings, Admin Panel, Feedback, About, Campus area guide).
2. Home Overview: Hero card with CTAs, live KPI stat boxes (Verified Properties, Happy Students, Campuses), 4 colored feature cards (Blue: Smart Search, Green: Verified Listings, Yellow: Direct Contact, Pink: Campus Location Based).
3. Browse Properties: Search field + 5 filter dropdowns (Location, Price Range, Property Type, Bedrooms, Sort By), responsive FlowPane of property cards with images, verified badge, price, location, amenities chips, Save button, Details button, Quick Contact button.
4. Saved Favorites: Grid of bookmarked properties with one-click toggle.
5. List / Edit Property Modal: Full form with title, location, type, rent, bedrooms, contact phone, Google Maps URL, description, 8 amenity checkboxes, image file chooser + preset room photo picker.
6. Property Details Modal: Large image preview, verified badge, price/month, bedrooms, owner info, amenity chips, full description, direct phone copy button (copies to clipboard), Google Maps link button, direct student inquiry form.
7. My Listings View: TableView of owner's properties with availability toggle, edit/delete actions, and TableView of incoming student inquiries.
8. Admin Dashboard: 4 stat boxes, PieChart (user distribution), BarChart (listings per campus location), and 3-tab moderation TableView (Users, Listings, Reviews).
9. Feedback View: Rating submission (1-5 stars in Yellow) + testimonial feed.
10. About View: Campus guide (Khagan, Candgaon, Charabag, Kumkumari, Dattopara), student safety tips, team credits.
11. Auth Modal: Clean TabPane for Sign In and Sign Up with quick 1-click demo profile buttons (Student, House Owner, Admin, Guest).
12. Notification Toasts: Non-blocking animated bottom-right toast notifications for success, warning, info, and error.

### 🛠️ Technology Stack:
- Java 21 (LTS)
- JavaFX 21.0.5 (`javafx-controls`, `javafx-fxml`, `javafx-graphics`)
- `mysql-connector-j` 9.1.0
- `sqlite-jdbc` 3.46.1.0
- Maven Wrapper (`mvnw`) for zero-dependency build and run via `./mvnw clean javafx:run`.
```

---

## 2. Architectural Blueprint & File Tree

The following directory layout represents the canonical structure of the StudentNest desktop application:

```
StudentNest/
├── pom.xml                                      # Maven project descriptor
├── mvnw                                         # Maven wrapper (UNIX)
├── mvnw.cmd                                     # Maven wrapper (Windows)
├── .mvn/wrapper/maven-wrapper.properties        # Maven wrapper configuration
├── remake.md                                    # This blueprint & remake prompt
├── README.md                                    # Project documentation
├── room_images/                                 # Runtime upload directory
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── studentnest/
        │           ├── App.java                 # JavaFX Application Entrypoint
        │           ├── controllers/
        │           │   ├── MainAppController.java
        │           │   ├── LoginModalController.java
        │           │   ├── AddPropertyModalController.java
        │           │   └── PropertyDetailModalController.java
        │           ├── database/
        │           │   ├── DatabaseConnection.java
        │           │   ├── SchemaInitializer.java
        │           │   └── DataSeeder.java
        │           ├── models/
        │           │   ├── User.java
        │           │   ├── Room.java
        │           │   ├── Inquiry.java
        │           │   └── Feedback.java
        │           └── utils/
        │               ├── ThemeManager.java
        │               └── NotificationUtils.java
        └── resources/
            ├── css/
            │   └── modern-v31.css               # 7-Color Modern Design System
            ├── fxml/
            │   ├── main-app.fxml                # Master Shell with 7 dynamic views
            │   ├── login-modal.fxml             # Authentication dialog
            │   ├── add-property-modal.fxml      # Add/Edit listing dialog
            │   └── property-detail-modal.fxml   # Full property details & inquiry dialog
            └── images/
                ├── room0.jpg                    # Default preset apartment photo
                ├── room1.jpg                    # Default preset studio photo
                └── icon.png                     # App taskbar & window icon
```

---

## 3. Database Schema & Dual-Connector Engine

### 3.1 Dual-Connector Engine (`DatabaseConnection.java`)
The application implements an intelligent dual-connector strategy:
1. **Primary:** Attempts connection to MySQL at `jdbc:mysql://localhost:3306/studentnest_db?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false` with user `root` and password `""` (timeout: 2.5 seconds).
2. **Fallback:** If MySQL is unreachable, it logs a graceful warning and activates embedded SQLite at `jdbc:sqlite:studentnest.db`.
3. **Status Reporting:** Exposes `getDatabaseType()` ("MySQL 3306" or "SQLite Light") to render live in the UI header badge.

### 3.2 SQL Table Schemas (`SchemaInitializer.java`)

```sql
-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,        -- In MySQL: INT AUTO_INCREMENT PRIMARY KEY
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(30),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    user_type VARCHAR(20) NOT NULL DEFAULT 'STUDENT', -- 'STUDENT', 'HOUSE_OWNER', 'ADMIN'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Rooms / Properties Table
CREATE TABLE IF NOT EXISTS rooms (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    location VARCHAR(100) NOT NULL,              -- 'Khagan', 'Candgaon', 'Charabag', 'Kumkumari', etc.
    price DOUBLE NOT NULL,
    contact_number VARCHAR(30) NOT NULL,
    owner_id INTEGER DEFAULT 1,
    owner_name VARCHAR(100) DEFAULT 'House Owner',
    is_available BOOLEAN DEFAULT 1,
    is_verified BOOLEAN DEFAULT 0,
    room_type VARCHAR(50) DEFAULT 'Single Room', -- 'Single Room', 'Shared Seat', 'Master Bed', 'Studio', 'Full Flat'
    bedrooms INTEGER DEFAULT 1,
    amenities TEXT,                              -- Comma-separated: 'WiFi, Furnished, Kitchen, AC'
    map_link VARCHAR(255),
    image1_path VARCHAR(255),
    image2_path VARCHAR(255),
    image3_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Student Inquiries Table
CREATE TABLE IF NOT EXISTS inquiries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_id INTEGER NOT NULL,
    room_title VARCHAR(150),
    student_name VARCHAR(100) NOT NULL,
    student_contact VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING',        -- 'PENDING', 'ACCEPTED', 'DECLINED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Community Feedback & Reviews Table
CREATE TABLE IF NOT EXISTS feedback (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER DEFAULT 0,
    user_name VARCHAR(100) NOT NULL,
    user_role VARCHAR(30) DEFAULT 'Student',
    rating INTEGER DEFAULT 5,                    -- 1 to 5 Stars
    comment TEXT NOT NULL,
    status VARCHAR(30) DEFAULT 'APPROVED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3.3 Default Seed Data (`DataSeeder.java`)
- **Users:**
  - `student` / `student123` (Name: "Md. Hasibur Rahman", Role: "STUDENT")
  - `owner` / `owner123` (Name: "Haji Nurul Islam", Role: "HOUSE_OWNER")
  - `admin` / `admin123` (Name: "Arman Hossen", Role: "ADMIN")
- **Properties (8 realistic listings around DIU campus):**
  - "Sunshine Student Hostel - Single Room" (Khagan, ৳4,500/mo, 1 Bed, WiFi, Furnished, Kitchen, Verified)
  - "Green Valley Master Bedroom" (Candgaon, ৳8,000/mo, 1 Bed, WiFi, AC, Balcony, Attached Bath, Verified)
  - "DIU Smart Studio Apartment" (Khagan Gate 1, ৳10,500/mo, 1 Bed, Full Furnished, AC, Generator, Verified)
  - "Kumkumari Shared Student Flat (Seat 2)" (Kumkumari, ৳3,200/mo, 3 Beds, WiFi, Meal System, Kitchen)
  - "Charabag Quiet Study Flat for CS Students" (Charabag, ৳14,000/mo, 2 Beds, High-speed Fiber WiFi, Inverter)
  - "Dattopara Budget Mess Room" (Dattopara, ৳2,800/mo, 2 Beds, Shared Kitchen, Filter Water)
  - "Candgaon Lakeside Bachelor Residence" (Candgaon, ৳6,500/mo, 1 Bed, WiFi, Balcony, Parking)
  - "Khagan Central Deluxe 2-Bed Flat" (Khagan, ৳16,000/mo, 2 Beds, Full Furnished, Lift, Security Guard, Verified)

---

## 4. The 7-Color Design System Token Guide

Every element in the application adheres strictly to the 7-color modern design hierarchy:

| Color | Hex Token | Functional Role in Application |
|---|---|---|
| **1. White** | `#FFFFFF` | Card backgrounds, dialog surfaces, content canvas, input fills |
| **2. Dark Slate** | `#0F172A` / `#1E293B` | Primary typography, headers, subtle structural borders, dark mode canvas |
| **3. Blue** | `#2563EB` / `#3B82F6` | Primary brand accents, active sidebar tab, primary CTAs, search button, Student badge |
| **4. Yellow** | `#EAB308` / `#FACC15` | Star ratings (1–5 stars), warning alerts, review metric cards |
| **5. Pink** | `#EC4899` / `#F43F5E` | Favorites counter pill, "Save" / "Saved" button state, community callout card |
| **6. Red** | `#EF4444` / `#DC2626` | Delete listing, delete user, Admin badge, Occupied status badge |
| **7. Green** | `#10B981` / `#059669` | Verified badge, House Owner badge, Available status, Success toast notifications |

---

## 5. View Specifications & User Workflows

### 5.1 Shell & Top Header
- **Brand Title:** Clean bold sans-serif text `"StudentNest"` (no house emoji).
- **Version Pill:** `"v3.1 Modern"` (light border, compact padding).
- **Database Status Badge:** Dynamically reads `"MySQL 3306 Connected"` or `"SQLite Light Embedded"` (clean font, no emoji).
- **Favorites Pill:** Pink pill badge `"Favorites (X)"` showing real-time saved count. Clicking navigates directly to the Saved Favorites view.
- **Theme Toggle:** Button toggles between Clean White theme and Slate Dark theme.
- **User Chip:** Displays user name and a colored role pill (Guest = Slate, Student = Blue, House Owner = Green, Admin = Red).
- **Auth Button:** `"Sign In / Join"` or `"Log Out"`.

### 5.2 Sidebar Navigation
- Navigation categories separated by clean headers:
  - **NAVIGATION:** Home Overview, Browse Properties, Saved Favorites, List Property.
  - **MANAGEMENT:** My Listings (visible to Owners & Admins), Admin Panel (visible to Admins only).
  - **COMMUNITY:** Feedback & Reviews, About DIU StudentNest.
- **Campus Quick Box:** Highlights key DIU student locations (Khagan, Candgaon, Charabag, Kumkumari).

### 5.3 Browse Properties View (Search & Multi-Filter Engine)
- **Search Bar:** Real-time search across listing title, campus location, description, and amenities.
- **Filters Row:**
  - Location: All Locations, Khagan, Candgaon, Charabag, Kumkumari, Dattopara.
  - Price: All Prices, Under ৳5,000, ৳5,000 - ৳8,000, ৳8,000 - ৳12,000, Over ৳12,000.
  - Type: All Types, Single Room, Shared Seat, Master Bed, Studio, Full Flat.
  - Bedrooms: All Bedrooms, 1 Bed, 2 Beds, 3+ Beds.
  - Sort By: Default, Price: Low to High, Price: High to Low, Most Recent.
- **Property Card Architecture:**
  - Top Image banner (with auto-fallback to bundled preset photos).
  - Top-Left: "Save" / "Saved" toggle pill button.
  - Top-Right: Type badge (`Studio`, `Single Room`) + Green `Verified` badge.
  - Body: Listing Title, Location, Price (`৳7,500/month`), Bedrooms count.
  - Amenities: First 3 chips (`WiFi`, `Furnished`, `AC`) + `+X more` counter.
  - Action Buttons: `View Details` (opens modal) and `Contact` (copies phone number with instant feedback toast).

### 5.4 Property Details Modal
- Header image with badges.
- Title, monthly rent, location, bedroom count, host name.
- Amenity chip cloud.
- Detailed property description with line spacing.
- Direct contact section:
  - Phone number label.
  - `Copy Phone` button (copies to clipboard, triggers green toast).
  - `Open Google Maps` button (opens system default browser to exact coordinates or area).
- Direct host inquiry form (Student Name, Contact, Message, `Send Inquiry to Host` CTA).

### 5.5 House Owner Dashboard (My Listings)
- TableView listing all properties owned by the logged-in host (or all properties if Admin).
- Columns: Title, Location, Price (৳), Type, Available (Yes/No).
- Actions:
  - `Add New Listing` (opens Add Property Modal).
  - `Edit Listing` (pre-populates modal with selected property data).
  - `Toggle Availability` (instantly flips Available ↔ Occupied status in database).
  - `Delete Listing` (with confirmation alert).
- Inquiries Table: Shows inquiries received from students for the owner's properties with room title, student name, phone, message, and status.

### 5.6 Admin Panel & Analytics
- 4 Key Metrics: Total Registered Users, Active Listings, Verified Badges, Community Reviews.
- User Roles Breakdown PieChart: Visualizes Students, House Owners, and Admins.
- Listings by Campus Location BarChart: Visualizes property distribution across Khagan, Candgaon, Charabag, Kumkumari.
- 3 Moderation Tabs:
  1. Users Management (TableView with ID, Name, Username, Phone, Role + `Delete User` button).
  2. Listings Moderation (TableView with ID, Title, Location, Price, Verified + `Toggle Verified Badge` button).
  3. Community Feedbacks (TableView with User, Role, Star Rating, Feedback Text, Status).

### 5.7 Toast Notification System (`NotificationUtils.java`)
- Non-blocking custom overlay notification rendered inside a `StackPane`.
- Smooth slide-in and fade-out animation (using `Timeline` / `TranslateTransition` / `FadeTransition`).
- Styled with the 7-color palette:
  - Success = Green border & accent.
  - Info = Blue border & accent.
  - Warning = Yellow border & accent.
  - Error = Red border & accent.

---

## 6. Maven `pom.xml` Dependencies & Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.studentnest</groupId>
    <artifactId>studentnest</artifactId>
    <version>3.1.0</version>
    <name>StudentNest</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <javafx.version>21.0.5</javafx.version>
    </properties>

    <dependencies>
        <!-- JavaFX Controls & FXML -->
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-graphics</artifactId>
            <version>${javafx.version}</version>
        </dependency>

        <!-- MySQL Connector -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>9.1.0</version>
        </dependency>

        <!-- SQLite Embedded Driver (Zero-Config Fallback) -->
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.46.1.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <executions>
                    <execution>
                        <id>default-cli</id>
                        <configuration>
                            <mainClass>com.studentnest.App</mainClass>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 7. Build, Run, and Validation Commands

```bash
# 1. Ensure Maven Wrapper has execute permissions
chmod +x mvnw

# 2. Clean previous build artifacts and compile
./mvnw clean compile

# 3. Launch the desktop application
./mvnw javafx:run
```

### Quick Demo Accounts:
| Role | Username | Password | Notes |
|---|---|---|---|
| **Student** | `student` | `student123` | Can search, bookmark favorites, inquire, and review |
| **House Owner** | `owner` | `owner123` | Can list properties, toggle availability, edit/delete, view inquiries |
| **Admin** | `admin` | `admin123` | Full system analytics, user moderation, listing verification |
| **Guest** | *(None)* | *(None)* | Instant access without signing in |

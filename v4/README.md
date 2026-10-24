# StudentNest

StudentNest is a native JavaFX desktop marketplace for student accommodation around Daffodil International University (DIU) in Ashulia, Bangladesh. It is a desktop application, not a website.

Version: 3.1.0 (`v3.1 Modern`)

## Prerequisites

- Java 21 LTS (JDK)
- Maven 3.8+ or the included Maven Wrapper
- Optional: MySQL 8+ for production-style persistence
- A desktop environment (macOS, Windows, or Linux with a display) for the JavaFX UI

Confirm the JDK:

```bash
java -version
```

## Installation

```bash
git clone <repository-url>
cd studentnest
```

With the Maven Wrapper:

```bash
chmod +x mvnw
./mvnw -q -DskipTests package
```

Without the wrapper:

```bash
mvn -q -DskipTests package
```

On first launch the app creates `studentnest.db` (SQLite fallback) and `room_images/` for uploaded photos.

## Database configuration

Development defaults: MySQL user `root`, empty password, database `studentnest`, host `localhost`, port `3306`.

Override with environment variables or `application.properties`:

```text
STUDENTNEST_DB_HOST=localhost
STUDENTNEST_DB_PORT=3306
STUDENTNEST_DB_NAME=studentnest
STUDENTNEST_DB_USER=root
STUDENTNEST_DB_PASSWORD=
STUDENTNEST_SQLITE_PATH=studentnest.db
```

Classpath defaults live in `src/main/resources/application.properties`. A local `application.properties` in the working directory overrides those values.

If MySQL is unavailable during the initial connection, StudentNest automatically falls back to:

```text
jdbc:sqlite:studentnest.db
```

Schema is created automatically. The app does not destroy data and does not silently switch databases after an ordinary query failure. All core features work in SQLite mode.

## Demo accounts (development only)

These accounts are seeded only when missing. They are labelled in the sign-in dialog as development-only.

| Role | Username | Password |
|------|----------|----------|
| Student | student | student123 |
| House Owner | owner | owner123 |
| Administrator | admin | admin123 |

Guest browsing is supported without an account. Public registration cannot create an administrator.

Demo listings are fictional sample data. Unverified cards are labelled as demo / unverified until an administrator reviews them.

## Running

```bash
./mvnw javafx:run
```

Or:

```bash
mvn javafx:run
```

The window includes a sidebar, top bar, theme toggle, live database status, and role-aware navigation.

## Testing

```bash
./mvnw test
```

JUnit 5 covers password hashing, validation, SQLite schema initialization, demo seeding, authentication, authorization, search/filter/sort, inquiries, and feedback visibility.

## Features

- Guest, student, owner, and admin permissions enforced in services
- Real-time property search with location, rent, type, bedroom, and sort filters
- Local guest favorites that merge into an account after sign-in
- Owner listing CRUD with image uploads into `room_images/`
- Inquiry workflow: PENDING, ACCEPTED, DECLINED
- Admin analytics (live KPIs, pie chart, bar chart) and moderation
- Light/dark themes persisted in `~/.studentnest/prefs.properties`
- Non-blocking notifications

## Troubleshooting

**The UI does not start.** Install a full JDK 21 and a working desktop display. Headless servers cannot show JavaFX windows.

**MySQL connection failed.** Check host, port, user, and password. The first failed connection falls back to SQLite. Later query errors do not switch databases.

**No listings appear.** Confirm `studentnest.db` is writable. Delete is not required; restarting reuses existing data. Seed data is inserted only when tables are empty.

**Images missing.** Bundled placeholders are used when a file is absent. Uploaded photos must be PNG, JPG, JPEG, GIF, or WEBP.

**Forgot that demo passwords are for development.** Change them before any shared deployment. Passwords are stored as salted PBKDF2 hashes, never plaintext.

**Tests fail on a locked SQLite file.** Stop a running StudentNest instance, then rerun `./mvnw test`.

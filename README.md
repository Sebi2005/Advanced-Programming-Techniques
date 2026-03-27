# 🏋️ Gym Management Application (JavaFX)

A Java-based Gym Management application developed using a layered architecture.  
The application supports multiple storage types, advanced filtering and reporting, and a full undo/redo system implemented using the Command design pattern.

---

## 📌 Features

### 👤 Client Management
- Add, update, delete clients
- Filter clients by name and email
- Cascade delete: removing a client also removes all associated sessions

### 📅 Session Management
- Add, update, delete sessions
- Filter sessions by client ID and date
- Sessions include date, time, and description

### 📊 Reports
- Sessions of a client between two dates
- Sessions containing a keyword
- Session count per client
- Next upcoming session per client
- Sessions grouped by day

### ↩️ Undo / Redo (Command Pattern)
- Supports multiple undo/redo operations
- Works for:
  - Add / Update / Delete operations
  - Cascade delete (client + all sessions)
- Implemented using:
  - Command pattern
  - Two-stack mechanism (undo/redo)

---

## 🏗️ Architecture
# 🏋️ Gym Management Application (JavaFX)

A Java-based Gym Management application developed using a layered architecture.  
The application supports multiple storage types, advanced filtering and reporting, and a full undo/redo system implemented using the Command design pattern.

---

## 📌 Features

### 👤 Client Management
- Add, update, delete clients
- Filter clients by name and email
- Cascade delete: removing a client also removes all associated sessions

### 📅 Session Management
- Add, update, delete sessions
- Filter sessions by client ID and date
- Sessions include date, time, and description

### 📊 Reports
- Sessions of a client between two dates
- Sessions containing a keyword
- Session count per client
- Next upcoming session per client
- Sessions grouped by day

### ↩️ Undo / Redo (Command Pattern)
- Supports multiple undo/redo operations
- Works for:
  - Add / Update / Delete operations
  - Cascade delete (client + all sessions)
- Implemented using:
  - Command pattern
  - Two-stack mechanism (undo/redo)

---

## 🏗️ Architecture

The project follows a clean layered architecture:
UI (JavaFX / Console)
↓
Service Layer (Business Logic)
↓
Repository Layer (Data Access)
↓
Domain Layer (Entities)

### Layers:
- **Domain** → `Client`, `Session`
- **Repository** → In-memory, text, binary, JDBC (SQLite)
- **Service** → Validation + business logic
- **UI** → JavaFX GUI + Console UI

---

## 💾 Storage Options

The application supports multiple repository types configured via `settings.properties`:

| Type      | Description |
|----------|------------|
| `memory` | In-memory storage (default) |
| `text`   | Text file storage |
| `binary` | Binary file storage |
| `database` | SQLite database |

Example configuration:
Repository=database
Location=data
Database=gym.db
Clients=clients
Sessions=sessions

## 🖥️ Technologies Used

- Java 17+
- JavaFX
- SQLite (JDBC)
- Object-Oriented Programming
- Design Patterns:
  - Command Pattern (Undo/Redo)
  - Adapter Pattern (Repository abstraction)



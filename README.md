# 📚 Library Management System (Web & Desktop Edition)

A modern, full-featured **Library Management System** with role-based authentication, real-time book catalog management, search & filtering, borrowing workflows, analytics metrics, and single-click **Vercel** deployment readiness via GitHub.

---

## ✨ Features

- 🔐 **Real Dual-Role Authentication (Sign In & Sign Up)**: Access control for **Admin** and **User/Student** accounts created via Sign Up.
- 🎨 **Glassmorphic Web Interface**: Built with modern CSS3 variables, dark mode glow aesthetics, Google Fonts (Outfit & Plus Jakarta Sans), and smooth micro-animations.
- 📖 **Book Catalog Management**:
  - Add, Edit, and Delete books (Admin).
  - Search by Title, Author, or ISBN in real-time.
  - Filter by availability status (*All*, *Available*, *Borrowed*, *My Borrowed*).
- 🔄 **Borrowing & Return Workflows**:
  - Issue books to users with instant status updates.
  - User self-borrow and return management.
- 📊 **Live Dashboard Metrics**: Dynamic counters for Total Books, Available Books, Issued Books, and Registered Members.
- 🌐 **Vercel Cloud Ready**: Built-in static configuration (`vercel.json`) for free 1-click web hosting.
- ☕ **Java Swing Desktop App**: Standalone Java Desktop application included with Maven and embedded H2 / MySQL database support.

---

## 🚀 Run Commands

### 1. Web Application Run Command
To start the Web Application locally:
```bash
npm run dev
```
*(Or `npm start`)*

---

### 2. Java Desktop Application Run Commands
To run the Java Swing desktop application:

- **Method A (Maven Direct Run)**:
  ```bash
  mvn clean compile exec:java
  ```

- **Method B (Windows Launcher Batch Script)**:
  ```cmd
  run.bat
  ```

- **Method C (Build Fat Executable JAR)**:
  ```bash
  mvn clean package
  java -jar target/library-management-system-1.0-SNAPSHOT.jar
  ```

---

## 🔐 Authentication Portal

1. **Sign Up**: Register your account as an **Admin** or **User/Student**.
2. **Sign In**: Select your role, enter your registered username and password to log in.
3. **Session Management**: Session stays active in your browser and can be ended anytime by clicking **Logout**.

---

## 🐙 Deploying to Vercel via GitHub

### Step 1: Push Project to GitHub
```bash
git init
git add .
git commit -m "Initial commit of Library Management System"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/library-management-system.git
git push -u origin main
```

### Step 2: Import & Deploy on Vercel
1. Go to **[vercel.com/new](https://vercel.com/new)** and sign in with your GitHub account.
2. Under **Import Git Repository**, select your `library-management-system` repository.
3. Click **Deploy**. Vercel will automatically host your web app live!

---

## 📁 Project Structure

```text
├── index.html            # Main Web Application HTML structure & Auth overlay
├── styles.css            # Glassmorphism UI theme & responsive design system
├── app.js                # Core Web App state, auth logic, and local storage engine
├── package.json          # Node package configuration & run scripts (npm run dev)
├── vercel.json           # Vercel deployment & routing configuration
├── run.bat               # Windows batch script launcher
├── pom.xml               # Maven configuration with MySQL, H2, & Shade plugin
├── src/                  # Java Swing Desktop Application source code
└── README.md             # Project documentation
```

---

## 🛡️ License

Distributed under the MIT License. Built with ❤️ for seamless library management.

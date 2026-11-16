# 📝 To-Do Application (Android)

> **Oasis Infobyte Student Internship Program (OIBSIP)** — Android Development Task

A simple and clean To-Do application for Android that lets users sign up, log in, and manage their daily tasks in one place. Built with **Java** and **XML** using Android Studio.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Screenshots](#-screenshots)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [How to Use](#-how-to-use)
- [Future Improvements](#-future-improvements)
- [Author](#-author)

---

## 📖 Overview

The To-Do Application helps users stay organized by keeping track of their tasks. Each user creates an account, logs in, and gets a personal task list where new tasks can be added through a simple dialog. The app keeps the user signed in using session management, so they don't have to log in every time they open it.

This project was developed as part of the **Oasis Infobyte Internship Program**.

---

## ✨ Features

- 🔐 **User Sign Up** — create a new account
- 🔑 **User Login** — secure access to personal tasks
- 💾 **Session Management** — stays logged in between app launches
- ➕ **Add Tasks** — quickly add a new task using a dialog box
- 📋 **Task List** — all tasks displayed in a scrollable list using RecyclerView
- 🎨 **Clean UI** — card-style task items with Material design look
- 🌙 **Light and Dark theme support**

---

## 🛠️ Tech Stack

| Category | Details |
|----------|---------|
| Language | Java |
| UI | XML layouts, Material Components |
| List display | RecyclerView + custom Adapter |
| IDE | Android Studio |
| Build system | Gradle (Kotlin DSL) |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 37 |

---

## 📸 Screenshots

| Login | Sign Up | Task List | Add Task |
|:-----:|:-------:|:---------:|:--------:|
| ![Login](screenshots/login.png) | ![Signup](screenshots/signup.png) | ![Tasks](screenshots/tasks.png) | ![Add Task](screenshots/add_task.png) |

> 📁 Add your screenshots inside a `screenshots` folder in this directory with the file names above.

---

## 🗂️ Project Structure

```
Task1-ToDoApp/
├── app/
│   └── src/main/
│       ├── java/com/example/todoapp/
│       │   ├── MainActivity.java       # Main screen with task list
│       │   ├── LoginActivity.java      # Login screen
│       │   ├── SignUpActivity.java     # Registration screen
│       │   ├── SessionManager.java     # Handles login session
│       │   ├── Task.java               # Task model
│       │   ├── TaskAdapter.java        # RecyclerView adapter
│       │   └── User.java               # User model
│       └── res/
│           ├── layout/                 # activity_login, activity_signup,
│           │                           # activity_main, dialog_add_task, item_task
│           ├── drawable/               # Backgrounds and icons
│           └── values/                 # Colors, strings, themes
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest version recommended)
- JDK 11 or higher
- Android device or emulator running Android 7.0 (API 24) or above

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/ranjita-Dev815/OIBSIP.git
   ```
2. **Open the project**
   - Launch Android Studio
   - Select **File → Open** and choose the `Task1-ToDoApp` folder
3. **Sync Gradle**
   - Wait for Gradle sync to finish
4. **Run the app**
   - Select an emulator or connected device
   - Click the **Run ▶** button

---

## 📱 How to Use

1. Open the app and tap **Sign Up** to create an account.
2. **Log in** with your credentials.
3. Tap the **Add** button to create a new task.
4. View all your tasks on the main screen.

---

## 🔮 Future Improvements

- Edit and delete tasks
- Mark tasks as completed
- Task deadlines and reminders
- Cloud sync with Firebase
- Search and filter tasks

---

## 👩‍💻 Author

**Ranjita Kumari**

- GitHub: [@ranjita-Dev815](https://github.com/ranjita-Dev815)

---

## 🙏 Acknowledgement

Thanks to **Oasis Infobyte** for the opportunity to work on this project as part of the Student Internship Program.

---

⭐ If you like this project, consider giving the repository a star!

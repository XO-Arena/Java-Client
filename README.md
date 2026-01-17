# 🎮 Network-Based Tic-Tac-Toe – Client Application

## 🚀 XO Arena Project
This project represents our **first milestone at ITI**, developed through strong teamwork and collaboration as part of the **XO Arena Team**.

---

## 👥 Team Members (XO Arena)
- **Mohannad El-Sayeh**
- **Ahmed El-Sayyad**
- **Esraa Ehab**
- **Mohamed Ayman**

---

## 📌 Overview
This project is the **Client-side application** of a **Network-Based Tic-Tac-Toe game** developed using **Java SE**.

It allows users to play Tic-Tac-Toe in:
- Single-player mode (against AI)
- Local multiplayer
- Online multiplayer over a network

The client provides a **modern JavaFX GUI**, handles game logic, communicates with the server in real time, and supports **game recording, replay, quick matchmaking, and spectator mode**.

---

## ✨ Features

### 🎯 Single Player Mode
- Play against the computer  
- Adjustable AI difficulty

### 👥 Multiplayer Mode
- Local multiplayer (same machine)
- Online multiplayer (different machines)

### ⚡ Quick Match
- Automatically matches players without manual invitations
- Fast and seamless matchmaking experience

### 👀 Spectator Mode
- Allows users to watch ongoing matches in real time
- Live updates of game moves from the server

### 🌐 Online Gameplay
- View available online users
- Send and receive game requests
- Accept or refuse invitations

### 🎥 Game Recording
- Record played games
- Replay saved matches later

### 🏆 Rewards System
- Bonus videos shown to winners

### 📊 Player Statistics
- View personal scores
- Track match history

### 🔐 Authentication
- User registration
- User login / logout

### 🎨 User-Friendly GUI
- Clean, responsive, and intuitive interface using JavaFX

---

## 🛠️ Technologies Used
- **Java SE**
- **JavaFX**
- **Sockets / TCP Networking**
- **Gson (JSON Serialization)**
- **Multithreading**
- **MVC Architecture**
- **JDBC**

---

## 📁 Project Structure
```text
client/
├─ controllers/     # JavaFX controllers
├─ models/          # Game and player models
├─ services/        # App Services & business logic
├─ dto/             # Data Transfer Objects
├─ enums/           # Enums for game states & types
├─ views/           # FXML UI files
├─ assets/
│  ├─ videos/       # Reward & game videos
│  └─ styles/       # CSS styles
└─ App.java         # Application entry point
```



## ⚙️ Requirements (What You Need to Run the Project)

Before running the client application, make sure you have the following installed on your device:

- **Java JDK 8 or higher** (Java SE)
- **JavaFX SDK** (if not bundled with your JDK)
- **IDE** (recommended):
  - IntelliJ IDEA
  - NetBeans
  - Eclipse
- **Running Server Application**
  - The client depends on the server to enable online features (Quick Match, Multiplayer, Spectator Mode)
- **Internet or Local Network Connection**

---

## ▶️ How to Run the Project

### 1️⃣ Run the Server First
- Clone and open the **Server Application** repository
- Run the server and make sure it is listening on the configured port
- Keep the server running before starting the client

### 2️⃣ Open the Client Project
- Clone this repository
- Open the project in your preferred IDE (IntelliJ / NetBeans)

### 3️⃣ Configure JavaFX
- If JavaFX is not bundled with your JDK:
  - Download JavaFX SDK
  - Configure VM options, for example:
    ```
    --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml
    ```

### 4️⃣ Run the Application
- Locate the `App.java` file
- Run it as a **Java Application**
- The client UI should launch successfully

└── App.java # Application entry point

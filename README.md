# Wall Street Tycoon

**Wall Street Tycoon** is an offline single-player stock trading game designed to be educational, engaging, and accessible to users of all experience levels. Created by *TheSigmas* (Liam Keech, Gareth Munnings, Téshan Olwage), this game simulates historical and fictional market events to help players learn core trading concepts while having fun.

---

## 🧠 Project Overview

In recent years, trading has become increasingly popular, but most platforms cater to experienced users. **Wall Street Tycoon** bridges this gap by offering a gamified stock market simulation featuring:

- Predefined, story-driven stock price movements
- Market events based on historical financial crises and booms
- Integrated educational mini-games
- Visual analytics like pie charts and transaction history
- Simple UI with a clear progression system

The objective is to reach a net worth goal (e.g., 1 trillion in-game currency) through wise investments and market timing.

---

## 🧩 Core Features

### 📈 Buy & Sell Stocks  
Trade using in-game currency with real-time updates

<p align="center">
  <img width="400" src="https://github.com/user-attachments/assets/97b22c93-e26f-4008-8eb6-ca7eb7631d5f" />
</p>

---

### 🧠 Mini-Games  

<p align="center">
  <img width="400" src="https://github.com/user-attachments/assets/7daa35ba-a324-4055-abd4-5e8fd2e22434" />
  <img width="400" src="https://github.com/user-attachments/assets/e1a032d2-657c-4542-ade7-2fff712213bc" />
</p>

<p align="center">
  <img width="400" src="https://github.com/user-attachments/assets/d96f15e4-e843-44aa-965b-50990c997b76" />
</p>

---

### 📊 Dynamic Portfolio View  

<p align="center">
  <img width="400" src="https://github.com/user-attachments/assets/c93b822f-b570-4b4c-a8da-f89d959dd179" />
</p>

---

### 🔔 Market Notifications  

<p align="center">
  <img width="400" src="https://github.com/user-attachments/assets/b705e207-f394-47d9-b806-fc051c530fe9" />
</p>

---

## 🎮 Game Structure

The game is divided into chapters, each simulating a distinct economic era:

1. **Tutorial** – Learn the basics of trading and controls.
2. **Dot-Com Boom** – React to early 2000s tech hype.
3. **Housing Bubble** – Navigate the 2008 financial crisis.
4. **Crypto Surge** – Enter the world of digital currencies.
5. **Corona Crash** – Trade during the COVID-19 pandemic.
6. **AI Revolution** – Ride the wave of artificial intelligence.

Each chapter includes:
- Market notifications and story prompts
- Pre-determined stock price changes
- Mini-games that influence your success
  
---

## 💾 Data Model (Entities)

- **User**: Personal details and account info
- **Stock**: Company metadata and pricing
- **StockPriceHistory**: Time-based changes per chapter
- **Portfolio**: Link between user and their stock holdings
- **MarketEvent**: Tied to chapters and mini-games
- **Minigame**: Individual mini-game metadata

---

## 🎨 UI/UX Design Guidelines

- **Primary Color:** Dark Blue `#2321D6`
- **Accent Colors:** Green `#48C73C`, Orange `#FF6417`
- **Typography:** Maketa Display (headings), Montserrat (body)
- **Design Language:** Clean, intuitive, and mobile-friendly

---

## ⚙️ Tech Stack

- Language: Java 11
- Database: SQLite
- Charts: MPAndroidChart v3.1.0
- Build: Gradle 8.9 with Android Gradle Plugin 8.7.3
- Tools: Android Studio

---

## 📲 Download & Install

Grab the latest sideloadable APK from the
**[Releases page](https://github.com/LiamKeech/WallStreetTycoon/releases/latest)**.

### Requirements

| | |
|---|---|
| **Minimum Android version** | **Android 13 (API 33)** |
| Target Android version | Android 15 (API 35) |
| Compiled against | compileSdk 35 |
| Architecture | Universal, no native code |
| Permissions | `INTERNET` |
| Download size | ~12.4 MB |

The app will **not** install on Android 12 or older.

### Installing on a phone

1. Download the APK to the device, or copy it across over USB.
2. Open it from the Files app or from the download notification.
3. Android will ask you to allow installs from whichever app opened it
   (Files, Chrome, and so on). Grant that, then tap **Install**.
4. Play Protect may warn that the developer is unknown. That is expected for a
   sideloaded build, choose **Install anyway**.

### Installing over USB

With USB debugging enabled on the device:

```bash
adb install -r WallStreetTycoon-v1.0-debug.apk
```

### A note on signing

The published APK is **debug-signed**, using the standard Android debug
certificate (`CN=Android Debug, O=Android, C=US`) with APK Signature Scheme v2.
That is fine for sideloading and testing. It is not a Play Store build, and a
future release-signed APK will not upgrade over it in place, so uninstall this
one first if that ever happens.

---

## 🚀 Building from Source

### Prerequisites

- **JDK 17 or 21.** AGP 8.7.3 will not run on JDK 25. Android Studio's bundled
  JBR works; point `JAVA_HOME` at it if your system default is newer.
- **Android SDK Platform 35** and the matching build tools.
- No Gradle install needed, the wrapper fetches Gradle 8.9 on first run.

### Clone and configure

```bash
git clone https://github.com/LiamKeech/WallStreetTycoon.git
cd WallStreetTycoon
```

Point the build at your SDK. In Android Studio, opening the project does this
for you. Otherwise create a `local.properties` file in the project root
(it is gitignored):

```properties
sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
```

On macOS or Linux:

```properties
sdk.dir=/Users/<you>/Library/Android/sdk
```

Alternatively, export `ANDROID_HOME` instead of creating the file.

### Build

```bash
# Windows
gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

The APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

If your default JDK is too new, override it for the build:

```bash
# Windows, using Android Studio's bundled JDK
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr" && gradlew.bat assembleDebug
```

### Install what you just built

```bash
./gradlew installDebug          # to a connected device or running emulator
```

Or open the project in Android Studio and press **Run**.

---

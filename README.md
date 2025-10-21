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

## 🧩 Core Features

- 📈 **Buy & Sell Stocks:** Trade using in-game currency with real-time portfolio updates
- 🧠 **Mini-Games:** Logic, reaction-time, and puzzle games tied to market outcomes
- 🧾 **Transaction History & Analytics:** Track your performance with visual dashboards
- 📊 **Dynamic Portfolio View:** See your holdings via interactive pie charts and value breakdowns
- 🔔 **Market Notifications:** In-game alerts drive the narrative and player decisions

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

- Language: Java
- Database: SQLite
- Tools: Android Studio

---

## ✅ Project Status

- [x] Functional specification complete
- [ ] Core features under development
- [ ] UI prototyping in progress
- [ ] Game loop and save/load features in testing

---

## 🚀 How to Run

1. Clone this repo:
   ```bash
   git clone https://github.com/LiamKeech/WallStreetTycoon

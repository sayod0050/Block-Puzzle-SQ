# BLOCK PUZZLE — SUBHON & SAYOD GAMES
## Edition: Premium 2026 | Android | Production Build

> **"THINK SMART. PLACE BOLD."**  
> *Created by Subhon Qodirov and Sadulloev Sayod. Made with passion by Subhon & Sayod Games.*

---

## 🌟 Official Game Identity
- **Game Title:** BLOCK PUZZLE
- **Studio & Developer:** Subhon & Sayod Games (S&S)
- **Official Creators:**
  1. **Subhon Qodirov**
  2. **Sadulloev Sayod**
- **Official Credit:** *Created by Subhon Qodirov and Sadulloev Sayod. Made with passion by Subhon & Sayod Games.*
- **Official Slogan:** *THINK SMART. PLACE BOLD.*

---

## 🎮 Game Overview & Core Engine
BLOCK PUZZLE by S&S GAMES is an original, ultra-responsive mobile puzzle experience built natively for Android using Kotlin, Jetpack Compose, and custom hardware-accelerated Canvas rendering.

### 🧩 1. The 10 × 10 Board & Polyomino Shapes
- **Board:** Standard 10 × 10 matrix with inset cell styling and glossy 3D block materials.
- **Library of 24+ Shapes:**
  - **1-Cell:** Single Dot
  - **2-Cell:** Domino Horizontal & Vertical
  - **3-Cell:** Line-3 H & V, 4 Corner Trominoes (TL, TR, BL, BR)
  - **4-Cell:** Square 2×2, Line-4 H & V, T-shapes, L-shapes, J-shapes, S & Z tetrominoes
  - **5-Cell:** Line-5 H & V, Plus (+), Large 3×3 Corners, Big 3×3 Square
- **Controlled Deal Generator:**
  - Evaluates board occupancy to guarantee at least one legal move upon deal.
  - Dynamically prevents impossible or unfairly repetitive giant shape deals.

### 🎯 2. Precision Drag-and-Drop
- Responsive pointer tracking with a **-80dp ergonomic vertical offset** so the player's thumb never blocks the target placement cells.
- Real-time snapping to the nearest 10×10 grid coordinates.
- **Ghost Preview:** Highlights destination cells with neon cyan glow (valid) or soft crimson (invalid).
- Touch release places block or smoothly snaps piece back into the tray.

### 🔥 3. Signature Feature — S&S STREAK™
- When players clear lines across consecutive turns, their **S&S Streak** meter ignites (x1, x2, x3, x4, x5+).
- **Animated Flame Meter:** Dynamic fire intensity and scaling.
- **Score Multiplier:** Earns up to +250% bonus points on subsequent clears.
- If a block is placed without clearing any line, the streak calmly resets.

---

## 🕹️ 5 Complete Game Modes

1. **CLASSIC MODE**
   - The timeless, endless 10×10 block puzzle experience.
   - High-score hunting, S&S Streak combos, and board management.
2. **TIME ATTACK**
   - 3-minute fast-paced countdown.
   - Clearing single lines awards **+5 seconds**, double clears award **+15 seconds**, and triple/mega clears award **+25 seconds**.
3. **ZEN MODE**
   - Tranquil, stress-free puzzle harmony.
   - Unlimited Undo, Hint, and Shuffle power-ups without game-over pressure.
4. **CHALLENGE MODE (30 Handcrafted Solvable Stages)**
   - Progressive difficulty stages with varied win conditions (Target Score, Line Clears, or S&S Streak goals under strict move limits).
   - 3-Star rating system with saved progression.
5. **DAILY PUZZLE**
   - Deterministic seed generation based on global calendar date (`YYYY-MM-DD`).
   - Identical shape deals globally so players can compete fairly every single day.

---

## 🛠️ Power-ups & Player Tools
- **UNDO:** Full state restoration (board, tray blocks, score, streak, and move count).
- **HINT:** Scans remaining pieces and board to highlight an optimal legal placement.
- **SHUFFLE:** Rerolls the current unused tray blocks with 3 balanced pieces.

---

## 🎨 3 Visual Themes
1. **Midnight (Default):** Deep obsidian background with neon cyan, ultraviolet, and amber jewel blocks.
2. **Aurora:** Cosmic indigo and magenta gradient with emerald mint and peach glow blocks.
3. **Pure:** Clean, bright minimalist theme with vivid pastel gem blocks.

---

## 🌍 Tri-Lingual Localization
Instant, seamless in-game language switching:
- **English (EN)**
- **Russian (RU — Русский)**
- **Tajik (TG — Тоҷикӣ)**

---

## 🔊 Audio & Haptic Feedback
- **Zero Missing Audio Dependencies:** Real-time procedural PCM waveform synthesis powered by Android `AudioTrack`:
  - Soft click, block pick, block snap thud, line clear harmony, combo arpeggio, S&S Streak fanfare, game over, and stage victory chords.
  - Soothing ambient pentatonic background music generator with separate volume sliders and mute toggles.
- **Haptics:** Android `Vibrator` tactile pulses on block drops, line clears, and S&S Streaks.

---

## 🚀 GitHub Actions CI/CD & Automated Releases
This repository includes a production-ready **GitHub Actions CI/CD pipeline** located at `.github/workflows/build_and_release.yml`:
- **Continuous Integration (CI):** On every push or pull request to `main` or `master`, the workflow automatically checks out the code, configures Java 17, and runs the entire automated unit test suite.
- **Automated Builds:** Builds the debug APK, release APK, and Android App Bundle (AAB).
- **Artifact Publishing:** Automatically uploads build artifacts for immediate download.
- **Automated GitHub Releases:** When a release tag is pushed (e.g. `git tag v1.0.0 && git push origin v1.0.0`) or when triggered manually via `workflow_dispatch`, GitHub Actions automatically creates a published GitHub Release and attaches `BlockPuzzle_S&S_Release.apk`, `BlockPuzzle_S&S_Debug.apk`, and `BlockPuzzle_S&S_Bundle.aab`.

---

## 🔒 100% Offline & Player Trust
- **Zero Trackers, Zero Ads, Zero Data Collection.**
- All high scores, statistics, achievements, challenge progress, and active sessions are stored locally via Android SharedPreferences and JSON.
- Seamless active session saving: players can quit anytime and resume right where they left off.

---

## 👥 Official Developer Credits
**BLOCK PUZZLE**  
*Created by Sadulloev Sayod and Subhon Qodirov.*  
*Made with passion by S&S GAMES.*  
*Slogan: THINK SMART. PLACE BOLD.*

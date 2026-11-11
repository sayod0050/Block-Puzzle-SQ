# 📱 How to Download the Standalone APK Directly (No ZIP!)

### Why did GitHub download a `.zip` before?
In GitHub Actions, the **"Artifacts"** section at the bottom of a workflow run automatically compresses files into a `.zip` archive (e.g. `Block-Puzzle-SQ-APK.zip`). That is normal GitHub behavior for build artifacts.

---

## 🌟 The Solution: Direct APK Download via GitHub Releases

With our updated workflow, every time you push to `main` or push a tag, GitHub Actions **automatically publishes a GitHub Release**!

In **GitHub Releases**, files are NOT zipped — they are raw `.apk` files!

### How to get the direct `.apk` on your phone:
1. Open your repository on GitHub: `https://github.com/YOUR_USERNAME/YOUR_REPO/releases`
2. Under the latest release (**"BLOCK PUZZLE — S&S GAMES"**), look under **Assets**.
3. Tap **`BlockPuzzle_S&S.apk`**.
4. Your Android browser will download the `.apk` directly (as an `.apk`, **not** a `.zip`!).
5. Tap the downloaded `.apk` and click **Install**.

---

## 🚀 How to Push to GitHub

```bash
# 1. Add your GitHub remote repository (if not already added)
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git

# 2. Push to GitHub
git push -u origin main
```

As soon as you run `git push`, GitHub Actions will:
1. Automatically run all unit tests.
2. Build `BlockPuzzle_S&S.apk` (and `BlockPuzzle_S&S_Release.apk`).
3. Automatically create/update the GitHub Release at `https://github.com/YOUR_USERNAME/YOUR_REPO/releases` with the direct `.apk` attached.
4. You simply open the link, tap `BlockPuzzle_S&S.apk`, and install it immediately!

---

## 👥 Official Credits
**BLOCK PUZZLE**  
*Created by Sadulloev Sayod and Subhon Qodirov.*  
*Made with passion by S&S GAMES.*  
*Slogan: THINK SMART. PLACE BOLD.*

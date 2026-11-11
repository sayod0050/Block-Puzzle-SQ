# 🚀 Quick GitHub Setup & Automated Release Guide

Your project is fully initialized with **Git** and configured with a complete **GitHub Actions CI/CD & Automated Release Pipeline** (`.github/workflows/build_and_release.yml`).

---

## 1. Push Code to Your GitHub Repository

On your machine or terminal, run:

```bash
# 1. Add your GitHub remote repository (replace with your repo URL)
git remote add origin https://github.com/YOUR_USERNAME/block-puzzle-ss-games.git

# 2. Push to GitHub
git push -u origin main
```

---

## 2. Automated GitHub Actions Workflows

As soon as you push to `main`:
1. **GitHub Actions** automatically runs `.github/workflows/build_and_release.yml`.
2. It sets up Java 17 and the Android SDK.
3. It executes the automated unit test suite (`:app:testDebugUnitTest`).
4. It builds:
   - **`BlockPuzzle_SS_Games_v1.0.0_release.apk`** (Release APK)
   - **`BlockPuzzle_SS_Games_v1.0.0_debug.apk`** (Debug APK)
   - **`BlockPuzzle_SS_Games_v1.0.0_release.aab`** (Google Play Store App Bundle)
5. It uploads the binaries under the **Artifacts** section of the GitHub Actions run for instant download.

---

## 3. Create an Automated GitHub Release with APKs Attached

Whenever you want to publish a new official version (e.g. `v1.0.0`), simply push a git tag:

```bash
# Tag the current release
git tag v1.0.0

# Push the tag to GitHub
git push origin v1.0.0
```

GitHub Actions will automatically:
- Trigger the **Release** job.
- Package the APKs and AAB.
- Publish a **GitHub Release** with download links to `BlockPuzzle_SS_Games_v1.0.0_release.apk`, `BlockPuzzle_SS_Games_v1.0.0_debug.apk`, and `BlockPuzzle_SS_Games_v1.0.0_release.aab`.
- Generate release notes featuring **S&S GAMES** and creators **Sadulloev Sayod & Subhon Qodirov**.

### Manual Release (One-Click)
You can also trigger a release directly from the GitHub UI:
1. Go to **Actions** tab in your GitHub repository.
2. Select **"Build and Release Block Puzzle S&S Games"**.
3. Click **"Run workflow"**, choose the version tag (e.g. `v1.0.0`), and check **Create GitHub Release**.

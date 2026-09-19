# Ledger & Board — Chess Puzzle Trainer

A self-contained puzzle trainer built from your two PGN files:

- **Practical Chess Endings** — 300 king-and-pawn / technique endings
- **Chess Strategy: Simplified** — 108 puzzles across 8 chapters, plus the lesson text between them

Tap a piece, then tap the square to move it to. Wrong tries flash red and let you
retry. The solution (with all the original annotator comments) stays hidden until
you tap **Show answer**, or until you solve the puzzle yourself. Progress (solved /
viewed) is saved in the browser on that device, so it's private to whoever is using it.

Everything lives in one folder — no build step, no server-side code, no database.

## Try it locally

Just double-click `index.html`, or run a tiny local server from this folder:

```bash
python3 -m http.server 8000
```

then open `http://localhost:8000`.

## Put it online with GitHub Pages (free, ~2 minutes)

1. Create a new repository on GitHub (public or private — Pages works with both on
   paid plans; public repos get Pages free).
2. Upload every file in this folder to the root of that repository (`index.html`,
   `manifest.json`, `service-worker.js`, the three `icon-*.png` files, `.nojekyll`).
   Easiest way: drag-and-drop them on the repo's "Add file → Upload files" page,
   or:
   ```bash
   git init
   git add .
   git commit -m "Chess puzzle trainer"
   git branch -M main
   git remote add origin https://github.com/<your-username>/<your-repo>.git
   git push -u origin main
   ```
3. In the repo, go to **Settings → Pages**. Under "Build and deployment", set
   **Source** to "Deploy from a branch", branch `main`, folder `/ (root)`. Save.
4. GitHub gives you a URL like `https://<your-username>.github.io/<your-repo>/`.
   It's usually live within a minute. Open it — the trainer should load and work
   exactly like the version you tried in this chat.

The app caches itself for offline use after the first visit (via `service-worker.js`),
so it keeps working on a phone with no signal.

## Turn it into an installable Android APK

An APK has to be built from a hosted URL, so deploy to GitHub Pages first (above),
then use one of these:

### Option A — PWABuilder (no coding, ~5 minutes)

1. Go to **[pwabuilder.com](https://www.pwabuilder.com)**.
2. Paste your GitHub Pages URL and click **Start**. PWABuilder reads
   `manifest.json` and `service-worker.js` automatically and scores the app.
3. Click **Package for stores → Android**.
4. Leave the defaults (package ID, app name, colors are already set from the
   manifest) and click **Generate**. It downloads a `.zip`.
5. Unzip it — inside is a ready-to-install **`.apk`** (and an `.aab` for the Play
   Store, if you ever want to publish it there). Sideload the `.apk` onto an
   Android phone (you may need to enable "install from unknown sources"), or
   share the file directly.

This produces a Trusted Web Activity: a real Android app that wraps this exact
web app, with its own icon and no browser chrome.

### Option B — Bubblewrap CLI (for developers who want more control)

Requires Node.js and the Android SDK/JDK installed locally:

```bash
npm install -g @bubblewrap/cli
bubblewrap init --manifest https://<your-username>.github.io/<your-repo>/manifest.json
bubblewrap build
```

This walks you through signing keys and produces `app-release-signed.apk` in the
project folder.

## Editing the puzzle set

All puzzle data is embedded as a single JSON blob near the top of `index.html`
(inside the first `<script>` tag, assigned to `window.__PUZZLE_DATA__`). Each
puzzle has `fen`, `moves` (the solution line in SAN), `moveComments` (annotator
notes keyed by move index), and `intro` (the text shown above the board). Add,
edit, or regenerate this JSON to update the puzzle set — the rest of the app
reads from it automatically.

## Credits

Puzzle content comes from `Practical-chessendings.pgn` and
`Chess_Strategy_Simplified.pgn`. Move validation runs on
[chess.js](https://github.com/jhlywa/chess.js) (loaded from cdnjs at runtime).

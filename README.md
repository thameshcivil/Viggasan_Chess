# Vigaasan's Chess Board — Chess Puzzle Trainer

A self-contained puzzle trainer built from two PGN collections, with:

- **Your own PGN library** — upload any `.pgn` file with `[FEN]` puzzle positions
  (tap **+ Add PGN** in the top strip, or use the 📊 **Stats & library** panel).
  Files are parsed in the browser and saved on-device; nothing is uploaded anywhere.
- **A timer and a rating** — each puzzle is timed, and a lightweight Elo-style
  rating (starts at 1200) adjusts after every solve or "show answer," based on
  how hard the puzzle was relative to your current rating. See it all in the
  📊 panel: rating, accuracy, puzzles solved, and a small trend line.
- **A colour-coded puzzle picker** — the number strip below the puzzle/chapter
  chips lets you jump to any puzzle directly, and shows at a glance which ones
  are solved (green), answered wrong / answer shown (red), opened but not
  finished (amber), or never opened (plain).
- **An automatic Android build** — push to `main` and GitHub Actions builds a
  downloadable `.apk` for you (see below).

Everything lives in one folder — no local build step for the *website*, no
server-side code, no database.

## Put it online with GitHub Pages (do this first)

1. Push every file in this folder to the root of your GitHub repository
   (`index.html`, `manifest.json`, `service-worker.js`, the `icon-*.png`
   files, `.nojekyll`, and the `.github/workflows/build-apk.yml` workflow).
2. **Settings → Pages** → Source: "Deploy from a branch" → Branch `main`,
   folder `/ (root)` → Save.
3. Your site goes live at `https://<your-username>.github.io/<your-repo>/`
   within about a minute.

## About the browser address bar

Visiting the Pages URL in a browser (or an in-app browser / Custom Tab, which
is what shows the `X`, share icon, and URL bar) will always show that
browser's own toolbar — that's the browser's UI, not something a website can
turn off. To get the app-like, chrome-free view, **install it**: open the
site in Chrome, use the browser menu → **"Add to Home Screen"** (or Chrome
may prompt you automatically), then always launch it from that home-screen
icon rather than a link. That launches it in `display: standalone` mode with
no address bar, using the name and icon from `manifest.json`.

If you'd previously added it to your home screen *before* an update, the
shortcut keeps the old name/icon — remove it and re-add it to pick up
changes, since Android only reads the manifest at install time. Likewise,
because of the offline service worker, a browser tab can keep showing a
cached older version for a visit or two after an update; a hard refresh or
closing and reopening the tab clears it.

## Download the APK from Actions

Once Pages is live, the **Build Android APK** workflow
(`.github/workflows/build-apk.yml`) builds the app automatically on every
push to `main`, or on demand from **Actions → Build Android APK → Run
workflow**. Open that run and scroll to the bottom of the summary page for
the **Artifacts** section — **`ledger-and-board-apk`** contains
`app-release-signed.apk`, ready to sideload onto an Android phone.

**Important — one-time setup required, or the installed app will just open
as a browser tab:** the workflow needs two repository secrets
(`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`) so it signs every build with the
same key, and your GitHub Pages site needs a small `assetlinks.json` file
published at your domain's root proving that key is authorized to
represent it. Without both of these, Android falls back to showing the
address bar — which is exactly what a TWA is supposed to do when it can't
verify the app, as a safety measure. Full step-by-step instructions for
both parts are in the separate signing-setup package (ask for it if you no
longer have it) — it's a short one-time setup, not something you repeat
per build.

The signing key is now a stable key reused on every run (set up per the
instructions above), not a fresh one each time — that instability was
exactly what broke the browser-bar verification originally. This key is
for sideloading/testing; publishing updates through the Google Play Store
would need its own separate signing/release process.

### Alternative: PWABuilder (no CI)

Paste your GitHub Pages URL into [pwabuilder.com](https://www.pwabuilder.com)
→ **Package for stores** → **Android** → **Generate** → unzip → `.apk` is
inside.

## Using your own PGN files in the app

Tap **+ Add PGN** in the collection strip (or **📊 → Upload PGN file(s)**).
Each game needs a `[FEN "..."]` header and at least one move in the mainline
to be treated as a puzzle; games without a FEN show as plain lesson/text
cards. Uploaded files appear as their own chip, marked 📄, and can be removed
any time from the 📊 panel. Everything is stored in the browser's local
storage on that device — there's no server, so files don't sync between
devices or browsers.

## How solving works

Tap a piece, then tap the destination square. Wrong tries flash red and let
you retry; **Hint** highlights the correct piece to move; **Show answer**
plays out the full solution with the original annotator's comments. A timer
runs while you solve — solving without "Show answer" raises your rating,
using it without solving lowers it. Progress, timing history, and rating are
all saved locally per device.

## Credits

Built-in puzzle content comes from `Practical-chessendings.pgn` and
`Chess_Strategy_Simplified.pgn`. Move validation runs on
[chess.js](https://github.com/jhlywa/chess.js) (loaded from cdnjs at
runtime). Android packaging uses
[Bubblewrap](https://github.com/GoogleChromeLabs/bubblewrap).

# Vigaasan’s Chess Board

A chess puzzle trainer that looks like a printed chess book. Each puzzle is a
numbered plate with a framed diagram, and your moves are written into a
ledger beside it. It runs as a single web page, installs on a phone like an
app, works offline, and has no server or database.

- **Two built-in collections:** *Practical Chess Endings* (300 puzzles) and
  *Chess Strategy: Simplified* (108 puzzles plus 25 short lessons, in 10
  chapters).
- **Your own PGN files:** tap **+ PGN** in the collection tabs, or use
  **ELO → Upload PGN file(s)**. Files are parsed in the browser and saved on
  that device only. To delete a file, tap the 🗑 trash icon next to its tab
  or in the ELO sheet.
- **Timer and rating:** each puzzle is timed. An Elo-style rating starts at
  1200 and moves once per puzzle, on your first result:
  - a clean solve counts fully;
  - hints and wrong tries count for less;
  - revealing the answer counts as a miss.

  Tap the **ELO** button for your rating, puzzles solved, accuracy, average
  solve time and rating trend.
- **Plate index:** the numbered squares under the tabs let you jump to any
  puzzle:
  - green: solved
  - red: answer revealed
  - amber: opened, not finished
  - outlined: never opened
- **It remembers where you were** in each collection and chapter.
- **Light and dark themes** follow the phone's setting; **◐** overrides it.
  **⇅** flips the board.
- **Layouts:** in portrait everything stacks. On tablets and phones in
  landscape it becomes three columns: index, board, then the ledger.

## Put it online with GitHub Pages

1. Push every file in this folder to the root of the repository. That means
   `index.html`, `manifest.json`, `service-worker.js`, the `icon-*.png`
   files, `.nojekyll`, and `.github/workflows/build-apk.yml`.
2. Go to **Settings → Pages**. Set Source to "Deploy from a branch", choose
   branch `main` and folder `/ (root)`, then Save.
3. The site goes live at `https://<your-username>.github.io/<your-repo>/`
   within a minute or two.

Updates show up the next time you open the app, because the page is loaded
network-first and served from cache only when you're offline.

## Install it on a phone

**Android app (recommended):** the **Build Android APK** workflow runs on
every push to `main`. You can also start it from **Actions → Build Android
APK → Run workflow**. Open the latest run and download
**`vigaasans-chess-board-apk`** from **Artifacts**. Unzip it and install
`vigaasans-chess-board.apk`.

The app (in `android/`) is a full-screen WebView around the live site:

- **No browser bar, ever.** It's a WebView, not a Chrome tab, so there's no
  address bar, share button or ✕, and no `assetlinks.json` or domain setup.
- **Updates itself.** It loads the GitHub Pages site, so changes you push
  appear the next time you open the app. You rarely need a new APK.
- **Works offline** after the first launch.
- **Native touches:** **+ PGN** and **Restore backup** open the phone's file
  picker, and **Export backup** saves a `.json` file to your Downloads.

**Browser:** you can also open the Pages URL in Chrome and choose
**Add to Home Screen**.

### Moving your progress (backup & restore)

Each app and browser keeps its own progress. To move it, tap **ELO → Export
backup** where your progress is now. Then on the new app or device, tap
**ELO → Restore backup → Choose file**, pick the backup, and tap
**Restore**.

The backup includes your rating, solved puzzles and uploaded PGN files.

### Signing (optional)

Without setup, each APK build is signed with a one-off key. It works
normally, but to install a *newer* APK you must uninstall the old one first.
Export a backup before you do.

To make new APKs install straight over old ones, set this up once:

1. **Create a keystore:**
   `keytool -genkeypair -keystore vigaasan.keystore -alias vigaasan -keyalg RSA -keysize 2048 -validity 10000`
2. **Add repository secrets** under Settings → Secrets and variables →
   Actions:
   - `KEYSTORE_BASE64`: the output of `base64 -w0 vigaasan.keystore`
   - `KEYSTORE_PASSWORD`: your password
   - `KEYSTORE_ALIAS` (optional): only needed if the alias isn't `vigaasan`

Keep the keystore safe.

### Why the old app showed an address bar

The previous APK was a Trusted Web Activity, which is a Chrome tab without
the toolbar. Android hides that toolbar only after verifying the site
through `/.well-known/assetlinks.json` at the domain root, signed with a
stable key. Neither was set up, so Chrome fell back to showing the URL bar,
✕ and share button. The WebView app replaces it and has no toolbar at all.
Uninstall the old "Vigaasan" app once you've moved your progress.

## Using your own PGN files

Each game needs a `[FEN "..."]` header and at least one move in the main line
to become a puzzle. Games without a FEN appear as text lessons. If the
`White` header is shared by several games, it's used as the chapter name;
`Black` is used as the puzzle title. Variations in parentheses are ignored,
and comments in `{...}` appear in the ledger. Removing a file in the stats
sheet also removes its progress.

## How solving works

Tap a piece, then its destination square; the legal destinations are dotted.

- **Wrong tries** shake the feedback line and let you retry.
- **Hint** marks the piece to move.
- **Reveal** plays out the rest of the line with the annotator's comments,
  keeping any moves you've already played.
- **Reset** starts the puzzle again.

The opponent's replies play automatically.

## Credits

Built-in content comes from `Practical-chessendings.pgn` and
`Chess_Strategy_Simplified.pgn`. Move validation uses
[chess.js](https://github.com/jhlywa/chess.js) 0.10.3 from cdnjs. Fonts are
Newsreader and JetBrains Mono from Google Fonts. The Android app is a plain
Android WebView project (no extra libraries).

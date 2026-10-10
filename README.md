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
  that device only.
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

Open the Pages URL in Chrome and use the menu → **Add to Home Screen** (or
**Install app**). Launch it from that icon to get the full-screen view with
no address bar. If you installed an older version, remove the icon and add
it again to pick up the new name and icon.

## Download the Android APK

The **Build Android APK** workflow runs on every push to `main`. You can also
start it from **Actions → Build Android APK → Run workflow**. Open the run
and download **`vigaasans-chess-board-apk`** from the **Artifacts** section
at the bottom. It contains `app-release-signed.apk`.

### Stable signing (needed to hide the address bar)

Without any setup, the workflow signs each build with a one-off key and
shows a warning. That APK installs and works for testing, but Android shows
a browser address bar, and a new build can't install over the old one.

To fix both, set up these three things once:

1. **Create a keystore** on your computer (any machine with Java):
   `keytool -genkeypair -keystore vigaasan.keystore -alias vigaasan -keyalg RSA -keysize 2048 -validity 10000`
2. **Add repository secrets** under Settings → Secrets and variables →
   Actions:
   - `KEYSTORE_BASE64`: the output of `base64 -w0 vigaasan.keystore`
   - `KEYSTORE_PASSWORD`: the password you chose
   - `KEYSTORE_ALIAS` (optional): only needed if the alias isn't `vigaasan`
3. **Publish `assetlinks.json` at your domain root.** It must be at
   `https://<your-username>.github.io/.well-known/assetlinks.json`. That is
   the root of the domain, *not* this project's folder, so it lives in a
   separate repository named `<your-username>.github.io`, which also needs
   a `.nojekyll` file. Use the package ID `io.github.<username>.ledgerboard`
   and the SHA-256 fingerprint that the workflow prints in its "Print the
   signing fingerprint" step.

Keep the keystore file somewhere safe. Losing it means existing installs can
never be updated.

### Alternative: PWABuilder (no CI)

Paste your Pages URL into [pwabuilder.com](https://www.pwabuilder.com), then
choose **Package for stores → Android → Generate**.

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
Newsreader and JetBrains Mono from Google Fonts. Android packaging uses
[Bubblewrap](https://github.com/GoogleChromeLabs/bubblewrap).

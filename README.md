# Ledger & Board — Chess Puzzle Trainer

A self-contained puzzle trainer, now with:

- **Your own PGN library** — upload any `.pgn` file with `[FEN]` puzzle positions
  (tap **+ Add PGN** in the top strip, or use the 📊 **Stats & library** panel).
  Files are parsed in the browser and saved on-device; nothing is uploaded anywhere.
- **A timer and a rating** — each puzzle is timed, and a lightweight Elo-style
  rating (starts at 1200) adjusts after every solve or "show answer," based on
  how hard the puzzle was relative to your current rating. See it all in the
  📊 panel: rating, accuracy, puzzles solved, and a small trend line.
- **Bigger, crisper pieces and icons** — piece glyphs are ~70% larger and no
  longer get swapped for tiny colour-emoji versions on Android; app icons were
  regenerated at high resolution with anti-aliased edges.
- **An automatic Android build** — push to `main` and GitHub Actions builds a
  downloadable `.apk` for you (see below). No PWABuilder step required anymore,
  though that route still works too if you ever prefer it.

Everything still lives in one folder — no local build step for the *website*,
no server-side code, no database.

## Put it online with GitHub Pages (do this first)

1. Push every file in this folder to the root of your GitHub repository
   (`index.html`, `manifest.json`, `service-worker.js`, the `icon-*.png`
   files, `.nojekyll`, and the `.github/workflows/build-apk.yml` workflow).
2. **Settings → Pages** → Source: "Deploy from a branch" → Branch `main`,
   folder `/ (root)` → Save.
3. Your site goes live at `https://<your-username>.github.io/<your-repo>/`
   within about a minute.

## Download the APK from Actions (what you asked for)

Once Pages is live, the **Build Android APK** workflow
(`.github/workflows/build-apk.yml`) builds the app automatically:

1. It runs on every push to `main`, or on demand from **Actions → Build
   Android APK → Run workflow**.
2. Open that workflow run and scroll to the bottom of the summary page —
   there's an **Artifacts** section with **`ledger-and-board-apk`**.
3. Click it to download a `.zip`; inside is **`app-release-signed.apk`**,
   ready to sideload onto an Android phone.

This replaces the old "Android CI" template that failed for this repo (that
one expected a native Android Studio project, which a PGN trainer doesn't
have). This workflow instead uses **Bubblewrap** to wrap your live Pages URL
as a Trusted Web Activity and compiles a real APK on GitHub's servers — no
Android Studio, no local setup.

A few things worth knowing about this workflow:

- It generates a **fresh signing key on every run**, which is fine for
  installing/testing but means each build is signed differently. If you ever
  want a stable key (e.g. to publish updates without Android complaining
  about a "conflicting signature"), add a repository secret named
  `KEYSTORE_PASSWORD` and let me know if you'd like the workflow adjusted to
  reuse a committed keystore instead of generating a new one each time.
- It assumes the default `https://<owner>.github.io/<repo>/` Pages URL. If
  you're using a custom domain for Pages, the `host` value in the workflow's
  generated `twa-manifest.json` step needs to be edited to match.
- If a run fails, open the failing step's log and send it my way — CI
  environments occasionally shift (Android SDK paths, tool versions) and the
  workflow may need a small update.

### Alternative: PWABuilder (no CI, if you'd rather not wait on Actions)

Still works exactly as before: paste your Pages URL into
[pwabuilder.com](https://www.pwabuilder.com) → **Package for stores** →
**Android** → **Generate** → unzip → `.apk` is inside.

## Using your own PGN files in the app

Tap **+ Add PGN** in the collection strip (or **📊 → Upload PGN file(s)**).
Pick one or more `.pgn` files — each game needs a `[FEN "..."]` header (a
starting position) and at least one move in the mainline to be treated as a
puzzle; games without a FEN are shown as plain lesson/text cards, same as the
built-in "Chess Strategy" chapters. Uploaded files appear as their own chip
in the collection strip, with a 📄 icon, and can be removed any time from the
📊 panel. Everything is stored in the browser's local storage on that device;
there's no server, so files don't sync between devices or browsers.

If local storage is full or unavailable (e.g. private browsing), the app
will tell you rather than silently losing your upload.

## How solving works

Tap a piece, then tap the destination square. Wrong tries flash red and let
you retry; **Hint** highlights the correct piece to move; **Show answer**
plays out the full solution with the original annotator's comments. A timer
runs while you solve — solving without using "Show answer" updates your
rating up, using "Show answer" without solving counts as a miss. Progress,
timing history, and rating are all saved locally per device.

## Editing the built-in puzzle set

The two built-in collections are embedded as JSON near the top of
`index.html` (inside the first `<script>` tag, assigned to
`window.__PUZZLE_DATA__`). To regenerate them from the original PGNs, or to
add a third built-in collection, ask me and I can rebuild that block —
uploaded files don't need this, they're parsed live in the browser.

## Credits

Puzzle content originally comes from `Practical-chessendings.pgn` and
`Chess_Strategy_Simplified.pgn`. Move validation runs on
[chess.js](https://github.com/jhlywa/chess.js) (loaded from cdnjs at
runtime). Android packaging uses
[Bubblewrap](https://github.com/GoogleChromeLabs/bubblewrap).

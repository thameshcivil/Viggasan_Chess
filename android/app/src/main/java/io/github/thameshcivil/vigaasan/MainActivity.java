package io.github.thameshcivil.vigaasan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Vigaasan's Chess Board — a full-screen WebView around the live site.
 *
 * Unlike the previous Trusted Web Activity build, a WebView never shows a
 * browser toolbar, so there is no address bar to hide and no domain
 * verification (assetlinks.json) to set up. Because it loads the live
 * GitHub Pages site, web updates appear without reinstalling the APK; the
 * site's service worker keeps it working offline after the first launch.
 */
public class MainActivity extends Activity {

    private static final int REQ_FILE = 41;
    private static final int REQ_STORAGE = 42;
    private static final int PLUM = Color.parseColor("#1D0B12");

    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private String pendingBackupName, pendingBackupText;
    private Uri siteUri;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        siteUri = Uri.parse(BuildConfig.SITE_URL);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(PLUM);
        webView = new WebView(this);
        webView.setBackgroundColor(PLUM);
        root.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);

        // Keep the page clear of the status bar, navigation bar and any
        // camera cutout (Android 15 draws apps edge-to-edge by default).
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            int l, t, r, b;
            if (Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets in = insets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout() | WindowInsets.Type.ime());
                l = in.left; t = in.top; r = in.right; b = in.bottom;
            } else {
                l = insets.getSystemWindowInsetLeft(); t = insets.getSystemWindowInsetTop();
                r = insets.getSystemWindowInsetRight(); b = insets.getSystemWindowInsetBottom();
            }
            v.setPadding(l, t, r, b);
            return Build.VERSION.SDK_INT >= 30 ? WindowInsets.CONSUMED : insets.consumeSystemWindowInsets();
        });
        getWindow().setStatusBarColor(PLUM);
        getWindow().setNavigationBarColor(PLUM);
        getWindow().getDecorView().setSystemUiVisibility(0); // light icons on the dark bars

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setTextZoom(100);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setUserAgentString(s.getUserAgentString() + " VigaasanApp/" + BuildConfig.VERSION_NAME);

        webView.addJavascriptInterface(new Bridge(), "VigaasanBridge");
        webView.setWebViewClient(new FolioClient());
        webView.setWebChromeClient(new FolioChrome());

        if (savedInstanceState != null) webView.restoreState(savedInstanceState);
        else webView.loadUrl(siteUri.toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onPause() { webView.onPause(); super.onPause(); }

    @Override
    protected void onResume() { super.onResume(); webView.onResume(); }

    @Override
    protected void onDestroy() {
        if (webView != null) { webView.destroy(); webView = null; }
        super.onDestroy();
    }

    private boolean isOwnPage(Uri u) {
        return u != null && siteUri.getHost() != null && siteUri.getHost().equalsIgnoreCase(u.getHost())
                && u.getPath() != null && u.getPath().startsWith(siteUri.getPath() == null ? "/" : siteUri.getPath());
    }

    private class FolioClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri u = request.getUrl();
            if (isOwnPage(u)) return false;
            // Anything else (e.g. a credits link) opens in the phone's browser.
            try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (ActivityNotFoundException ignored) { }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (!request.isForMainFrame()) return;
            String html = "<!doctype html><meta name=viewport content='width=device-width,initial-scale=1'>"
                    + "<body style='margin:0;min-height:100vh;display:flex;flex-direction:column;align-items:center;justify-content:center;"
                    + "background:#1d0b12;color:#f3e6d3;font-family:Georgia,serif;text-align:center;padding:24px'>"
                    + "<div style='font-size:42px;color:#d4a25a'>&#9822;</div>"
                    + "<h1 style='font-weight:500;font-size:24px;margin:12px 0 6px'>Vigaasan&rsquo;s Chess Board</h1>"
                    + "<p style='font-style:italic;color:#cdb59c;max-width:320px'>The first launch needs an internet connection. "
                    + "After that the board works offline.</p>"
                    + "<button onclick=\"location.href='" + siteUri + "'\" style='margin-top:14px;padding:12px 22px;background:transparent;"
                    + "border:1px solid #d4a25a;color:#d4a25a;font:600 12px monospace;letter-spacing:.14em'>TRY AGAIN</button></body>";
            view.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        }
    }

    private class FolioChrome extends WebChromeClient {
        // Lets the page's "+ PGN" and "Restore backup" pickers open the
        // system file picker (a plain WebView ignores <input type=file>).
        @Override
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
            if (fileCallback != null) fileCallback.onReceiveValue(null);
            fileCallback = callback;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*"); // .pgn files rarely carry a reliable MIME type
            if (params.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE) i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            try {
                startActivityForResult(Intent.createChooser(i, "Choose a file"), REQ_FILE);
            } catch (ActivityNotFoundException e) {
                fileCallback = null;
                Toast.makeText(MainActivity.this, "No file picker available", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQ_FILE) { super.onActivityResult(requestCode, resultCode, data); return; }
        if (fileCallback == null) return;
        Uri[] result = null;
        if (resultCode == RESULT_OK && data != null) {
            ClipData clip = data.getClipData();
            if (clip != null && clip.getItemCount() > 0) {
                result = new Uri[clip.getItemCount()];
                for (int i = 0; i < clip.getItemCount(); i++) result[i] = clip.getItemAt(i).getUri();
            } else if (data.getData() != null) {
                result = new Uri[]{ data.getData() };
            }
        }
        fileCallback.onReceiveValue(result);
        fileCallback = null;
    }

    /** Exposed to the page as window.VigaasanBridge. */
    private class Bridge {
        @JavascriptInterface
        public void saveBackup(String filename, String text) {
            new Handler(Looper.getMainLooper()).post(() -> writeBackup(filename, text));
        }
    }

    private void writeBackup(String filename, String text) {
        String safeName = filename == null ? "vigaasan-backup.json" : filename.replaceAll("[^A-Za-z0-9._-]", "_");
        byte[] bytes = text == null ? new byte[0] : text.getBytes(StandardCharsets.UTF_8);
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                ContentResolver cr = getContentResolver();
                ContentValues v = new ContentValues();
                v.put(MediaStore.MediaColumns.DISPLAY_NAME, safeName);
                v.put(MediaStore.MediaColumns.MIME_TYPE, "application/json");
                v.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = cr.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v);
                if (uri == null) throw new IllegalStateException("insert failed");
                try (OutputStream os = cr.openOutputStream(uri)) { os.write(bytes); }
            } else {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    pendingBackupName = safeName; pendingBackupText = text;
                    requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQ_STORAGE);
                    return;
                }
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!dir.exists()) dir.mkdirs();
                try (FileOutputStream fos = new FileOutputStream(new File(dir, safeName))) { fos.write(bytes); }
            }
            Toast.makeText(this, "Backup saved to Downloads/" + safeName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Couldn't save the backup: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQ_STORAGE && pendingBackupText != null) {
            String n = pendingBackupName, t = pendingBackupText;
            pendingBackupName = null; pendingBackupText = null;
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) writeBackup(n, t);
            else Toast.makeText(this, "Storage permission is needed to save the backup.", Toast.LENGTH_LONG).show();
        }
    }
}

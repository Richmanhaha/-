package com.quotes.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private WebView web;
    private final ExecutorService pool = Executors.newFixedThreadPool(6);

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        web = new WebView(this);
        setContentView(web);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new Bridge(), "Android");
        web.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        web.evaluateJavascript("window.__back&&window.__back()", v -> {
            if (!"true".equals(v)) finish();
        });
    }

    private class Bridge {
        @JavascriptInterface
        public void get(final String url, final String id) {
            pool.execute(() -> {
                String out;
                try {
                    if (!url.startsWith("https://query1.finance.yahoo.com/")) throw new Exception("blocked");
                    HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                    c.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Mobile Safari/537.36");
                    c.setConnectTimeout(10000);
                    c.setReadTimeout(15000);
                    int code = c.getResponseCode();
                    InputStream in = code < 400 ? c.getInputStream() : c.getErrorStream();
                    ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    byte[] buf = new byte[8192];
                    int n;
                    while (in != null && (n = in.read(buf)) > 0) bo.write(buf, 0, n);
                    out = code < 400 ? bo.toString("UTF-8") : "{\"__err\":" + code + "}";
                } catch (Exception e) {
                    out = "{\"__err\":\"net\"}";
                }
                final String res = out;
                runOnUiThread(() -> web.evaluateJavascript(
                        "window.__cb(" + JSONObject.quote(id) + "," + JSONObject.quote(res) + ")", null));
            });
        }
    }
}

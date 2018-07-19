
package org.communiquons.android.comunic.client.ui.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import org.communiquons.android.comunic.client.BuildConfig;
import org.communiquons.android.comunic.client.R;


/**
 * PDF Activity
 *
 * This activity is used to display remote PDF
 *
 * @author Pierre HUBERT
 */
public class PDFActivity extends AppCompatActivity {

    /**
     * The WebView of the layout
     */
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        //Get and setup WebView
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(false);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(false);
        mWebView.getSettings().setGeolocationEnabled(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Determine and open appropriate URL
        Uri data = getIntent().getData();
        assert data != null;

        String url = BuildConfig.pdf_view_url + data.getQueryParameter("url");
        mWebView.loadUrl(url);
    }
}

package org.communiquons.android.comunic.client.ui.activities;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import org.communiquons.android.comunic.client.R;

/**
 * About activity
 *
 * @author Pierre HUBERT
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView view = findViewById(R.id.webview);
        view.loadUrl("file:///android_asset/about.html");

        //Make ope
        findViewById(R.id.opensource_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOpenSourceLicenses();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    public void openOpenSourceLicenses(){
        Dialog dialog = new Dialog(AboutActivity.this,
                android.R.style.Theme_Holo_Light);

        WebView webView = new WebView(AboutActivity.this);
        webView.loadUrl("file:///android_asset/open_source_licenses.html");
        dialog.setContentView(webView);
        dialog.setTitle(R.string.dialog_open_source_licenses_title);
        dialog.show();
    }
}

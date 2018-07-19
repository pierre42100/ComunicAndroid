
package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.activities.PDFActivity;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * PDF button link widget
 *
 * This button is used to offer the user to open a PDF by clicking on it.
 *
 * @author Pierre HUBERT
 */
public class PDFLinkButtonView extends android.support.v7.widget.AppCompatImageButton implements View.OnClickListener {

    /**
     * The URL of the target PDF
     */
    private String mPDFUrl = null;

    public PDFLinkButtonView(Context context) {
        super(context);
        init();
    }

    public PDFLinkButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PDFLinkButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize the button
     */
    private void init(){

        //Set the drawable
        setImageDrawable(UiUtils.getDrawable(getContext(), R.drawable.file_pdf));

        setOnClickListener(this);
    }

    public String getPDFUrl() {
        return mPDFUrl;
    }

    public boolean hasPDFUrl(){
        return mPDFUrl != null;
    }

    public void setPDFUrl(String PDFUrl) {
        this.mPDFUrl = PDFUrl;
    }

    @Override
    public void onClick(View view) {

        if(!hasPDFUrl())
            return;

        try {
            Intent intent = new Intent(getContext(), PDFActivity.class);
            intent.setData(Uri.parse("?url=" + URLEncoder.encode(getPDFUrl(), "UTF-8")));
            getContext().startActivity(intent);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }
}

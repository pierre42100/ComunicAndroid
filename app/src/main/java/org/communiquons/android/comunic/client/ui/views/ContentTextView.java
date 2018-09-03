package org.communiquons.android.comunic.client.ui.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import org.communiquons.android.comunic.client.data.utils.StringsUtils;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

/**
 * TextView extension which parses the content passed to the
 * view in order to make links live
 *
 * @author Pierre HUBERT
 */
public class ContentTextView extends android.support.v7.widget.AppCompatTextView {

    /**
     * Debug tag
     */
    private static final String TAG = ContentTextView.class.getSimpleName();

    /**
     * The color of detected links
     */
    private int mLinksColor = Color.BLUE;

    public ContentTextView(Context context) {
        this(context, null);
    }

    public ContentTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    protected Activity getActivity(){
        return BaseFrameLayoutView.GetActivityFromContext(getContext());
    }

    /**
     * Set the color of selected links
     *
     * @param res_id The ID of the target color
     */
    public void setLinksColor(int res_id){
        this.mLinksColor = UiUtils.getColor(getContext(), res_id);
    }

    /**
     * Set na new text, with links parsed
     *
     * @param text The text to parse
     */
    public void setParsedText(String text) {
        super.setText(text);

        //Parse text
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);

        String[] parts = text.split("\\s+");
        int pos = 0;
        for (String part : parts) {

            //Process it as URL if possible
            if (StringsUtils.isURL(part)) {

                ClickableSpan clickableSpan = new URLClickableSpan(part);
                ssb.setSpan(clickableSpan, pos, pos + part.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            else if(part.length() > 0 && part.charAt(0) == '@'){
                ClickableSpan clickableSpan = new TAGClickableSpan(part);
                ssb.setSpan(clickableSpan, pos, pos + part.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            pos += part.length() + 1;

        }

        setText(ssb);

    }

    /**
     * Base ClickableSpan class
     */
    private abstract class BaseClickableSpan extends ClickableSpan {

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(mLinksColor);
        }
    }

    /**
     * Clickable span class for URL
     */
    private class URLClickableSpan extends BaseClickableSpan {

        private String mURL;

        private URLClickableSpan(String url){
            super();
            mURL = url;
        }

        @Override
        public void onClick(View widget) {
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mURL)));
        }
    }

    /**
     * Clickable span for tag
     */
    private class TAGClickableSpan extends BaseClickableSpan {

        private String mTag;

        private TAGClickableSpan(String tag){
            mTag = tag.substring(1);
        }

        @Override
        public void onClick(View widget) {
            MainActivity.FollowTag(getActivity(), mTag);
        }
    }
}

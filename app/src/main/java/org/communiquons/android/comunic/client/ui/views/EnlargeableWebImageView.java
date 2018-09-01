package org.communiquons.android.comunic.client.ui.views;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import org.communiquons.android.comunic.client.R;

/**
 * Enlargeable images view.
 *
 * The image represented by this view can be clicked in order to be shown in a bigger way
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/28/18.
 */

public class EnlargeableWebImageView extends WebImageView {

    /**
     * Optional additional OnClick Listener
     */
    private OnClickListener mOnClickListener;

    public EnlargeableWebImageView(Context context) {
        super(context);
        init();
    }

    public EnlargeableWebImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EnlargeableWebImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize ImageView
     */
    private void init(){

        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //Call additional onclick listener if required
                if(mOnClickListener != null)
                    mOnClickListener.onClick(v);

                openLargeImage();
            }
        });

    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    @Override
    public boolean hasOnClickListeners() {
        return mOnClickListener == null;
    }

    /**
     * Open the image in large dimensions
     */
    public void openLargeImage(){

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_image, null);
        WebImageView image = view.findViewById(R.id.image);

        //Apply the image to the dialog (if available)
        if(hasImageURL()){
            image.loadURL(getCurrURL());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .show();


    }
}

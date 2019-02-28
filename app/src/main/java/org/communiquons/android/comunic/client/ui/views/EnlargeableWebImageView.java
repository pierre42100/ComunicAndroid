package org.communiquons.android.comunic.client.ui.views;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.ImageLoadHelper;
import org.communiquons.android.comunic.client.data.utils.ImageLoadUtils;
import org.communiquons.android.comunic.client.ui.utils.BitmapUtils;

import java.io.File;

/**
 * Enlargeable images view.
 *
 * The image represented by this view can be clicked in order to be shown in a bigger way
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/28/18.
 */

public class EnlargeableWebImageView extends WebImageView implements PopupMenu.OnMenuItemClickListener {


    /**
     * Debug tag
     */
    private static final String TAG = EnlargeableWebImageView.class.getSimpleName();

    /**
     * Optional additional OnClick Listener
     */
    private OnClickListener mOnClickListener;

    private boolean mCanSaveImageToGallery;

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

    public boolean isCanSaveImageToGallery() {
        return mCanSaveImageToGallery;
    }

    public void setCanSaveImageToGallery(boolean mCanSaveImageToGallery) {
        this.mCanSaveImageToGallery = mCanSaveImageToGallery;
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


        //Offer the user to save if image if possible
        if(isCanSaveImageToGallery()){
            image.setOnLongClickListener(this::openContextMenu);
        }
    }


    /**
     * Open context menu for the image
     */
    private boolean openContextMenu(View v){

        //Show popup menu
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.image_menu);
        popupMenu.setOnMenuItemClickListener(this);

        if(ImageLoadHelper.IsLoading(this))
            popupMenu.getMenu().findItem(R.id.action_save_image_in_gallery).setEnabled(false);


        popupMenu.show();

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {


        if(item.getItemId() == R.id.action_save_image_in_gallery){

            if(!saveImageToGallery())
                Toast.makeText(getContext(), R.string.err_save_image_in_gallery, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), R.string.success_save_image_in_gallery, Toast.LENGTH_SHORT).show();

            return true;
        }

        return false;
    }

    /**
     * Save the current image in this {@link WebImageView} into the gallery
     *
     * @return TRUE in case of success / FALSE else
     */
    private boolean saveImageToGallery(){

        try {
            if (ImageLoadHelper.IsLoading(this))
                return false;

            File file = ImageLoadUtils.getFileForImage(getContext(), getCurrURL());

            if (!file.isFile()) {
                Log.e(TAG, "Image is not a file!");
                return false;
            }

            Bitmap bm = BitmapUtils.openResized(file, 1500, 1500);

            //Save the image into the gallery
            return MediaStore.Images.Media.insertImage(
                    getContext().getContentResolver(),
                    bm,
                    "Comunic image",
                    getCurrURL()
            ) != null;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

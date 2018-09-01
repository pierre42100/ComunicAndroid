package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;
import android.graphics.Bitmap;

import org.communiquons.android.comunic.client.data.helpers.AccountSettingsHelper;

/**
 * Upload a new account image task
 *
 * @author Pierre HUBERT
 */
public class UploadNewAccountImageTask extends SafeAsyncTask<Bitmap, Void, Boolean> {

    public UploadNewAccountImageTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(Bitmap... bitmaps) {

        return bitmaps.length != 0 &&
                bitmaps[0] != null &&
                new AccountSettingsHelper(getContext()).uploadAccountImage(bitmaps[0]);

    }
}

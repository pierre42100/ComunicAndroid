package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.VirtualDirectoryHelper;
import org.communiquons.android.comunic.client.data.models.VirtualDirectory;

/**
 * Find a virtual directory task
 *
 * @author Pierre HUBERT
 */
public class FindVirtualDirectoryTask extends SafeAsyncTask<String, Void, VirtualDirectory> {
    public FindVirtualDirectoryTask(Context context) {
        super(context);
    }

    @Override
    protected VirtualDirectory doInBackground(String... strings) {
        return new VirtualDirectoryHelper(getContext()).find(strings[0]);
    }
}

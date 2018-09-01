package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.helpers.APIRequestHelper;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;

/**
 * API Request safe async task
 *
 * @author Pierre HUBERT
 */
public class APIRequestTask extends SafeAsyncTask<APIRequest, Void, APIResponse> {

    public APIRequestTask(Context context) {
        super(context);
    }

    @Override
    protected APIResponse doInBackground(APIRequest... apiRequests) {
        try {
            return new APIRequestHelper().exec(apiRequests[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

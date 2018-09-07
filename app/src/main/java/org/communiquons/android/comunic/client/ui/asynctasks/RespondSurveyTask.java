package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.SurveyHelper;

/**
 * Respond to survey task
 *
 * @author Pierre HUBERT
 */
public class RespondSurveyTask extends SafeAsyncTask<Integer, Void, Boolean> {
    public RespondSurveyTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        return new SurveyHelper(getContext()).sendResponse(integers[0], integers[1]);
    }
}

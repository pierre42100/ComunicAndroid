package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.SurveyHelper;

/**
 * Cancel the response to a survey
 *
 * @author Pierre HUBERT
 */
public class CancelSurveyResponseTask extends SafeAsyncTask<Integer, Void, Boolean> {
    public CancelSurveyResponseTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        return new SurveyHelper(getContext()).cancelResponse(integers[0]);
    }
}

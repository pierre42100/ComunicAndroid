package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.helpers.SearchHelper;
import org.communiquons.android.comunic.client.data.models.SearchResult;
import org.communiquons.android.comunic.client.data.models.SearchResultWithInfo;

import java.util.ArrayList;

public class GlobalSearchTask extends SafeAsyncTask<String, Void, ArrayList<SearchResultWithInfo>> {

    public GlobalSearchTask(Context context) {
        super(context);
    }

    @Override
    @Nullable
    protected ArrayList<SearchResultWithInfo> doInBackground(String... strings) {

        SearchHelper helper = new SearchHelper(getContext());

        //First, perform the search itself
        ArrayList<SearchResult> list = helper.global(strings[0]);
        if(list == null)
            return null;

        //Then get information about related users and groups
        return helper.fillResults(list);
    }
}

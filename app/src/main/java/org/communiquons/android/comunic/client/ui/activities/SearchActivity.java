package org.communiquons.android.comunic.client.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.SearchResultWithInfo;
import org.communiquons.android.comunic.client.ui.Constants;
import org.communiquons.android.comunic.client.ui.adapters.SearchResultsAdapter;
import org.communiquons.android.comunic.client.ui.asynctasks.GlobalSearchTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.listeners.OnOpenPageListener;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Search activity
 *
 * This activity can be used to search both users and groups
 *
 * @author Pierre HUBERT
 */
public class SearchActivity extends BaseActivity implements TextWatcher, OnOpenPageListener {

    /**
     * Debug tag
     */
    private static final String TAG = SearchActivity.class.getSimpleName();


    /**
     * Results type
     */
    public static final String INTENT_ARG_RESULT_ID = "id";
    public static final String INTENT_ARG_RESULT_TYPE = "type";
    public static final String INTENT_RESULT_USER = "user";
    public static final String INTENT_RESULT_GROUP = "group";


    /**
     * Adapters
     */
    private SearchResultsAdapter mAdapter;


    /**
     * Views
     */
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Add go back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((EditText)findViewById(R.id.searchInput)).addTextChangedListener(this);

        //Initialize recycler view
        mRecyclerView = findViewById(R.id.resultsRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchResultsAdapter(this ,this);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            //Check if we have to finish activity
            case android.R.id.home:
                finish();
                return true;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        //Check if user did not type at least three characters
        if(s.length() < 3)
            return; //We ignore small researches

        //Perform the search
        getTasksManager().unsetSpecificTasks(GlobalSearchTask.class);
        GlobalSearchTask task = new GlobalSearchTask(this);

        task.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<ArrayList<SearchResultWithInfo>>() {
            @Override
            public void OnPostExecute(@Nullable ArrayList<SearchResultWithInfo> searchResultWithInfoList) {
                onGotNewList(searchResultWithInfoList);
            }
        });

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ""+s);
        getTasksManager().addTask(task);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void onGotNewList(@Nullable ArrayList<SearchResultWithInfo> list){

        //First, check for errors
        if(list == null){
            Toast.makeText(this, R.string.err_get_search_results, Toast.LENGTH_SHORT).show();
            return;
        }

        //Apply search results
        mAdapter.setList(list);
    }

    @Override
    public void onOpenGroup(int groupID) {
        sendResult(groupID, INTENT_RESULT_GROUP);
    }

    @Override
    public void onOpenGroupAccessDenied(int groupID) {
        onOpenGroup(groupID);
    }

    @Override
    public void openUserPage(int userID) {
        sendResult(userID, INTENT_RESULT_USER);
    }

    @Override
    public void openUserAccessDeniedPage(int userID) {
        openUserPage(userID);
    }

    /**
     * This method is called once the user has made a selection
     *
     * @param id The id of the element chosen by the user
     * @param type The type of the element chosen by the user
     */
    private void sendResult(int id, String type){

        //Send result and terminates activity
        Intent intent = new Intent(Constants.IntentResults.SEARCH_GLOBAL_RESULT);
        intent.setData(Uri.parse("?" + INTENT_ARG_RESULT_TYPE+"="+type+"&"+INTENT_ARG_RESULT_ID+"="+id));
        setResult(RESULT_OK, intent);
        finish();

    }
}

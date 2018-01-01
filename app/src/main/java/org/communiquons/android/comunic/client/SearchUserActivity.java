package org.communiquons.android.comunic.client;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.UsersBasicAdapter;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;

import java.util.ArrayList;

public class SearchUserActivity extends AppCompatActivity 
        implements TextWatcher{

    /**
     * Debug tag
     */
    private static final String TAG = "SearchUserActivity";

    /**
     * Search field
     */
    private EditText searchField;

    /**
     * Results list
     */
    private ListView resultListView;

    /**
     * Search result user informations
     */
    private ArrayList<UserInfo> resultArray;

    /**
     * Search results adapter
     */
    private UsersBasicAdapter resultAdapter;

    /**
     * Get user infos helper
     */
    private GetUsersHelper getUsersHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        //Create a get user helper object
        getUsersHelper = new GetUsersHelper(this, DatabaseHelper.getInstance(this));

        //Get view
        searchField = (EditText) findViewById(R.id.activity_search_user_field);
        resultListView = (ListView) findViewById(R.id.activity_search_user_results);

        //Set on key listener
        searchField.addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Do nothing
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        newSearch(""+searchField.getText());
    }

    /**
     * Perform a new search on the server
     *
     * @param query The query to perform on the server
     */
    private void newSearch(String query){

        //Check if there is a query
        if(query.equals(""))
            return; //Cancel the request

        new AsyncTask<String, Void, ArrayMap<Integer, UserInfo>>(){
            @Override
            protected ArrayMap<Integer, UserInfo> doInBackground(String... params) {
                return getUsersHelper.search_users(params[0]);
            }

            @Override
            protected void onPostExecute(ArrayMap<Integer, UserInfo> result) {
                searchCallback(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);

    }

    /**
     * Search callback function
     *
     * @param result The result of the research
     */
    private void searchCallback(@Nullable ArrayMap<Integer, UserInfo> result){

        //Check for errors
        if(result == null){
            Toast.makeText(this, R.string.err_search_user, Toast.LENGTH_SHORT).show();
            return;
        }

        //Save the results
        resultArray = new ArrayList<>(result.values());

        //Display the result
        resultAdapter = new UsersBasicAdapter(this, resultArray);

        //Set the adapter
        resultListView.setAdapter(resultAdapter);
    }
}

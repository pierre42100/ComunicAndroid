package org.communiquons.android.comunic.client.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.ui.adapters.UsersBasicAdapter;
import org.communiquons.android.comunic.client.data.models.UserInfo;

import java.util.ArrayList;

public class SearchUserActivity extends AppCompatActivity 
        implements TextWatcher, AdapterView.OnItemClickListener{

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
        searchField = findViewById(R.id.activity_search_user_field);
        resultListView = findViewById(R.id.activity_search_user_results);

        //Set on key listener
        searchField.addTextChangedListener(this);

        resultListView.setOnItemClickListener(this);
    }

    /**
     * This function is called when we got a response to send
     *
     * @param userID The ID of the user
     */
    private void onGotUserID(int userID){

        Intent data = new Intent("org.communiquons.android.RESULT");
        data.setData(Uri.parse("?userID=" + userID));
        setResult(RESULT_OK, data);
        finish();

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Get ID
        int userID = resultArray.get(position).getId();

        //This is the result
        onGotUserID(userID);
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
                return getUsersHelper.search_users(params[0], 10);
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

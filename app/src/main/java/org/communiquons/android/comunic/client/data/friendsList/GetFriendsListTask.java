package org.communiquons.android.comunic.client.data.friendsList;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * This class handles the asynchronous refresh of the friends list
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/12/17.
 */

public abstract class GetFriendsListTask extends AsyncTask<Void, Void, ArrayList<Friend>> {

    /**
     * The context of the task
     */
    private Context mContext;

    /**
     * Public constructor to the class
     *
     * @param context the context of execution of the refresh task
     */
    public GetFriendsListTask(Context context){
        mContext = context;
    }

    /**
     * What to to once the users list has been refreshed
     *
     * It it the role of each class that use this task class to implement this method
     *
     * @param friendsList The list of friends of the user / null in case of failure
     */
    @Override
    abstract protected void onPostExecute(ArrayList<Friend> friendsList);

    /**
     * Background operation
     *
     * @param params
     * @return The list of friends of the user
     */
    @Override
    protected ArrayList<Friend> doInBackground(Void... params) {
        //Create the GetFriendList object and use it to fetch the list
        return new GetFriendsList(mContext).get();
    }
}

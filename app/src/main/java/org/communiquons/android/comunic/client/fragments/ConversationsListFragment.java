package org.communiquons.android.comunic.client.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.conversations.ConversationsInfo;
import org.communiquons.android.comunic.client.data.conversations.ConversationsListHelper;

import java.util.ArrayList;

/**
 * Conversation list fragment
 *
 * Display all the conversations list
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/6/17.
 */

public class ConversationsListFragment extends Fragment {

    /**
     * Debug tag
     */
    private String TAG = "ConversationsListFrag";

    /**
     * The list of conversations
     */
    private ArrayList<ConversationsInfo> convList;

    /**
     * The conversation list helper
     */
    private ConversationsListHelper conversationsListHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversationslist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Create the conversation list helper
        conversationsListHelper = new ConversationsListHelper(getActivity());

        //Get the list of conversations
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                //Get the list of conversations
                convList = conversationsListHelper.download();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                for(ConversationsInfo conv : convList){
                    Log.v(TAG, "Conversation "+conv.getID()+": " + conv.getName() + " / " +
                            conv.countMembers() + " members / Owner: " + conv.getID_owner());
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }
}

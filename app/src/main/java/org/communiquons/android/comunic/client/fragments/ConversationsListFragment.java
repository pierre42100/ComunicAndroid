package org.communiquons.android.comunic.client.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
        new AsyncTask<Void, Void, ArrayList<ConversationsInfo>>(){
            @Override
            protected ArrayList<ConversationsInfo> doInBackground(Void... params) {

                //Get the list of conversations
                return conversationsListHelper.download();

            }

            @Override
            protected void onPostExecute(ArrayList<ConversationsInfo> list) {
                process_conversations_list(list);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    /**
     * Process the conversation list
     *
     * @param list The list of conversations
     */
    public void process_conversations_list(ArrayList<ConversationsInfo> list){

        //Check if got the list
        if(list == null){
            Toast.makeText(getActivity(), R.string.fragment_conversationslist_err_get_list,
                    Toast.LENGTH_LONG).show();
            return;
        }

        //Process the list of conversation
        ArrayList<Integer> usersToGet = new ArrayList<>();
        ArrayList<ConversationsInfo> convToUpdate = new ArrayList<>();
        for(ConversationsInfo conv : list){

            //Set the displayed names of the conversation
            if(conv.hasName()){
                //Use the name of the conversation if available
                conv.setDisplayName(conv.getName());
            }
            else {

                //Add the first users of the conversations to the users for which we need info
                for(int i = 0; i < 2; i++){

                    if(conv.getMembers().size() <= i)
                        break;

                    usersToGet.add(conv.getMembers().get(i));

                }

                convToUpdate.add(conv);
            }

        }



    }
}

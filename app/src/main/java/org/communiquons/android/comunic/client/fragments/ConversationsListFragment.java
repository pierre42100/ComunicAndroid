package org.communiquons.android.comunic.client.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.conversations.ConversationsInfo;
import org.communiquons.android.comunic.client.data.conversations.ConversationsListAdapter;
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
     * User information helper
     */
    private GetUsersHelper userHelper;

    /**
     * The conversation list helper
     */
    private ConversationsListHelper conversationsListHelper;

    /**
     * Conversations ListView
     */
    private ListView conversationsListView;

    /**
     * Conversation list adapter
     */
    private ConversationsListAdapter conversationsListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversationslist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Database helper
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        //Instantiate the user informations helper
        userHelper = new GetUsersHelper(getActivity(), dbHelper);

        //Create the conversation list helper
        conversationsListHelper = new ConversationsListHelper(getActivity(), dbHelper);

        //Get the conversation target list view
        conversationsListView = view.findViewById(R.id.fragment_conversationslist_list);

        //Get the list of conversations
        new AsyncTask<Void, Void, ArrayList<ConversationsInfo>>(){
            @Override
            protected ArrayList<ConversationsInfo> doInBackground(Void... params) {

                //Get the list of conversations
                ArrayList<ConversationsInfo> list = conversationsListHelper.get();
                process_conversations_list(list);
                return list;

            }

            @Override
            protected void onPostExecute(ArrayList<ConversationsInfo> list) {
                display_conversations_list(list);
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
            return; //Nothing to be done
        }

        //Process the list of conversation
        ArrayList<Integer> usersToGet = new ArrayList<>();
        ArrayList<ConversationsInfo> convToUpdateName = new ArrayList<>();
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

                convToUpdateName.add(conv);
            }
        }

        //Check if we have user to get information about
        if(usersToGet.size() > 0){

            //Get information about the users
            ArrayMap<Integer, UserInfo> usersInfo = userHelper.getMultiple(usersToGet);

            //Check for errors
            if(usersInfo == null){
                Log.e(TAG, "Couldn't get informations about some users !");
                return;
            }

            //Process the conversation that have to be processed
            for(ConversationsInfo conv : convToUpdateName){

                //Get the name of the first members
                String conversationName = "";
                int count = 0;
                for(int userID : conv.getMembers()){

                    if(usersInfo.containsKey(userID)){

                        UserInfo userInfo = usersInfo.get(userID);

                        if(userInfo != null){
                            conversationName += userInfo.getFullName() + ", ";
                            count++;
                        }

                    }

                    if(count == 2)
                        break;
                }

                if(conv.getMembers().size() > 3)
                    conversationName += "...";

                //Update the displayed name of the conversation
                conv.setDisplayName(conversationName);
            }
        }

    }

    /**
     * Display the conversation list
     *
     * @param list The list to display
     */
    public void display_conversations_list(ArrayList<ConversationsInfo> list){

        //Check if we got a list
        if(list == null) {
            Toast.makeText(getActivity(), R.string.fragment_conversationslist_err_get_list,
                    Toast.LENGTH_LONG).show();
            return;
        }

        //Save the list
        convList = list;

        //Create the adapter
        conversationsListAdapter = new ConversationsListAdapter(getActivity(), convList);

        //Attach it to the view
        conversationsListView.setAdapter(conversationsListAdapter);
    }
}

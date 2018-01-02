package org.communiquons.android.comunic.client.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.communiquons.android.comunic.client.MainActivity;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.Account.AccountUtils;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.conversations.ConversationsInfo;
import org.communiquons.android.comunic.client.data.conversations.ConversationsListAdapter;
import org.communiquons.android.comunic.client.data.conversations.ConversationsListHelper;
import org.communiquons.android.comunic.client.data.conversations.ConversationsListHelper.openConversationListener;
import org.communiquons.android.comunic.client.data.conversations.ConversationsListHelper.updateConversationListener;

import java.util.ArrayList;

/**
 * Conversation list fragment
 *
 * Display all the conversations list
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/6/17.
 */

public class ConversationsListFragment extends Fragment implements AdapterView.OnItemClickListener {

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
     * Conversation opener
     */
    private openConversationListener openConvListener;

    /**
     * Conversation updater
     */
    private updateConversationListener updateConversationListener;

    /**
     * Conversation list adapter
     */
    private ConversationsListAdapter conversationsListAdapter;

    /**
     * Loading progress bar
     */
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversationslist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Database helper
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());

        //Instantiate the user informations helper
        userHelper = new GetUsersHelper(getActivity(), dbHelper);

        //Create the conversation list helper
        conversationsListHelper = new ConversationsListHelper(getActivity(), dbHelper);

        //Get the conversation target list view
        conversationsListView = view.findViewById(R.id.fragment_conversationslist_list);

        //Get progress bar wheel
        progressBar = view.findViewById(R.id.fragment_conversationslist_progressbar);

        //Refresh conversations list
        refresh_conversations_list();

        //Set the open and update conversation listener
        try {
            openConvListener = (openConversationListener) getActivity();
            updateConversationListener = (updateConversationListener) getActivity();
        } catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() +
                    " must implement OpenConversationListener and updateConversationListener");
        }

        //Set create conversation button listener
        view.findViewById(R.id.fragment_conversationslist_create)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateConversationListener.createConversation();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //Update activity title
        getActivity().setTitle(R.string.fragment_conversationslist_title);

        //Update the bottom navigation menu
        ((MainActivity) getActivity())
                .setSelectedNavigationItem(R.id.main_bottom_navigation_conversations);
    }

    /**
     * Refresh the list of conversations
     */
    private void refresh_conversations_list(){

        //Display loading wheel
        progressBar.setVisibility(View.VISIBLE);

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
                if(getActivity() != null)
                    display_conversations_list(list);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * Process the conversation list
     *
     * This method must be called on a separate thread
     *
     * @param list The list of conversations
     */
    private void process_conversations_list(ArrayList<ConversationsInfo> list){

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

                    //Do not display current user name
                    if(userID == new AccountUtils(getActivity()).get_current_user_id())
                        continue;

                    if(usersInfo.containsKey(userID)){

                        UserInfo userInfo = usersInfo.get(userID);

                        if(count > 0){
                            conversationName += ", ";
                        }

                        if(userInfo != null){
                            conversationName += userInfo.getFullName();
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
            display_progress_bar(false);
            return;
        }

        //Save the list
        convList = list;

        //Create the adapter
        conversationsListAdapter = new ConversationsListAdapter(getActivity(), convList);

        //Attach it to the view
        conversationsListView.setAdapter(conversationsListAdapter);

        //Add click listener
        conversationsListView.setOnItemClickListener(this);

        conversationsListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.menu_fragment_conversationslist_item, menu);
            }
        });

        //Remove progress bar
        display_progress_bar(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //Fetch source item
        AdapterView.AdapterContextMenuInfo src
                = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //Get conversation ID
        int convID = convList.size() > src.position ? convList.get(src.position).getID() : -1;

        if(convID != -1) {

            //Check which action was chosen
            switch (item.getItemId()) {

                case R.id.menu_fragment_conversationslist_item_delete:
                    confirmDeleteConversation(convID);
                    return true;

            }

        }

        return super.onContextItemSelected(item);
    }

    /**
     * Handles the click on a conversation to open it
     *
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Get the clicked conversation
        ConversationsInfo conv = convList.get(position);

        //Open the specified conversation
        openConvListener.openConversation(conv.getID());

    }

    /**
     * Display (or hide) the progress bar
     *
     * @param show Set wether the progress bar should be shown or not
     */
    private void display_progress_bar(boolean show){
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Display a popup window to ask user to confirm the deletion of a conversation
     *
     * @param convID The ID of the conversation to delete
     */
    private void confirmDeleteConversation(final int convID){

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.popup_deleteconversation_title)
                .setMessage(R.string.popup_deleteconversation_messsage)
                .setNegativeButton(R.string.popup_deleteconversation_cancel, null)

                .setPositiveButton(R.string.popup_deleteconversation_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete_conversation(convID);
                    }
                })

                .show();

    }

    /**
     * Delete a conversation
     *
     * @param convID The ID of the conversation to delete
     */
    private void delete_conversation(final int convID){
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                return conversationsListHelper.delete(convID);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                refresh_conversations_list();

                //Display a toast if an error occurred
                if(!result)
                    Toast.makeText(getActivity(),
                            R.string.fragment_conversationslist_err_del_conversation,
                            Toast.LENGTH_SHORT).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

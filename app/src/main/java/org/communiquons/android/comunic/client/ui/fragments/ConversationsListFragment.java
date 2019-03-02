package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.ConversationsListHelper;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.ConversationInfo;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.adapters.ConversationsListAdapter;
import org.communiquons.android.comunic.client.ui.asynctasks.GetConversationsListTask;
import org.communiquons.android.comunic.client.ui.listeners.openConversationListener;
import org.communiquons.android.comunic.client.ui.listeners.updateConversationListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Conversation list fragment
 *
 * Display all the conversations list
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/6/17.
 */

public class ConversationsListFragment extends AbstractFragment implements AdapterView.OnItemClickListener {

    /**
     * Debug tag
     */
    private static final String TAG = ConversationsListFragment.class.getSimpleName();

    /**
     * The list of conversations
     */
    private ArrayList<ConversationInfo> convList;

    /**
     * User information helper
     */
    private GetUsersHelper userHelper;

    /**
     * The conversation list helper
     */
    private ConversationsListHelper conversationsListHelper;

    /**
     * No conversation notice
     */
    private TextView mNoConversationNotice;

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
    private ProgressBar mProgressBar;

    /**
     * Specify whether we got the first version of the list of conversation
     */
    private boolean mGotFirstList = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversationslist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Database helper
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());

        //Instantiate the user information helper
        userHelper = new GetUsersHelper(Objects.requireNonNull(getActivity()));

        //Create the conversation list helper
        conversationsListHelper = new ConversationsListHelper(getActivity(), dbHelper);

        //Get the conversation target list view
        conversationsListView = view.findViewById(R.id.fragment_conversationslist_list);

        //Get progress bar wheel
        mProgressBar = view.findViewById(R.id.fragment_conversationslist_progressbar);

        //Get no conversation notice
        mNoConversationNotice = view.findViewById(R.id.no_conversation_notice);
        mNoConversationNotice.setVisibility(View.GONE);

        //Refresh conversations list
        refresh_conversations_list(false);

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
                .setOnClickListener(v -> updateConversationListener.createConversation());
    }

    @Override
    public void onResume() {
        super.onResume();

        //Update activity title
        Objects.requireNonNull(getActivity()).setTitle(R.string.fragment_conversationslist_title);
        MainActivity.SetNavbarSelectedOption(getActivity(), R.id.action_conversations);
    }

    /**
     * Refresh the list of conversations
     */
    private void refresh_conversations_list(boolean online){

        //Display loading wheel
        mProgressBar.setVisibility(View.VISIBLE);

        //Get the list of conversations
        GetConversationsListTask getConversationsListTask = new GetConversationsListTask(getActivity());
        getConversationsListTask.setOnPostExecuteListener(this::display_conversations_list);
        getConversationsListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, online);
        getTasksManager().addTask(getConversationsListTask);
    }

    /**
     * Display the conversation list
     *
     * @param list The list to display
     */
    public void display_conversations_list(@Nullable ArrayList<ConversationInfo> list){

        display_progress_bar(false);

        //Check if we were fetching the local list of conversation
        if(!mGotFirstList) {
            mGotFirstList = true;
            refresh_conversations_list(true);
        }

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

        //Add click listener
        conversationsListView.setOnItemClickListener(this);

        conversationsListView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            MenuInflater inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
            inflater.inflate(R.menu.menu_fragment_conversationslist_item, menu);
        });

        //Update the visibility of the no conversation notice
        updateNoConversationNotice();
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

                //To delete the conversation
                case R.id.menu_fragment_conversationslist_item_delete:
                    confirmDeleteConversation(convID);
                    return true;

                //To update the conversation
                case R.id.menu_fragment_conversationslist_item_update:
                    updateConversationListener.updateConversation(convID);
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
        ConversationInfo conv = convList.get(position);

        //Open the specified conversation
        openConvListener.openConversation(conv.getID());

    }

    /**
     * Display (or hide) the progress bar
     *
     * @param show Set wether the progress bar should be shown or not
     */
    private void display_progress_bar(boolean show){
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
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

                .setPositiveButton(R.string.popup_deleteconversation_confirm,
                        (dialog, which) -> delete_conversation(convID))

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

                //Check if the activity has been destroyed
                if(getActivity() == null)
                    return;

                refresh_conversations_list(true);

                //Display a toast if an error occurred
                if(!result)
                    Toast.makeText(getActivity(),
                            R.string.fragment_conversationslist_err_del_conversation,
                            Toast.LENGTH_SHORT).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Update the visibility of the "no conversation" notice message
     */
    private void updateNoConversationNotice(){
        if(convList != null)
            mNoConversationNotice.setVisibility(convList.size() == 0 ? View.VISIBLE : View.GONE);
    }
}

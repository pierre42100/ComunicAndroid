package org.communiquons.android.comunic.client.ui.fragments;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.activities.SearchUserActivity;
import org.communiquons.android.comunic.client.data.utils.AccountUtils;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.ui.adapters.UsersAsysncInfoAdapter;
import org.communiquons.android.comunic.client.data.models.ConversationsInfo;
import org.communiquons.android.comunic.client.data.helpers.ConversationsListHelper;
import org.communiquons.android.comunic.client.ui.listeners.openConversationListener;

import java.util.ArrayList;

/**
 * Create and / or update a conversation fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/1/18.
 */

public class UpdateConversationFragment extends Fragment {

    /**
     * Debug tag
     */
    private static final String TAG = "UpdateConversationFragment";

    /**
     * The conversation ID argument
     */
    public static final String ARG_CONVERSATION_ID = "conversation_id";

    /**
     * Find user ID intent
     */
    public static final int FIND_USER_ID_INTENT = 0;

    /**
     * Action : create a conversation
     */
    private static final int ACTION_CREATE_CONVERSATION = 0;

    /**
     * Action : update a conversation
     */
    private static final int ACTION_UPDATE_CONVERSATION = 1;

    /**
     * Current action of the fragment
     */
    private int current_action = ACTION_CREATE_CONVERSATION;

    /**
     * Target conversation ID
     */
    private int conversation_id = 0;

    /**
     * Specify whether the user is the owner of the conversation or not
     */
    private boolean conversation_owner = true;

    /**
     * The name of the conversation
     */
    private TextView nameView;

    /**
     * Follow a conversation checkbox
     */
    private CheckBox followCheckbox;

    /**
     * Members listview
     */
    private ListView membersList;

    /**
     * Add a member button
     */
    private Button addMember;

    /**
     * Submit the conversation button
     */
    private Button submitButton;

    /**
     * Loading progress bar
     */
    private ProgressBar progressBar;

    /**
     * Users members ID list
     */
    private ArrayList<Integer> membersID = null;

    /**
     * Members informations
     */
    private ArrayMap<Integer, UserInfo> membersInfo = null;

    /**
     * Members list adapter
     */
    private UsersAsysncInfoAdapter membersAdapter;

    /**
     * Users information helper
     */
    private GetUsersHelper usersHelper;

    /**
     * Conversations list helper
     */
    private ConversationsListHelper convListHelper;

    /**
     * Conversation opener
     */
    private openConversationListener convOpener;

    /**
     * Conversation members list context menu
     */
    View.OnCreateContextMenuListener membersListContext = new View.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                ContextMenu.ContextMenuInfo menuInfo) {

            //Create menu
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.menu_fragment_update_conversation_memberslist, menu);

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get database helper instance
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());

        //Get User helper
        usersHelper = new GetUsersHelper(getActivity(), dbHelper);

        //Get conversation list helper
        convListHelper = new ConversationsListHelper(getActivity(), dbHelper);

        //Get conversation opener
        try {
            convOpener = (openConversationListener) getActivity();
        } catch (ClassCastException e){
            throw new RuntimeException(getActivity().getClass().getName() + " must implement the" +
                    " ConversationsListHelper.openConversationListener interface !");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_conversation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get the views
        progressBar = view.findViewById(R.id.progress_bar);
        nameView = view.findViewById(R.id.fragment_update_conversation_name);
        followCheckbox = view.findViewById(R.id.fragment_update_conversation_follow);
        membersList = view.findViewById(R.id.fragment_update_conversation_members);
        addMember = view.findViewById(R.id.fragment_update_conversation_addmember);
        submitButton = view.findViewById(R.id.fragment_update_conversation_submit);

        //Make add button lives
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAddMember();
            }
        });

        //Make submit button lives
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_form();
            }
        });

        //Initialize the form
        init_form();

        //Set members list context menu
        membersList.setOnCreateContextMenuListener(membersListContext);


    }


    @Override
    public void onResume() {
        super.onResume();

        //Update title and dock
        ((MainActivity) getActivity()).setSelectedNavigationItem(
                R.id.action_conversations);

        //Set the adapted title
        if(current_action == ACTION_CREATE_CONVERSATION)
            getActivity().setTitle(R.string.fragment_update_conversation_title_create);
        else
            getActivity().setTitle(R.string.fragment_update_conversation_title_update);
    }

    /**
     * Create and submit an intent to add a user to the members list
     */
    private void requestAddMember(){

        //Make intent
        Intent intent = new Intent(getActivity(), SearchUserActivity.class);
        startActivityForResult(intent, FIND_USER_ID_INTENT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check if it is a success
        if(resultCode == Activity.RESULT_OK){

            switch(requestCode){

                case FIND_USER_ID_INTENT:
                    addMemberID(Integer.decode(data.getData().getQueryParameter("userID")));
            }

        }
    }

    /**
     * Initialize the form
     */
    private void init_form(){

        //Initialize the list of members
        membersID = new ArrayList<>();
        membersInfo = new ArrayMap<>();
        membersAdapter = new UsersAsysncInfoAdapter(getActivity(), membersID, membersInfo);
        membersList.setAdapter(membersAdapter);

        //Check if we have to create or to update a conversation
        conversation_id = getArguments().getInt(ARG_CONVERSATION_ID, 0);

        //Check if we have to create a conversation
        if(conversation_id == 0) {

            //Hide progress bar
            set_progressbar_visibility(false);

            //Set action
            current_action = ACTION_CREATE_CONVERSATION;

            //Update submit button text
            submitButton.setText(R.string.fragment_update_conversation_button_create);
        }

        //Check if we have to update a conversation
        else {

            //Lock the form
            set_form_blocked(true);

            //Set action
            current_action = ACTION_UPDATE_CONVERSATION;

            //Update submit button text
            submitButton.setText(R.string.fragment_update_conversation_button_update);

            //Get informations about the conversation
            new AsyncTask<Integer, Void, ConversationsInfo>(){

                @Override
                protected ConversationsInfo doInBackground(Integer... params) {
                    return convListHelper.getInfosSingle(params[0], true);
                }

                @Override
                protected void onPostExecute(ConversationsInfo conversationsInfo) {
                    onGotConversationInfos(conversationsInfo);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, conversation_id);
        }

    }

    /**
     * This method is called when we received information about a conversation
     *
     * @param infos Informations about a conversation, or null in case of failure
     */
    private void onGotConversationInfos(@Nullable ConversationsInfo infos){

        //Check if the activity has been destroyed
        if(getActivity() == null)
            return;

        //Check for errors
        if(infos == null){
            Toast.makeText(getActivity(), R.string.err_get_conversation_info,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Check if the user is the owner of the conversation or not
        conversation_owner = AccountUtils.getID(getActivity()) == infos.getID_owner();

        //Update the values
        nameView.setText(infos.getName());
        followCheckbox.setChecked(infos.isFollowing());
        membersID.addAll(infos.getMembers());

        //Notify members adapter and refresh users informations
        membersAdapter.notifyDataSetChanged();
        refresh_members_information();

        //Remove progress bar
        set_progressbar_visibility(false);

        //Unlock form fields
        set_form_blocked(false);
    }

    /**
     * Add a member to the list, specified by its ID
     *
     * @param memberID The ID of the member to add
     */
    private void addMemberID(int memberID){

        //Check if the member is already on the list
        if(membersID.contains(memberID)) {
            Toast.makeText(getActivity(), R.string.err_add_member_double, Toast.LENGTH_SHORT).show();
            return;
        }

        //Push the member into the list
        membersID.add(memberID);
        membersAdapter.notifyDataSetChanged();

        //Refresh members information
        refresh_members_information();
    }

    /**
     * Refresh members informations
     *
     * Fetch informations about the users for which we don't know anything yet
     */
    private void refresh_members_information(){

        //Get the list of the required IDs
        final ArrayList<Integer> missingIDs = GetUsersHelper.get_missing_ids(membersID, membersInfo);

        //Continue only if required
        if(missingIDs.size() == 0)
            return; //Do nothing

        //Search informations about them
        new AsyncTask<Void, Void, ArrayMap<Integer, UserInfo>>(){

            @Override
            protected ArrayMap<Integer, UserInfo> doInBackground(Void... params) {
                return usersHelper.getMultiple(missingIDs);
            }

            @Override
            protected void onPostExecute(ArrayMap<Integer, UserInfo> result) {
                append_new_members_info(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * Append new information about members
     *
     * @param usersInfo Information about the members
     */
    private void append_new_members_info(@Nullable ArrayMap<Integer, UserInfo> usersInfo){

        //Check for errors
        if(usersInfo == null){
            Toast.makeText(getActivity(), R.string.err_get_users_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Add the list of user information
        membersInfo.putAll(usersInfo);

        //Notify data set update
        membersAdapter.notifyDataSetChanged();
    }

    /**
     * Handles the context menu actions of the friends list
     *
     * @param item The selected item
     * @return TRUE if the action was handled, false else
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //Get the selected opition
        int option = item.getItemId();

        //Check if we have to remove a member of the conversation
        if(option == R.id.update_conversationmembers_delete){

            //Remove the member from the list
            membersID.remove(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
            membersAdapter.notifyDataSetChanged();

            return true;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Submit creation form
     */
    private void submit_form(){

        //Check there is at least on member to the conversation
        if(membersID.size() == 0){
            Toast.makeText(getActivity(), R.string.err_conversation_need_members,
                    Toast.LENGTH_SHORT).show();
        }

        //Get the values
        final String name = ""+nameView.getText();
        final boolean following = followCheckbox.isChecked();

        //Block the form
        set_form_blocked(true);
        set_progressbar_visibility(true);

        //Create the conversation if required
        if(current_action == ACTION_CREATE_CONVERSATION) {

            //Create the conversation in the background
            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... params) {
                    return convListHelper.create(name, following, membersID);
                }

                @Override
                protected void onPostExecute(Integer integer) {
                    if (getActivity() != null)
                        creationCallback(integer);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        //Update the conversation if required
        if(current_action == ACTION_UPDATE_CONVERSATION){

            new AsyncTask<Void, Void, Boolean>(){
                @Override
                protected Boolean doInBackground(Void... params) {

                    //Check if the current user is the owner of the conversation or not
                    if(conversation_owner)
                        return convListHelper.update(conversation_id, name, membersID, following);
                    else
                        return convListHelper.update(conversation_id, following);
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    updateCallback(result);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }

    /**
     * This method is called once the conversation has been created (or not)
     *
     * @param convID The ID of the target conversation
     */
    private void creationCallback(@Nullable Integer convID){

        //Check for errors
        if(convID == null){
            Toast.makeText(getActivity(), R.string.err_conversation_create,
                    Toast.LENGTH_SHORT).show();

            //Release form
            set_form_blocked(false);
            set_progressbar_visibility(false);

            return;
        }

        //Open conversation
        convOpener.openConversation(convID);
    }

    /**
     * This method is called once the update request on the server has been made
     *
     * @param result The result of the operation
     */
    private void updateCallback(boolean result){

        if(getActivity() == null)
            return;

        //Handle errors
        if(!result){
            Toast.makeText(getActivity(), R.string.err_conversation_update,
                    Toast.LENGTH_SHORT).show();

            //Release form
            set_form_blocked(false);
            set_progressbar_visibility(false);

            return;
        }

        //Open conversation
        convOpener.openConversation(conversation_id);
    }

    /**
     * Update progressbar visibility
     *
     * @param visible TRUE to make the progressbar visible
     */
    private void set_progressbar_visibility(boolean visible){
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Make the fields of the form read only or read and write
     *
     * @param blocked Specify whether the fields should be blocked or not
     */
    private void set_form_blocked(boolean blocked){
        nameView.setEnabled(!blocked && conversation_owner);
        submitButton.setEnabled(!blocked);
        addMember.setEnabled(!blocked && conversation_owner);
        followCheckbox.setEnabled(!blocked);

        membersList.setOnCreateContextMenuListener(
                !blocked && conversation_owner ? membersListContext : null);
    }
}

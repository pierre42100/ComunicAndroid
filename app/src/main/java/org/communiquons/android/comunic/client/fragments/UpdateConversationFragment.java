package org.communiquons.android.comunic.client.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.MainActivity;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.SearchUserActivity;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.UsersInfo.UsersAsysncInfoAdapter;

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
     * Find user ID intent
     */
    public static final int FIND_USER_ID_INTENT = 0;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get database helper instance
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());

        //Get User helper
        usersHelper = new GetUsersHelper(getActivity(), dbHelper);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_conversation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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

        //Initialize the form
        init_form();

    }

    @Override
    public void onResume() {
        super.onResume();

        //Update title and dock
        getActivity().setTitle(R.string.fragment_update_conversation_title_create);
        ((MainActivity) getActivity()).setSelectedNavigationItem(
                R.id.main_bottom_navigation_conversations);
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

        //Hide progress bar
        set_progressbar_visibility(false);

        //Initialize the list of members
        membersID = new ArrayList<>();
        membersInfo = new ArrayMap<>();
        membersAdapter = new UsersAsysncInfoAdapter(getActivity(), membersID, membersInfo);
        membersList.setAdapter(membersAdapter);
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
     * Update progressbar visibility
     *
     * @param visible TRUE to make the progressbar visible
     */
    private void set_progressbar_visibility(boolean visible){
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}

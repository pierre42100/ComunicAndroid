package org.communiquons.android.comunic.client.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.MainActivity;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.SearchUserActivity;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_conversation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get the view
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
            Toast.makeText(getActivity(), "Request success", Toast.LENGTH_SHORT).show();
        }
    }
}

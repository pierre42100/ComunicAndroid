package org.communiquons.android.comunic.client.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.Account.AccountUtils;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.conversations.ConversationMessage;
import org.communiquons.android.comunic.client.data.conversations.ConversationMessageAdapter;
import org.communiquons.android.comunic.client.data.conversations.ConversationMessagesHelper;
import org.communiquons.android.comunic.client.data.conversations.ConversationRefreshRunnable;

import java.util.ArrayList;

/**
 * Conversation fragment
 *
 * Display a conversation, its message. Allow the user to send and receive messages
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/16/17.
 */

public class ConversationFragment extends Fragment
        implements ConversationRefreshRunnable.onMessagesChangeListener {

    /**
     * Debug tag
     */
    private static final String TAG = "ConversationFragment";

    /**
     * The conversation ID argument
     */
    public static final String ARG_CONVERSATION_ID = "conversation_id";

    /**
     * The conversation ID
     */
    private int conversation_id;

    /**
     * The last available message id
     */
    private int last_message_id = 0;

    /**
     * The list of messages of the conversation
     */
    private ArrayList<ConversationMessage> messagesList = new ArrayList<>();

    /**
     * Conversation refresh runnable
     */
    private ConversationRefreshRunnable refreshRunnable;

    /**
     * Conversation messages helper
     */
    private ConversationMessagesHelper convMessHelper;

    /**
     * Conversation messages adapter
     */
    private ConversationMessageAdapter convMessAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Database helper
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        //Set conversation message helper
        convMessHelper = new ConversationMessagesHelper(getActivity(), dbHelper);

        //Get the conversation ID
        conversation_id = getArguments().getInt(ARG_CONVERSATION_ID);

        if(conversation_id < 1){
            throw new RuntimeException(TAG + " requires a valid conversation ID when created !");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Conversation messages listView
        ListView convMessListView = view.findViewById(R.id.fragment_conversation_messageslist);

        int userID = new AccountUtils(getActivity()).get_current_user_id();
        convMessAdapter = new ConversationMessageAdapter(getActivity(), messagesList, userID);
        convMessListView.setAdapter(convMessAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();

        refreshRunnable = new ConversationRefreshRunnable(conversation_id, last_message_id,
                convMessHelper, getActivity(), this);

        //Create and start the thread
        new Thread(refreshRunnable).start();
    }

    @Override
    public void onPause() {
        super.onPause();

        refreshRunnable.quitSafely();
    }

    @Override
    public void onNoMessage() {

    }

    @Override
    public void onAddMessage(int lastID, @NonNull ArrayList<ConversationMessage> newMessages) {

        //Add the messages to the the main list of messages
        for(ConversationMessage message : newMessages){
            messagesList.add(message);
        }

        convMessAdapter.notifyDataSetChanged();

        last_message_id = lastID;
    }

    @Override
    public void onLoadError() {
        //Display a toast
        Toast.makeText(getActivity(), R.string.fragment_conversation_err_load_message,
                Toast.LENGTH_SHORT).show();
    }
}

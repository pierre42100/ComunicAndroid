package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.utils.AccountUtils;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.models.ConversationMessage;
import org.communiquons.android.comunic.client.ui.adapters.ConversationMessageAdapter;
import org.communiquons.android.comunic.client.data.helpers.ConversationMessagesHelper;
import org.communiquons.android.comunic.client.data.runnables.ConversationRefreshRunnable;
import org.communiquons.android.comunic.client.data.models.ConversationsInfo;
import org.communiquons.android.comunic.client.data.helpers.ConversationsListHelper;
import org.communiquons.android.comunic.client.ui.utils.BitmapUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

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
     * Pick image request number
     */
    public static final int PICK_PHOTO = 1;

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
     * Information about the conversation
     */
    private ConversationsInfo conversationInfo = null;

    /**
     * The last available message id
     */
    private int last_message_id = 0;

    /**
     * The list of messages of the conversation
     */
    private ArrayList<ConversationMessage> messagesList = new ArrayList<>();

    /**
     * Informations about the users of the conversation
     */
    private ArrayMap<Integer, UserInfo> users = new ArrayMap<>();

    /**
     * Conversation refresh runnable
     */
    private ConversationRefreshRunnable refreshRunnable;

    /**
     * Fragment main progress bar
     */
    private ProgressBar main_progress_bar;

    /**
     * No message yet notice
     */
    private TextView no_msg_notice;

    /**
     * Converstion message listView
     */
    private ListView convMessListView;

    /**
     * Conversation messages helper
     */
    private ConversationMessagesHelper convMessHelper;

    /**
     * Conversation list helper
     */
    private ConversationsListHelper convListHelper;

    /**
     * Conversation messages adapter
     */
    private ConversationMessageAdapter convMessAdapter;

    /**
     * Conversation new message content
     */
    private EditText new_message_content;

    /**
     * Conversation new message send button
     */
    private ImageButton send_button;

    /**
     * Conversation new message progress bar
     */
    private ProgressBar new_message_progress_bar;

    /**
     * Conversation add image button
     */
    private ImageButton pick_image_button;

    /**
     * New message selected image
     */
    private Bitmap new_message_bitmap = null;

    /**
     * Get user helper
     */
    private GetUsersHelper getUsersHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Database helper
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());

        //Set conversation message helper
        convMessHelper = new ConversationMessagesHelper(getActivity(), dbHelper);

        //Set conversation list helper
        convListHelper = new ConversationsListHelper(getActivity(), dbHelper);

        //Get the conversation ID
        conversation_id = getArguments().getInt(ARG_CONVERSATION_ID);

        //Get user helper
        getUsersHelper = new GetUsersHelper(getActivity(), dbHelper);

        if(conversation_id < 1){
            throw new RuntimeException(TAG + " requires a valid conversation ID when created !");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            //If the response if for the photos
            case PICK_PHOTO :
                pick_image_callback(resultCode, data);
                break;
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

        //Main progress bar
        main_progress_bar = view.findViewById(R.id.fragment_conversation_progressbar);
        display_main_progress_bar(true);

        //No message notice
        no_msg_notice = view.findViewById(R.id.fragment_conversation_noMsgYet);
        display_not_msg_notice(false);

        //Conversation messages listView
        convMessListView = view.findViewById(R.id.fragment_conversation_messageslist);

        //Need user ID
        int userID = new AccountUtils(getActivity()).get_current_user_id();

        //Create the adapter
        convMessAdapter = new ConversationMessageAdapter(getActivity(),
                messagesList, userID, users);

        //Apply adapter
        convMessListView.setAdapter(convMessAdapter);

        //Get new messages input fields
        new_message_content = view.findViewById(R.id.fragment_conversation_newmessage_content);
        pick_image_button = view.findViewById(R.id.fragment_conversation_newmessage_pickimage);
        send_button = view.findViewById(R.id.fragment_conversation_newmessage_send);
        new_message_progress_bar = view.findViewById(R.id.fragment_conversation_newmessage_loading);

        //Make send button lives
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_message();
            }
        });

        //Make pick image button lives
        pick_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_image();
            }
        });
        pick_image_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                confirm_remove_picked_image();
                return true;
            }
        });

        //Hide new message sending wheel
        new_message_progress_bar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshRunnable = new ConversationRefreshRunnable(conversation_id, last_message_id,
                convMessHelper, getActivity(), this);

        //Create and start the thread
        new Thread(refreshRunnable).start();

        //Update conversation title
        getActivity().setTitle(R.string.fragment_conversation_title);

        //Update the bottom navigation menu
        ((MainActivity) getActivity())
                .setSelectedNavigationItem(R.id.main_bottom_navigation_conversations);

        //Check for conversation information
        if(conversationInfo == null){

            //Query information about the conversation
            new AsyncTask<Void, Void, ConversationsInfo>(){
                @Override
                protected ConversationsInfo doInBackground(Void... params) {
                    ConversationsInfo infos = convListHelper.getInfosSingle(conversation_id, true);
                    if(infos != null)
                        infos.setDisplayName(convListHelper.getDisplayName(infos));
                    return infos;
                }

                @Override
                protected void onPostExecute(ConversationsInfo conversationsInfo) {
                    onGotConversationInfos(conversationsInfo);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
            onGotConversationInfos(conversationInfo);
    }

    @Override
    public void onPause() {
        super.onPause();

        refreshRunnable.quitSafely();
    }

    @Override
    public void onNoMessage() {

        //Hide main progress bar
        display_main_progress_bar(false);

        //Display no message notice
        display_not_msg_notice(true);

    }

    @Override
    public void onAddMessage(int lastID, @NonNull ArrayList<ConversationMessage> newMessages) {

        //Remove main progress bar and no message notice
        display_main_progress_bar(false);
        display_not_msg_notice(false);

        final ArrayList<Integer> usersToFetch = new ArrayList<>();

        //Add the messages to the the main list of messages
        for(ConversationMessage message : newMessages){
            messagesList.add(message);

            if(!users.containsKey(message.getUser_id()))
                usersToFetch.add(message.getUser_id());
        }

        convMessAdapter.notifyDataSetChanged();
        last_message_id = lastID;

        //Fetch user information if required
        if(usersToFetch.size() > 0){
            new AsyncTask<Void, Void, ArrayMap<Integer, UserInfo>>(){
                @Override
                protected ArrayMap<Integer, UserInfo> doInBackground(Void... params) {
                    //Get the users list
                    return getUsersHelper.getMultiple(usersToFetch);
                }

                @Override
                protected void onPostExecute(ArrayMap<Integer, UserInfo> usersInfo) {
                    onGotUserInfo(usersInfo);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onLoadError() {
        //Display a toast
        Toast.makeText(getActivity(), R.string.fragment_conversation_err_load_message,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is called when we get informations about users
     *
     * @param info Informations about the user
     */
    public void onGotUserInfo(@Nullable ArrayMap<Integer, UserInfo> info ){

        //Check for errors
        if(info == null){
            //This is a failure
            return;
        }

        //Process the list of users
        for(UserInfo user : info.values()){
            if(user != null){
                users.put(user.getId(), user);
            }
        }

        //Inform about dataset update
        convMessAdapter.notifyDataSetChanged();
    }

    /**
     * What to do once we got conversation informations
     *
     * @param infos Informations about the conversation
     */
    private void onGotConversationInfos(ConversationsInfo infos){

        //Check for errors
        if(infos == null){
            Toast.makeText(getActivity(), R.string.fragment_conversation_err_getconvinfos,
                    Toast.LENGTH_SHORT).show();
            return;
        }


        //Save conversation informations
        conversationInfo = infos;

        //Update the name of the conversation
        getActivity().setTitle(infos.getDisplayName());

    }

    /**
     * This method is called when the user request to add an image to a message
     */
    private void pick_image(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_PHOTO);
    }

    /**
     * This method is called to confirm to remove a previously picked image
     */
    private void confirm_remove_picked_image(){
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.conversation_message_remove_image_popup_title)
                .setMessage(R.string.conversation_message_remove_image_popup_message)

                .setNegativeButton(R.string.conversation_message_remove_image_popup_cancel, null)

                .setPositiveButton(R.string.conversation_message_remove_image_popup_confirm,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove_picked_image();
                    }
                })

                .show();
    }

    /**
     * Handles image picker callback
     *
     * @param resultCode The result code of the request
     * @param data The data passed with the intent
     */
    private void pick_image_callback(int resultCode, Intent data){
        //Continue in case of success
        if(resultCode == RESULT_OK){
            try {

                Uri imageUri = data.getData();
                InputStream imageStream = getActivity().getContentResolver()
                        .openInputStream(imageUri);
                new_message_bitmap = BitmapFactory.decodeStream(imageStream);

                //Append image
                pick_image_button.setImageBitmap(new_message_bitmap);


            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove a previously picked image in the new message form
     */
    private void remove_picked_image(){

        //Clean bitmap
        if(new_message_bitmap != null){
            new_message_bitmap.recycle();
            new_message_bitmap = null;
        }

        //Reset image button
        pick_image_button.setImageBitmap(null);
        pick_image_button.setImageDrawable(getActivity().getResources().
                getDrawable(android.R.drawable.ic_menu_gallery, getActivity().getTheme()));
    }

    /**
     * This method is called when the user click on the "send_message" button
     */
    private void send_message(){

        //Check message length
        if(new_message_content.length() < 3 && new_message_bitmap == null){
            Toast.makeText(getActivity(), R.string.conversation_message_err_too_short,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Hide the send button
        send_button.setVisibility(View.GONE);
        new_message_progress_bar.setVisibility(View.VISIBLE);

        //Get the message content
        final String message_content = new_message_content.getText()+"";

        //Send the message
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                String message_image = null;

                //Reduce Bitmap and convert it to a base64-encoded string
                if(new_message_bitmap != null)
                        message_image = BitmapUtils.bitmapToBase64(
                                BitmapUtils.reduceBitmap(new_message_bitmap, 1199, 1199));

                return convMessHelper.sendMessage(conversation_id, message_content, message_image);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                send_callback(success);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * This method is called in response to a post message request
     *
     * @param success Specify wether the message was successfully posted or not
     */
    private void send_callback(boolean success){

        //Check for error
        if(!success){
            Toast.makeText(getActivity(), R.string.conversation_message_err_send,
                    Toast.LENGTH_SHORT).show();
        }

        //Remove previous message in case of success
        if(success){
            new_message_content.setText("");

            //Remove image
            remove_picked_image();
        }

        //Make the "send" button available again
        send_button.setVisibility(View.VISIBLE);
        new_message_progress_bar.setVisibility(View.GONE);

    }

    /**
     * Update the visibility status of the main progress bar of the fragment
     *
     * @param visible True to make the progress bar visible
     */
    private void display_main_progress_bar(boolean visible){
        main_progress_bar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Update the visibility status of the "no message notice"
     *
     * @param visible True to make the progress bar visible
     */
    private void display_not_msg_notice(boolean visible){
        no_msg_notice.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}

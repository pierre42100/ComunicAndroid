package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.ConversationMessagesList;
import org.communiquons.android.comunic.client.data.models.NewConversationMessage;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.helpers.ConversationMessagesHelper;
import org.communiquons.android.comunic.client.data.helpers.ConversationsListHelper;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.ConversationMessage;
import org.communiquons.android.comunic.client.data.models.ConversationsInfo;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.runnables.ConversationRefreshRunnable;
import org.communiquons.android.comunic.client.data.utils.AccountUtils;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.adapters.ConversationMessageAdapter;
import org.communiquons.android.comunic.client.ui.asynctasks.DeleteConversationMessageTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SendConversationMessageTask;
import org.communiquons.android.comunic.client.ui.asynctasks.UpdateConversationMessageContentTask;
import org.communiquons.android.comunic.client.ui.listeners.OnConversationMessageActionsListener;
import org.communiquons.android.comunic.client.ui.listeners.OnScrollChangeDetectListener;
import org.communiquons.android.comunic.client.ui.utils.BitmapUtils;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.android.comunic.client.ui.views.AppBarLayout;
import org.communiquons.android.comunic.client.ui.views.ScrollRecyclerView;

import java.io.FileNotFoundException;
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
        implements ConversationRefreshRunnable.onMessagesChangeListener,
        OnScrollChangeDetectListener, OnConversationMessageActionsListener,
        PopupMenu.OnMenuItemClickListener {

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
     * Current user ID
     */
    private int userID;

    /**
     * The list of messages of the conversation
     */
    private ConversationMessagesList messagesList = new ConversationMessagesList();

    /**
     * Conversation refresh runnable
     */
    private ConversationRefreshRunnable refreshRunnable;

    /**
     * Fragment toolbar
     */
    private AppBarLayout mAppBar;

    /**
     * Fragment main progress bar
     */
    private ProgressBar main_progress_bar;

    /**
     * No message yet notice
     */
    private TextView no_msg_notice;

    /**
     * Conversation message listView
     */
    private ScrollRecyclerView convMessRecyclerView;

    /**
     * Conversation messages layout manager
     */
    private LinearLayoutManager mLinearLayoutManager;

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

    /**
     * Async task used to fetch older messages
     */
    private AsyncTask mGetOlderMessagesTask;

    /**
     * Current conversation message in context menu
     */
    private int mMessageInContextMenu;

    /**
     * Safe AsyncTasks
     */
    private SendConversationMessageTask mSendConversationMessageTask;
    private DeleteConversationMessageTask mDeleteMessageAsyncTask;
    private UpdateConversationMessageContentTask mUpdateConversationMessageContentTask;

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
        assert getArguments() != null;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Main progress bar
        main_progress_bar = view.findViewById(R.id.fragment_conversation_progressbar);
        display_main_progress_bar(true);

        //No message notice
        no_msg_notice = view.findViewById(R.id.fragment_conversation_noMsgYet);
        display_not_msg_notice(false);

        //Get views
        convMessRecyclerView = view.findViewById(R.id.fragment_conversation_messageslist);
        mAppBar = view.findViewById(R.id.appbar);

        //Need user ID
        userID = AccountUtils.getID(getActivity());

        //Initialize toolbar
        mAppBar.addBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.goBackward(getActivity());
            }
        });

        mAppBar.addButton(R.drawable.ic_settings, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).updateConversation(conversation_id);
            }
        });


        //Create the adapter
        convMessAdapter = new ConversationMessageAdapter(getActivity(),
                messagesList, userID);
        convMessAdapter.setOnConversationMessageActionsListener(this);

        //Apply adapter
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        convMessRecyclerView.setLayoutManager(mLinearLayoutManager);
        convMessRecyclerView.setAdapter(convMessAdapter);
        mLinearLayoutManager.setStackFromEnd(true);


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

        //Make message input act like send button on enter key press
        new_message_content.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    send_message();
                    return false;
                }


                return false;
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

        //Set a listener to detect when the user reaches the top of the conversation
        convMessRecyclerView.setOnScrollChangeDetectListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        refreshRunnable = new ConversationRefreshRunnable(conversation_id, last_message_id,
                convMessHelper, getActivity(), this);

        //Create and start the thread
        new Thread(refreshRunnable).start();

        //Update conversation title
        setTitle(UiUtils.getString(getActivity(), R.string.fragment_conversation_title));
        MainActivity.SetNavbarSelectedOption(getActivity(), R.id.action_conversations);


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
                    onGotConversationInfo(conversationsInfo);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
            onGotConversationInfo(conversationInfo);

        //Remove progress if we already have messages
        if(messagesList != null)
            if(messagesList.size() > 0)
                display_main_progress_bar(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        refreshRunnable.quitSafely();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unsetSendMessageTask();
        unsetPendingDeleteTasksCallback();
        unsetPendingUpdatedTasksCallbacks();
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

        //Add the messages to the the main list of messages
        messagesList.addAll(newMessages);

        convMessAdapter.notifyDataSetChanged();
        last_message_id = lastID;

        //Make sure we have got information about all the members of the conversation
        refreshUserInfo();

        //Scroll to bottom
        convMessRecyclerView.scrollToPosition(messagesList.size() - 1);
    }

    @Override
    public void onLoadError() {

        if(getActivity() == null)
            return;

        //Display a toast
        Toast.makeText(getActivity(), R.string.fragment_conversation_err_load_message,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Make sure we have got the information about all the users of the conversation
     */
    private void refreshUserInfo(){

        if(messagesList == null)
            return;

        final ArrayList<Integer> usersToFetch = messagesList.getMissingUsersList();


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

    /**
     * This method is called when we get information about users
     *
     * @param info Information about the user
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
                messagesList.getUsersInfo().put(user.getId(), user);
            }
        }

        //Inform about data set update
        convMessAdapter.notifyDataSetChanged();
    }

    /**
     * What to do once we got conversation information
     *
     * @param info Information about the conversation
     */
    private void onGotConversationInfo(ConversationsInfo info){

        //Check for errors
        if(info == null){
            Toast.makeText(getActivity(), R.string.fragment_conversation_err_getconvinfos,
                    Toast.LENGTH_SHORT).show();
            return;
        }


        //Save conversation information
        conversationInfo = info;

        //Update the name of the conversation
        setTitle(info.getDisplayName());

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

                //Get new message bitmap
                new_message_bitmap = BitmapUtils.IntentResultToBitmap(getActivity(), data);

                //Check for errors
                if(new_message_bitmap == null){
                    remove_picked_image();
                    return;
                }

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
        pick_image_button.setImageDrawable(UiUtils.getDrawable(getActivity(),
                android.R.drawable.ic_menu_gallery));
    }

    /**
     * This method is called when the user click on the "send_message" button
     */
    private void send_message(){

        //Check message length
        if(new_message_content.length() < 2 && new_message_bitmap == null){
            Toast.makeText(getActivity(), R.string.conversation_message_err_too_short,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Hide the send button
        send_button.setVisibility(View.GONE);
        new_message_progress_bar.setVisibility(View.VISIBLE);

        //Get the message content
        String message_content = new_message_content.getText()+"";

        if(message_content.length() < 3 && new_message_bitmap == null)
            message_content += " ";

        NewConversationMessage newMessage = new NewConversationMessage();
        newMessage.setConversationID(conversation_id);
        newMessage.setMessage(message_content);
        newMessage.setImage(new_message_bitmap);

        //Send the message
        unsetSendMessageTask();
        mSendConversationMessageTask = new SendConversationMessageTask(getActivity());
        mSendConversationMessageTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<Boolean>() {
            @Override
            public void OnPostExecute(Boolean success) {
                send_callback(success);
            }
        });
        mSendConversationMessageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newMessage);
    }

    private void unsetSendMessageTask(){
        if(mSendConversationMessageTask != null)
            mSendConversationMessageTask.setOnPostExecuteListener(null);
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

    /**
     * Update the title of the fragment
     *
     * @param title New title
     */
    private void setTitle(String title){
        if(getActivity() == null)
            return;

        getActivity().setTitle(title);
        mAppBar.setTitle(title);
    }

    /**
     * This method is called when the user reach the top of the conversation
     */
    @Override
    public void onReachTop() {

        //Check if we have got other messages to get
        if(messagesList == null)
            return;
        if(messagesList.size() == 0)
            return;
        final int firstMessageID = messagesList.get(0).getId();

        //Check if another task is running
        if(mGetOlderMessagesTask != null)
            return;

        //Display progress bar
        display_main_progress_bar(true);

        //Create and execute the task
        mGetOlderMessagesTask = new AsyncTask<Void, Void, ArrayList<ConversationMessage>>(){

            @Override
            protected ArrayList<ConversationMessage> doInBackground(Void... params) {
                return convMessHelper.getOlderMessages(conversation_id, firstMessageID);
            }

            @Override
            protected void onPostExecute(ArrayList<ConversationMessage> conversationMessages) {
                if(getActivity() == null)
                    return;

                onGotOlderMessages(conversationMessages);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onReachBottom() {

    }

    /**
     * Actions to do once we downloaded older messages from the server
     *
     * @param list The list of messages that was downloaded
     */
    private void onGotOlderMessages(@Nullable ArrayList<ConversationMessage> list){

        //Remove link over task
        mGetOlderMessagesTask = null;

        //Remove progress bar
        display_main_progress_bar(false);

        //Check if the list is null (in case of error)
        if(list == null) {
            Toast.makeText(getActivity(), R.string.err_get_older_conversation_messages,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(list.size() == 0)
            return;
        Log.v(TAG, "List size: " + list.size());

        //Add the messages to the list
        messagesList.addAll(0, list);

        //Notify adapter
        convMessAdapter.notifyDataSetChanged();

        //Refresh user information if required
        refreshUserInfo();
    }

    @Override
    public void onOpenContextMenu(int pos, View v) {

        mMessageInContextMenu = pos;
        ConversationMessage message = messagesList.get(pos);

        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.inflate(R.menu.menu_conversation_message);

        if(message.getUser_id() != userID) {
            popup.getMenu().findItem(R.id.action_delete).setEnabled(false);
            popup.getMenu().findItem(R.id.action_update_content).setEnabled(false);
        }

        popup.setOnMenuItemClickListener(this);

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        if(item.getItemId() == R.id.action_delete){
            onConfirmDeleteConversationMessage(mMessageInContextMenu);
            return true;
        }

        if(item.getItemId() == R.id.action_update_content){
            onRequestConversationMessageUpdate(mMessageInContextMenu);
            return true;
        }

        return false;
    }


    @Override
    public void onConfirmDeleteConversationMessage(final int pos) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_delete_conversation_message_title)
                .setMessage(R.string.dialog_delete_conversation_message_message)
                .setNegativeButton(R.string.dialog_delete_conversation_message_cancel, null)

                .setPositiveButton(R.string.dialog_delete_conversation_message_confirm,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteConversationMessage(pos);
                    }
                })

                .show();
    }

    @Override
    public void onRequestConversationMessageUpdate(final int pos) {

        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit_conversation_message, null);
        final EditText input = view.findViewById(R.id.input);
        input.setText(messagesList.get(pos).getContent());

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_edit_conversation_message_content_title)
                .setView(view)
                .setNegativeButton(R.string.dialog_edit_conversation_message_content_cancel, null)
                .setPositiveButton(R.string.dialog_edit_conversation_message_content_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        update_conversation_message(pos, ""+input.getText());
                    }
                }).show();

    }



    /**
     * Delete conversation message at a specified position
     *
     * @param pos The position of the message to delete
     */
    private void deleteConversationMessage(final int pos){
        unsetPendingDeleteTasksCallback();

        mDeleteMessageAsyncTask = new DeleteConversationMessageTask(getActivity());
        mDeleteMessageAsyncTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<Boolean>() {
            @Override
            public void OnPostExecute(Boolean result) {
                deleteConversationMessagesCallback(pos, result);
            }
        });


        mDeleteMessageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                messagesList.get(pos).getId());
    }

    /**
     * Unset any conversation message delete pending tasks
     */
    private void unsetPendingDeleteTasksCallback(){
        if(mDeleteMessageAsyncTask != null)
            mDeleteMessageAsyncTask.setOnPostExecuteListener(null);
    }

    /**
     * Delete conversation message callback
     *
     * @param pos The position of the message
     * @param result Result of the operation
     */
    private void deleteConversationMessagesCallback(int pos, boolean result){

        if(getActivity() == null)
           return;

        if(!result) {
            Toast.makeText(getActivity(), R.string.err_delete_conversation_message,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        messagesList.remove(pos);
        convMessAdapter.notifyDataSetChanged();
    }


    /**
     * Perform the update of the content of a conversation message
     *
     * @param pos The position of the message to update
     * @param content The new content of the message
     */
    private void update_conversation_message(int pos, String content){

        if(content.length() < 3){
            Toast.makeText(getActivity(), R.string.err_invalid_conversation_message_content,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        unsetPendingUpdatedTasksCallbacks();

        ConversationMessage message = messagesList.get(pos);
        message.setContent(content);

        mUpdateConversationMessageContentTask = new UpdateConversationMessageContentTask(getActivity());
        mUpdateConversationMessageContentTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<Boolean>() {
            @Override
            public void OnPostExecute(Boolean aBoolean) {
                if(getActivity() != null)
                    updateConversationMessageCallback(aBoolean);
            }
        });
        mUpdateConversationMessageContentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
    }

    /**
     * Unset any conversation message update pending tasks
     */
    private void unsetPendingUpdatedTasksCallbacks(){
        if(mUpdateConversationMessageContentTask != null)
            mUpdateConversationMessageContentTask.setOnPostExecuteListener(null);
    }

    /**
     * Update Conversation message callback
     *
     * @param result TRUE in case of result / FALSE else
     */
    private void updateConversationMessageCallback(boolean result){

        //Check for errors
        Toast.makeText(getActivity(),
                !result ? R.string.err_update_conversation_message_content :
                        R.string.success_update_conversation_message_content,
                Toast.LENGTH_SHORT).show();

        convMessAdapter.notifyDataSetChanged();
    }
}

package org.communiquons.android.comunic.client.data.conversations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.ImageLoad.ImageLoadManager;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;

import java.util.ArrayList;

/**
 * Conversation messages adapter
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/18/17.
 */

public class ConversationMessageAdapter extends ArrayAdapter<ConversationMessage> {

    /**
     * Debug tag
     */
    private static final String TAG = "ConversationMessageAdap";

    /**
     * The ID of the current user
     */
    private int userID;

    /**
     * Information about users
     */
    private ArrayMap<Integer, UserInfo> usersInfo;

    /**
     * Public class constructor
     *
     * @param context The context of execution of the application
     * @param list The dataset
     * @param userID The ID of the current user
     */
    public ConversationMessageAdapter(Context context, ArrayList<ConversationMessage> list,
                                      int userID, ArrayMap<Integer, UserInfo> usersInfo){
        super(context, 0, list);

        //Set user ID
        this.userID = userID;

        //Set user information list
        this.usersInfo = usersInfo;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Inflate view if required
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).
                inflate(R.layout.fragment_conversation_message_item, parent, false);


        //Get the content of the message
        ConversationMessage message = getItem(position);
        assert message != null;

        //Get the previous message
        ConversationMessage previousMessage = null;
        if(position > 0){
            previousMessage = getItem(position-1);
        }

        /*
            Get the view of the messages

            Update the general layout of the message
         */
        TextView contentView;
        ImageView messageImageView;
        ImageView accountImageView;
        TextView userNameView;
        if(message.getUser_id() == userID){

            //Message appears on the right
            convertView.findViewById(R.id.fragment_conversation_message_left).
                    setVisibility(View.GONE);
            convertView.findViewById(R.id.fragment_conversation_message_right).
                    setVisibility(View.VISIBLE);

            contentView = convertView.
                    findViewById(R.id.fragment_conversation_message_item_content_right);

            messageImageView = convertView.
                    findViewById(R.id.fragment_conversation_message_item_messageimage_right);

            accountImageView = convertView.
                    findViewById(R.id.fragment_conversation_message_item_accountimage_right);


            userNameView = null;
        }
        else {

            //Message appears on the left
            convertView.findViewById(R.id.fragment_conversation_message_right).
                    setVisibility(View.GONE);
            convertView.findViewById(R.id.fragment_conversation_message_left).
                    setVisibility(View.VISIBLE);

            contentView = convertView.
                    findViewById(R.id.fragment_conversation_message_item_content);

            messageImageView = convertView.
                    findViewById(R.id.fragment_conversation_message_item_messageimage);

            accountImageView = convertView.
                    findViewById(R.id.fragment_conversation_message_item_accountimage);

            userNameView = convertView.findViewById(R.id.fragment_conversation_message_item_username);
        }

        /*
            Check for user information
         */
        UserInfo user = null;
        if(usersInfo.containsKey(message.getUser_id())){
            user = usersInfo.get(message.getUser_id());
        }


        /*
            Update message content
         */
        //Set the text of the message
        contentView.setText(message.getContent());


        /*
            Update message image
         */
        if(message.hasImage()){
            //Load the image
            ImageLoadManager.remove(messageImageView);
            ImageLoadManager.load(getContext(), message.getImage_path(), messageImageView);

            //Make the image visible
            messageImageView.setVisibility(View.VISIBLE);
        }
        else {
            messageImageView.setVisibility(View.GONE);
        }

        /*
            Update user name
         */
        if(userNameView != null){

            if(user != null){
                //Set the name of the user
                userNameView.setText(user.getFullName());
                userNameView.setVisibility(View.VISIBLE);
            }
            else
                userNameView.setVisibility(View.GONE);

            if(previousMessage != null){
                if (message.getUser_id() == previousMessage.getUser_id()){
                    userNameView.setVisibility(View.GONE);
                }
            }

        }

        /*
            Update account image
         */
        //Cancel any load pending operation
        ImageLoadManager.remove(accountImageView);

        //Set the default image
        accountImageView.setImageResource(R.drawable.default_account_image);
        accountImageView.setVisibility(View.VISIBLE);

        //Check if we can load a specific image
        if(user != null) {
            String imageURL = user.getAcountImageURL();
            ImageLoadManager.load(getContext(), imageURL, accountImageView);
        }

        //Hide user image if not required
        if(previousMessage != null){
            if (message.getUser_id() == previousMessage.getUser_id()){
                accountImageView.setVisibility(View.INVISIBLE);
            }
        }


        return convertView;
    }
}

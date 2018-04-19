package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.ImageLoadHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.models.ConversationMessage;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

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
        //Get the views
        LinearLayout containerView = convertView
                .findViewById(R.id.fragment_conversation_message_item_container);
        TextView contentView = convertView.
                findViewById(R.id.fragment_conversation_message_item_content);
        ImageView messageImageView = convertView.
                findViewById(R.id.fragment_conversation_message_item_messageimage);
        WebUserAccountImage accountImageView;
        TextView userNameView = convertView.
                findViewById(R.id.fragment_conversation_message_item_username);

        //Adapt the layout depending of user of the message
        if(message.getUser_id() == userID){

            //Message appears on the right
            ((LinearLayout)convertView).setGravity(Gravity.END);

            //Message appears in blue
            containerView.setBackground(getContext().
                    getDrawable(R.drawable.fragment_conversation_message_currentuser_bg));

            //User account image appears on the right
            accountImageView = convertView.
                    findViewById(R.id.fragment_conversation_message_item_right_account_image);
            accountImageView.setVisibility(View.VISIBLE);

            //Hide left image
            convertView
                    .findViewById(R.id.fragment_conversation_message_item_left_account_image)
                    .setVisibility(View.GONE);

            //Align text on the right
            contentView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        }
        else {



            //Message appears on the right
            ((LinearLayout)convertView).setGravity(Gravity.START);

            //Message appears in blue
            containerView.setBackground(getContext().
                    getDrawable(R.drawable.fragment_conversation_message_otheruser_bg));

            //User account image appears on the left
            accountImageView = convertView.
                    findViewById(R.id.fragment_conversation_message_item_left_account_image);
            accountImageView.setVisibility(View.VISIBLE);

            //Hide right image
            convertView
                    .findViewById(R.id.fragment_conversation_message_item_right_account_image)
                    .setVisibility(View.GONE);

            //Align text on the left
            contentView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
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

        //Change the color of the text
        if(message.getUser_id() == userID){
            contentView.setTextColor(UiUtils.getColor(getContext(),
                    R.color.conversation_user_messages_textColor));
        }
        else {
            contentView.setTextColor(UiUtils.getColor(getContext(),
                    R.color.conversation_otheruser_messages_textColor));
        }

        /*
            Update message image
         */
        if(message.hasImage()){
            //Load the image
            ImageLoadHelper.remove(messageImageView);
            ImageLoadHelper.load(getContext(), message.getImage_path(), messageImageView);

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

            //Hide user name by default
            userNameView.setVisibility(View.GONE);

            if(user != null){
                if(userID != user.getId()) {
                    //Set the name of the user
                    userNameView.setText(user.getFullName());
                    userNameView.setVisibility(View.VISIBLE);
                }
            }

            if(previousMessage != null){
                if (message.getUser_id() == previousMessage.getUser_id()){
                    userNameView.setVisibility(View.GONE);
                }
            }

        }

        /*
            Update account image
         */

        //Check if we can load a specific image
        if(user != null) {
            accountImageView.setUser(user);
        }
        else
            accountImageView.removeUser();


        return convertView;
    }
}

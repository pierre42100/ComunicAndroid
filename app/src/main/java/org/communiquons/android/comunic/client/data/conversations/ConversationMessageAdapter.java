package org.communiquons.android.comunic.client.data.conversations;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;

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
     * Possible colors for the message
     */
    private int usercolorText;
    private int usercolorBackground;
    private int otherusercolorText;
    private int otherusercolorBackground;

    /**
     * The ID of the current user
     */
    private int userID;

    /**
     * Public class constructor
     *
     * @param context The context of execution of the application
     * @param list The dataset
     * @param userID The ID of the current user
     */
    public ConversationMessageAdapter(Context context, ArrayList<ConversationMessage> list,
                                      int userID){
        super(context, 0, list);

        //Set user ID
        this.userID = userID;

        //Get the color codes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            usercolorText = getContext().getResources().
                    getColor(R.color.conversation_user_messages_textColor, getContext().getTheme());
            usercolorBackground = getContext().getResources().
                    getColor(R.color.conversation_user_messages_background, getContext().getTheme());
            otherusercolorText = getContext().getResources().
                    getColor(R.color.conversation_otheruser_messages_textColor, getContext().getTheme());
            otherusercolorBackground = getContext().getResources().
                    getColor(R.color.conversation_otheruser_messages_background, getContext().getTheme());
        } else {
            usercolorText = getContext().getResources().
                    getColor(R.color.conversation_user_messages_textColor);
            usercolorBackground = getContext().getResources().
                    getColor(R.color.conversation_user_messages_background);
            otherusercolorText = getContext().getResources().
                    getColor(R.color.conversation_otheruser_messages_textColor);
            otherusercolorBackground = getContext().getResources().
                    getColor(R.color.conversation_otheruser_messages_background);
        }
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



        /*
            Update message content
         */
        //Set the text of the message
        TextView contentView = convertView.
                findViewById(R.id.fragment_conversation_message_item_content);
        contentView.setText(message.getContent());

        //Set the color of the message
        Log.v(TAG, "User ID : " + message.getUser_id() + " current: " + userID);
        if(message.getUser_id() == userID) {
            contentView.setTextColor(usercolorText);
            contentView.setBackgroundColor(usercolorBackground);
        }
        else {
            contentView.setTextColor(otherusercolorText);
            contentView.setBackgroundColor(otherusercolorBackground);
        }


        /*
            Update account image
         */
        //Get the image of the message
        ImageView accountImage = convertView.
                findViewById(R.id.fragment_conversation_message_item_accountimage);

        if(message.getUser_id() == userID){
            accountImage.setVisibility(View.GONE);
        }
        else {
            accountImage.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}

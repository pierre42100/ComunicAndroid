package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.ConversationsInfo;
import org.communiquons.android.comunic.client.data.utils.TimeUtils;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

import java.util.ArrayList;

/**
 * Conversations adapter
 *
 * Handles the rendering of the conversations in a {@link android.widget.ListView}
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/10/17.
 */

public class ConversationsListAdapter extends ArrayAdapter<ConversationsInfo> {

    /**
     * Class constructor
     *
     * @param context The context of the application
     * @param list The list of conversations to display
     */
    public ConversationsListAdapter(Context context, ArrayList<ConversationsInfo> list){
        super(context, 0, list);
    }


    /**
     * Handle the rendering of the views
     *
     * @param position The position of the view to inflate in the list
     * @param convertView The view to convert / null if it is a new view
     * @param parent The parent
     * @return The converted view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Check if it is the first time the view is used
        if(convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_conversationslist_item, parent, false);
        }

        //Get information about the conversation
        ConversationsInfo infos = getItem(position);

        //Set the name of the conversation
        TextView conversationName = convertView
                .findViewById(R.id.fragment_conversationslist_item_name);
        conversationName.setText(infos.getDisplayName());

        //Set drawable associated with TextView (if required = unread conversation)
        Drawable drawable = UiUtils.getDrawable(getContext(), R.drawable.ic_circle);
        drawable.setBounds(0, 0, UiUtils.GetPixel(getContext(), 10),
                UiUtils.GetPixel(getContext(), 10));
        conversationName.setCompoundDrawables(
                infos.hasSaw_last_message() ? null :
                        drawable,
                null, null, null);


        //Update the number of members of the conversation
        TextView number_members = convertView
                .findViewById(R.id.fragment_conversationslist_item_number_members);
        String members_text = String.format(getContext().getResources()
                .getString(R.string.conversations_members_number), infos.countMembers());
        number_members.setText(members_text);



        //Update the last activity time of the conversation
        TextView last_activity = convertView.
                findViewById(R.id.fragment_conversationslist_item_lastactive);
        last_activity.setText(TimeUtils.TimeToString(getContext(),
                TimeUtils.time() - infos.getLast_active()));

        return convertView;

    }
}

package org.communiquons.android.comunic.client.data.conversations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.R.id.list;

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

        return convertView;

    }
}

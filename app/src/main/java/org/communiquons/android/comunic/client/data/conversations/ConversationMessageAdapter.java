package org.communiquons.android.comunic.client.data.conversations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Conversation messages adapter
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/18/17.
 */

public class ConversationMessageAdapter extends ArrayAdapter<ConversationMessage> {

    /**
     * Public class constructor
     *
     * @param context The context of execution of the application
     * @param list The dataset
     */
    public ConversationMessageAdapter(Context context, ArrayList<ConversationMessage> list){
        super(context, 0, list);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).
                inflate(android.R.layout.simple_list_item_1, parent, false);

        ((TextView) convertView).setText(getItem(position).getContent());

        return convertView;
    }
}

package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.models.ConversationMessage;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

import java.util.ArrayList;

/**
 * Conversation messages adapter
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/18/17.
 */

public class ConversationMessageAdapter extends RecyclerView.Adapter {

    /**
     * View messages types
     */
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    /**
     * Debug tag
     */
    private static final String TAG = ConversationMessageAdapter.class.getCanonicalName();

    /**
     * The ID of the current user
     */
    private int userID;

    /**
     * Information about users
     */
    private ArrayMap<Integer, UserInfo> usersInfo;

    /**
     * Activity context
     */
    private Context mContext;

    /**
     * Conversation messages
     */
    private ArrayList<ConversationMessage> mList;

    /**
     * Public class constructor
     *
     * @param context The context of execution of the application
     * @param list The dataset
     * @param userID The ID of the current user
     */
    public ConversationMessageAdapter(Context context, ArrayList<ConversationMessage> list,
                                      int userID, ArrayMap<Integer, UserInfo> usersInfo){
        super();

        //Set values
        this.userID = userID;
        this.usersInfo = usersInfo;
        this.mContext = context;
        this.mList = list;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public ConversationMessage getAt(int pos){
        return mList.get(pos);
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getUser_id() == userID ? VIEW_TYPE_MESSAGE_SENT
                : VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        View view;

        if(type == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(mContext).inflate(R.layout.conversation_message_item_sent,
                    viewGroup, false);
            return new SentMessageHolder(view);
        }

        else if(type == VIEW_TYPE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(mContext).inflate(R.layout.conversation_message_item_received,
                    viewGroup, false);
            return new ReceivedMessageHolder(view);
        }

        else
            throw new RuntimeException("Could not determine view type!");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(position);
                break;

            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(position);
                break;
        }
    }

    /**
     * Base messages holder
     */
    private class BaseMessageHolder extends RecyclerView.ViewHolder {

        private TextView mMessage;

        BaseMessageHolder(@NonNull View itemView) {
            super(itemView);

            mMessage = itemView.findViewById(R.id.message_body);
        }

        /**
         * Bind view for a conversation message
         *
         * @param pos The message to bind
         */
        @CallSuper
        void bind(int pos){
            mMessage.setText(getAt(pos).getContent());
        }
    }

    /**
     * Sent messages holder
     */
    private class SentMessageHolder extends BaseMessageHolder {

        SentMessageHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * Received messages holder
     */
    private class ReceivedMessageHolder extends BaseMessageHolder {

        WebUserAccountImage mUserAccountImage;
        TextView mUserName;

        ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);

            mUserAccountImage = itemView.findViewById(R.id.account_image);
            mUserName = itemView.findViewById(R.id.user_name);
        }

        void setUserInfoVisibility(boolean visible){
            mUserAccountImage.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            mUserName.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        void bind(int pos) {
            super.bind(pos);

            //Apply user information
            mUserAccountImage.removeUser();
            mUserName.setText("");

            if(usersInfo.containsKey(getAt(pos).getUser_id())){
                UserInfo info = usersInfo.get(getAt(pos).getUser_id());
                mUserAccountImage.setUser(info);
                mUserName.setText(info.getDisplayFullName());
            }

            if(pos < 2)
                setUserInfoVisibility(true);
            else
                if(getAt(pos).getUser_id() == getAt(pos-1).getUser_id())
                    setUserInfoVisibility(false);
        }
    }
}

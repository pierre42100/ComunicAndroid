package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.ConversationMessagesList;
import org.communiquons.android.comunic.client.data.models.ConversationMessage;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.utils.StringsUtils;
import org.communiquons.android.comunic.client.ui.listeners.OnConversationMessageActionsListener;
import org.communiquons.android.comunic.client.ui.views.ContentTextView;
import org.communiquons.android.comunic.client.ui.views.EnlargeableWebImageView;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

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
     * Activity context
     */
    private Context mContext;

    /**
     * Conversation messages
     */
    private ConversationMessagesList mList;

    /**
     * Conversation messages listener
     */
    private OnConversationMessageActionsListener mOnConversationMessageActionsListener;

    /**
     * Public class constructor
     *
     * @param context The context of execution of the application
     * @param list The list of message
     * @param userID The ID of the current user
     */
    public ConversationMessageAdapter(Context context, ConversationMessagesList list, int userID){
        super();

        //Set values
        this.userID = userID;
        this.mContext = context;
        this.mList = list;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getUser_id() == userID ? VIEW_TYPE_MESSAGE_SENT
                : VIEW_TYPE_MESSAGE_RECEIVED;
    }

    public void setOnConversationMessageActionsListener(OnConversationMessageActionsListener listener) {
        this.mOnConversationMessageActionsListener = listener;
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
        ((BaseMessageHolder) viewHolder).bind(position);
    }

    /**
     * Base messages holder
     */
    private class BaseMessageHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener{

        ContentTextView mMessage;
        private TextView mSentDate;
        private EnlargeableWebImageView mImage;

        BaseMessageHolder(@NonNull View itemView) {
            super(itemView);

            mMessage = itemView.findViewById(R.id.message_body);
            mImage = itemView.findViewById(R.id.messageImage);
            mSentDate = itemView.findViewById(R.id.text_message_time);

            mMessage.setOnLongClickListener(this);
            mImage.setOnLongClickListener(this);
        }

        /**
         * Get the time a message was sent as a string
         *
         * @param message Information about the message
         * @return Generated sent string
         */
        String messageDate(ConversationMessage message){
            return StringsUtils.FormatDate(message.getTime_insert());
        }

        /**
         * Bind view for a conversation message
         *
         * @param pos The message to bind
         */
        @CallSuper
        void bind(int pos){
            ConversationMessage message = mList.get(pos);

            mMessage.setParsedText(message.getContent());
            mMessage.setVisibility(mMessage.getText().length() > 0 ? View.VISIBLE : View.GONE);

            mImage.setVisibility(message.hasImage() ? View.VISIBLE : View.GONE);
            if(message.hasImage())
                mImage.loadURL(message.getImage_path());
            else
                mImage.removeImage();


            mSentDate.setText(messageDate(message));

            if(pos < 1)
                mSentDate.setVisibility(View.VISIBLE);
            else if(messageDate(mList.get(pos-1)).equals(messageDate(message)))
                mSentDate.setVisibility(View.GONE);
            else
                mSentDate.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean onLongClick(View v) {

            if(mOnConversationMessageActionsListener != null)
                mOnConversationMessageActionsListener.onOpenContextMenu(getLayoutPosition(), v);

            return true;
        }
    }

    /**
     * Sent messages holder
     */
    private class SentMessageHolder extends BaseMessageHolder {

        SentMessageHolder(@NonNull View itemView) {
            super(itemView);

            mMessage.setLinksColor(R.color.conversation_user_links_color);
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

            mMessage.setLinksColor(R.color.conversation_otheruser_links_color);
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

            if(mList.hasUserForMessage(pos)){
                UserInfo info = mList.getUserForMessage(pos);
                mUserAccountImage.setUser(info);
                mUserName.setText(info.getDisplayFullName());
            }

            if(pos < 1)
                setUserInfoVisibility(true);
            else
                setUserInfoVisibility(
                        !(mList.get(pos).getUser_id() == mList.get(pos-1).getUser_id()));
        }
    }
}

package org.communiquons.android.comunic.client.data.arrays;

import android.util.ArrayMap;

import org.communiquons.android.comunic.client.data.models.ConversationMessage;
import org.communiquons.android.comunic.client.data.models.UserInfo;

import java.util.ArrayList;

/**
 * Conversation messages list
 *
 * @author Pierre HUBERT
 */
public class ConversationMessagesList extends ArrayList<ConversationMessage> {

    /**
     * Information about the related users
     */
    private ArrayMap<Integer, UserInfo> mUsersInfo = new ArrayMap<>();


    public ArrayMap<Integer, UserInfo> getUsersInfo() {
        return mUsersInfo;
    }

    public void setUsersInfo(ArrayMap<Integer, UserInfo> usersInfo) {
        this.mUsersInfo = usersInfo;
    }

    /**
     * Check whether information about the user who posted the message at pos are present or not
     *
     * @param pos The position to check
     * @return The result of the operation
     */
    public boolean hasUserForMessage(int pos){
        return mUsersInfo.containsKey(get(pos).getUser_id());
    }

    /**
     * Get the information about a user who posted a message
     *
     * @param pos The position of the message to get
     * @return Infomration about the user
     */
    public UserInfo getUserForMessage(int pos){
        return mUsersInfo.get(get(pos).getUser_id());
    }

    /**
     * Get the ID of the missing users in the user information list
     *
     * @return Information about the user
     */
    public ArrayList<Integer> getMissingUsersList(){

        ArrayList<Integer> missing = new ArrayList<>();

        //Process the list of messages
        for(ConversationMessage message : this){

            if(!getUsersInfo().containsKey(message.getUser_id())){
                if(!missing.contains(message.getUser_id()))
                    missing.add(message.getUser_id());
            }

        }

        return missing;
    }
}

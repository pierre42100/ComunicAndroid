package org.communiquons.android.comunic.client.data.conversations;

import android.support.annotation.Nullable;

import java.util.Objects;

/**
 * Conversation content object
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/16/17.
 */

public class ConversationMessage {

    /**
     * Message values
     */
    private int id;
    private int conversation_id;
    private int user_id;
    private String image_path = null;
    private String content;
    private int time_insert;

    /**
     * Set the ID of a content
     *
     * @param id The id of the content
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the content ID
     *
     * @return The ID of the content
     */
    public int getId() {
        return id;
    }

    /**
     * Set the ID of the conversation attached to the content
     *
     * @param conversation_id The ID of the conversation
     */
    public void setConversation_id(int conversation_id) {
        this.conversation_id = conversation_id;
    }

    /**
     * Get the conversation ID attached to a content
     *
     * @return The ID of the conversation
     */
    public int getConversation_id() {
        return conversation_id;
    }

    /**
     * Set the ID of the user who posted the content
     *
     * @param user_id The ID of the user
     */
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    /**
     * Get the ID of the user who posted the content
     *
     * @return The ID of the user who posted the content
     */
    public int getUser_id() {
        return user_id;
    }

    /**
     * Set the path of the image associated to the content
     *
     * @param image_path The path of the image
     */
    public void setImage_path(String image_path) {
        if(image_path != null && !Objects.equals(image_path, "null"))
            this.image_path = image_path;
        else
            this.image_path = null;
    }

    /**
     * Get the path of the image associated with the content
     *
     * @return The path of the image
     */
    @Nullable
    public String getImage_path() {
        return image_path;
    }

    /**
     * Set the content of the message
     *
     * @param content The content of the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Get the content of the message
     *
     * @return The content
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the image of insertion of the message
     *
     * @param time_insert The time of insertion of the message
     */
    public void setTime_insert(int time_insert) {
        this.time_insert = time_insert;
    }

    /**
     * Get the time of insertion of the message
     *
     * @return The time of insertion of the message
     */
    public int getTime_insert() {
        return time_insert;
    }
}

package org.communiquons.android.comunic.client.data.models;

import org.communiquons.android.comunic.client.data.utils.TimeUtils;

/**
 * Friend object
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/12/17.
 */

public class Friend {

    /**
     * The minimal time of activity required to consider a user as signed in
     */
    private static final int USER_INACTIVE_AFTER = 35;

    /**
     * The ID of the friend
     */
    private int id;

    /**
     * Specify whether the friendship request was accepted or not
     */
    private boolean accepted;

    /**
     * Specify wether the user is following this friend or not
     */
    private boolean following;

    /**
     * The last activity timestamp of the friend
     */
    private int last_activity;

    /**
     * Public constructor
     */
    public Friend() {}

    /**
     * Set the friend ID
     *
     * @param id The ID of the friend
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the ID of the friend
     *
     * @return The ID of the friend
     */
    public int getId() {
        return id;
    }

    /**
     * Specify whether the friend was accepted or not
     *
     * @param accepted True if the friend was accepted / false else
     */
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    /**
     * Check if the friend was accepted or not by the user
     *
     * @return True if the friend was accepted / false else
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Specify wether the user is following this friend or not
     *
     * @param following True if the user is following this friend
     */
    public void setFollowing(boolean following) {
        this.following = following;
    }

    /**
     * Check if the user is following this friend or not
     *
     * @return True if the user is followed / false else
     */
    public boolean isFollowing() {
        return following;
    }

    /**
     * Set the last user activity time
     *
     * @param last_activity The last activity time
     */
    public void setLast_activity(int last_activity) {
        this.last_activity = last_activity;
    }

    /**
     * Get the last activity time of the user
     *
     * @return The last activity time of the user
     */
    public int getLast_activity() {
        return last_activity;
    }

    /**
     * Determine whether user is signed in or not
     *
     * @return True if user is signed in / false else
     */
    public boolean signed_in(){
        return (TimeUtils.time()-USER_INACTIVE_AFTER) < last_activity;
    }
}

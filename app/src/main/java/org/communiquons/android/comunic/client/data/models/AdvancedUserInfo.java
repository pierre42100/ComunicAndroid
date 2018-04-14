package org.communiquons.android.comunic.client.data.models;

/**
 * Advanced informations about a single user
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/13/18.
 */

public class AdvancedUserInfo extends UserInfo {

    //Private fields
    private int account_creation_time;
    private boolean accessForbidden = false;
    private boolean canPostText;

    /**
     * Get the account creation time
     *
     * @return The time of creation of the account
     */
    public int getAccount_creation_time() {
        return account_creation_time;
    }

    /**
     * Set the account creation time
     *
     * @param account_creation_time The time when the account was created
     */
    public void setAccount_creation_time(int account_creation_time) {
        this.account_creation_time = account_creation_time;
    }

    /**
     * Check whether the access to the page is forbidden or not
     *
     * @return TRUE if the access to the page is forbidden / FALSE else
     */
    public boolean isAccessForbidden() {
        return accessForbidden;
    }

    /**
     * Set the forbidden state of the page
     *
     * @param accessForbidden TRUE if the access to the page is forbidden / FALSE else
     */
    public void setAccessForbidden(boolean accessForbidden) {
        this.accessForbidden = accessForbidden;
    }

    /**
     * Set whether the current user can post a text on this user page or not
     *
     * @param canPostText TRUE to allow / FALSE to deny
     */
    public void setCanPostText(boolean canPostText) {
        this.canPostText = canPostText;
    }

    /**
     * Check whether the user can post text on this user page
     *
     * @return TRUE : yes / FALSE no
     */
    public boolean isCanPostText() {
        return canPostText;
    }
}

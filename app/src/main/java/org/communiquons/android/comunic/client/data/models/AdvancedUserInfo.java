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
}

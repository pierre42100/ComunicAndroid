package org.communiquons.android.comunic.client.ui.listeners;

/**
 * This interface is implemented by all classes that have to relay
 * the opening of a group
 *
 * @author Pierre HUBERT
 */
public interface OnOpenGroupListener {

    /**
     * Open a group page
     *
     * @param groupID The ID of the group to open
     */
    void onOpenGroup(int groupID);

    /**
     * Open a group access denied fragment page
     *
     * @param groupID The ID of the group to open
     */
    void onOpenGroupAccessDenied(int groupID);
}

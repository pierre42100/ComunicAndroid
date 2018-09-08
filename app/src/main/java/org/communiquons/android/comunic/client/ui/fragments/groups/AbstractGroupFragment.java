package org.communiquons.android.comunic.client.ui.fragments.groups;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.CallSuper;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.asynctasks.CancelGroupMembershipRequestTask;
import org.communiquons.android.comunic.client.ui.asynctasks.LeaveGroupTask;
import org.communiquons.android.comunic.client.ui.asynctasks.RespondGroupInvitationTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SendGroupMembershipRequestTask;
import org.communiquons.android.comunic.client.ui.fragments.AbstractFragment;
import org.communiquons.android.comunic.client.ui.listeners.OnGroupMembershipUpdateListener;

import java.util.Objects;

/**
 * Base Group Fragment
 *
 * @author Pierre HUBERT
 */
abstract class AbstractGroupFragment extends AbstractFragment implements OnGroupMembershipUpdateListener {

    /**
     * Debug tag
     */
    private static final String TAG = AbstractGroupFragment.class.getSimpleName();

    @Override
    public void onResume() {
        super.onResume();

        MainActivity.SetNavbarSelectedOption(Objects.requireNonNull(getActivity()),
                R.id.action_personal_page);
    }

    @Override
    public void onRespondInvitation(GroupInfo group, boolean accept) {
        RespondGroupInvitationTask respondGroupInvitationTask =
                new RespondGroupInvitationTask(getContext(), accept);
        performGroupMembershipUpdate(group.getId(), respondGroupInvitationTask);
    }

    @Override
    public void onCancelRequest(GroupInfo group) {
        CancelGroupMembershipRequestTask task = new CancelGroupMembershipRequestTask(getActivity());
        performGroupMembershipUpdate(group.getId(), task);
    }

    @Override
    public void onSendRequest(GroupInfo group) {
        SendGroupMembershipRequestTask task = new SendGroupMembershipRequestTask(getActivity());
        performGroupMembershipUpdate(group.getId(), task);
    }

    @Override
    public void onLeaveGroup(GroupInfo group) {
        final int groupID = group.getId();
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_leave_group_title)
                .setMessage(R.string.dialog_leave_group_message)
                .setNegativeButton(R.string.dialog_leave_group_cancel, null)

                .setPositiveButton(R.string.dialog_leave_group_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveGroup(groupID);
                    }
                }).show();
    }

    /**
     * Do leave the group for the user
     *
     * @param groupID The ID of the group to leave
     */
    private void leaveGroup(final int groupID) {
        LeaveGroupTask leaveGroupTask = new LeaveGroupTask(getActivity());
        performGroupMembershipUpdate(groupID, leaveGroupTask);
    }

    /**
     * Perform group membership update
     *
     * @param groupID The ID of the target group
     * @param task    Task to execute
     */
    private void performGroupMembershipUpdate(final int groupID, SafeAsyncTask<Integer, Void, Boolean> task) {
        task.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<Boolean>() {
            @Override
            public void OnPostExecute(Boolean success) {
                onGroupMembershipUpdated(success, groupID);
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupID);
        getTasksManager().addTask(task);
    }

    /**
     * This method is called when the membership to a group has been updated
     *
     * @param success TRUE if the operation was a success / FALSE else
     * @param groupID Information about the updated group
     */
    @CallSuper
    public void onGroupMembershipUpdated(boolean success, int groupID) {

        //Check for errors
        if (!success)
            Toast.makeText(getActivity(), R.string.err_update_group_membership,
                    Toast.LENGTH_SHORT).show();

    }
}

package org.communiquons.android.comunic.client.ui.fragments.groups;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.enums.GroupsMembershipLevels;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.communiquons.android.comunic.client.ui.asynctasks.GetGroupInfoTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.listeners.OnOpenGroupListener;
import org.communiquons.android.comunic.client.ui.views.GroupImageView;
import org.communiquons.android.comunic.client.ui.views.GroupMembershipStatusView;

import java.util.Objects;

/**
 * Group access denied fragment
 *
 * This fragment appears when the user was explicitly denied
 * access to a group advanced information
 *
 * @author Pierre HUBERT
 */
public class GroupAccessDeniedFragment extends AbstractGroupFragment {

    /**
     * Debug tag
     */
    private static final String TAG = GroupAccessDeniedFragment.class.getSimpleName();

    /**
     * Mandatory argument to use this fragment : target group id
     */
    public static final String ARGUMENT_GROUP_ID = "group_id";

    /**
     * Current group ID
     */
    private int mGroupID;

    /**
     * Views
     */
    private GroupImageView mGroupImageView;
    private TextView mGroupNameView;
    private GroupMembershipStatusView mGroupMembershipView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_access_denied, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGroupImageView = view.findViewById(R.id.groupImageView);
        mGroupNameView = view.findViewById(R.id.groupName);
        mGroupMembershipView = view.findViewById(R.id.groupMembershipStatusView);
    }

    @Override
    public void onStart() {
        super.onStart();

        assert getArguments() != null;
        mGroupID = getArguments().getInt(ARGUMENT_GROUP_ID);

        getGroupInfo();
    }

    @Override
    public void onGroupMembershipUpdated(boolean success, int groupID) {
        super.onGroupMembershipUpdated(success, groupID);
        getGroupInfo();
    }

    /**
     * Get and return information about the group
     */
    private void getGroupInfo(){

        getTasksManager().unsetSpecificTasks(GetGroupInfoTask.class);

        GetGroupInfoTask task = new GetGroupInfoTask(getActivity());
        task.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<GroupInfo>() {
            @Override
            public void OnPostExecute(GroupInfo groupInfo) {
                onGotGroupInfo(groupInfo);
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mGroupID);
        getTasksManager().addTask(task);

    }

    /**
     * Method called once we have got group information
     *
     * @param info Information about the group
     */
    private void onGotGroupInfo(@Nullable GroupInfo info){

        //Check for errors
        if(info == null){
            Toast.makeText(getActivity(), R.string.err_get_group_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Check if the user is now at least a member of the group
        if(info.isAtLeastMember()){

            //Go back to the group
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager().popBackStack();
            ((OnOpenGroupListener)getActivity()).onOpenGroup(info.getId());
        }

        //Apply group information
        mGroupImageView.setGroup(info);
        mGroupNameView.setText(info.getDisplayName());
        mGroupMembershipView.setGroup(info);
        mGroupMembershipView.setOnGroupMembershipUpdateListener(this);
    }
}

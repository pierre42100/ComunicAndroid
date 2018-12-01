package org.communiquons.android.comunic.client.ui.fragments.groups;

import android.app.AlertDialog;
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
import org.communiquons.android.comunic.client.data.models.AdvancedGroupInfo;
import org.communiquons.android.comunic.client.ui.asynctasks.GetGroupAdvancedInfoTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.android.comunic.client.ui.views.GroupImageView;

/**
 * Group main page
 *
 * @author Pierre HUBERT
 */
public class GroupPageMainFragment extends AbstractGroupFragment {

    /**
     * Debug tag
     */
    private static final String TAG = GroupPageMainFragment.class.getSimpleName();

    /**
     * Page arguments
     */
    public static final String ARGUMENT_GROUP_ID = "group_id";

    /**
     * Target group ID
     */
    private int mGroupID;

    /**
     * Target group advanced information
     */
    private AdvancedGroupInfo mAdvancedGroupInfo;

    /**
     * Loading dialog
     */
    private AlertDialog mLoadingDialog;

    /**
     * UI views
     */
    private GroupImageView mGroupImage;
    private TextView mGroupName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_group_main_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get views
        mGroupImage = view.findViewById(R.id.groupImageView);
        mGroupName = view.findViewById(R.id.groupName);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Get group ID
        assert getArguments() != null;
        mGroupID = getArguments().getInt(ARGUMENT_GROUP_ID);

        if(mAdvancedGroupInfo == null)
            loadGroupInformation();
        else
            applyGroupInfo();
    }

    /**
     * Download group information
     */
    private void loadGroupInformation(){

        //Display a loading dialog
        mLoadingDialog = UiUtils.create_loading_dialog(getActivity());

        getTasksManager().unsetSpecificTasks(GetGroupAdvancedInfoTask.class);

        GetGroupAdvancedInfoTask task =  new GetGroupAdvancedInfoTask(getActivity());
        task.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<AdvancedGroupInfo>() {
            @Override
            public void OnPostExecute(@Nullable AdvancedGroupInfo advancedGroupInfo) {
                onLoadGroupAdvancedInfoCallback(advancedGroupInfo);
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mGroupID);
        getTasksManager().addTask(task);
    }

    /**
     * Get group advanced information callback
     *
     * @param info Information about the target group
     */
    private void onLoadGroupAdvancedInfoCallback(@Nullable AdvancedGroupInfo info){

        //Hide loading dialog
        mLoadingDialog.dismiss();

        if(info == null) {
            Toast.makeText(getActivity(), R.string.err_get_group_info, Toast.LENGTH_SHORT).show();
            return;
        }

        mAdvancedGroupInfo = info;
        applyGroupInfo();
    }

    /**
     * Apply previously downloaded group information
     */
    private void applyGroupInfo(){

        //Apply main group information
        mGroupName.setText(mAdvancedGroupInfo.getDisplayName());
        mGroupImage.setGroup(mAdvancedGroupInfo);

    }
}

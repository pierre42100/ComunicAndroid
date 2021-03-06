package org.communiquons.android.comunic.client.ui.fragments.groups;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.adapters.GroupsListAdapter;
import org.communiquons.android.comunic.client.ui.asynctasks.GetUserGroupsTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.listeners.OnGroupActionListener;
import org.communiquons.android.comunic.client.ui.listeners.OnOpenGroupListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * User groups fragment
 *
 * @author Pierre HUBERT
 */
public class UserGroupsFragment extends AbstractGroupFragment implements OnGroupActionListener {

    /**
     * Debug tag
     */
    private static final String TAG = UserGroupsFragment.class.getSimpleName();

    /**
     * Views
     */
    private ProgressBar mProgressBar;
    private RecyclerView mGroupsView;
    private TextView mNoGroupNotice;

    /**
     * User groups
     */
    private ArrayMap<Integer, GroupInfo> mGroupsList;

    /**
     * Groups adapter
     */
    private GroupsListAdapter mGroupsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get views
        mGroupsView = view.findViewById(R.id.groups_list);
        mProgressBar = view.findViewById(R.id.progressBar);
        mNoGroupNotice = view.findViewById(R.id.noGroupNotice);

        setProgressBarVisibility(true);
        setNoGroupNoticeVisibility(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mGroupsList == null)
            getGroupsList();
        else
            displayGroupsList();
    }

    /**
     * Get the list of groups of the user
     */
    private void getGroupsList(){

        setProgressBarVisibility(true);

        getTasksManager().unsetSpecificTasks(GetUserGroupsTask.class);
        GetUserGroupsTask getUserGroupsTask = new GetUserGroupsTask(getActivity());
        getUserGroupsTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<ArrayMap<Integer, GroupInfo>>() {
            @Override
            public void OnPostExecute(ArrayMap<Integer, GroupInfo> list) {
                getGroupsListCallback(list);
            }
        });
        getUserGroupsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        getTasksManager().addTask(getUserGroupsTask);
    }

    /**
     * Get user groups callback
     *
     * @param list The list of groups of the user
     */
    private void getGroupsListCallback(@Nullable ArrayMap<Integer, GroupInfo> list){

        setProgressBarVisibility(false);

        if(list == null){
            Toast.makeText(getActivity(), R.string.err_get_user_groups, Toast.LENGTH_SHORT).show();
            return;
        }

        mGroupsList = list;
        displayGroupsList();
    }

    /**
     * Display the list of groups of the user
     */
    private void displayGroupsList(){

        setProgressBarVisibility(false);


        mGroupsAdapter = new GroupsListAdapter(getActivity());

        mGroupsView.setAdapter(mGroupsAdapter);
        mGroupsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupsView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()),
                DividerItemDecoration.VERTICAL));



        mGroupsAdapter.setOnGroupActionListener(this);
        mGroupsAdapter.setList(new ArrayList<>(mGroupsList.values()));
        mGroupsAdapter.notifyDataSetChanged();

        setNoGroupNoticeVisibility(mGroupsList.size() == 0);
    }

    @Override
    public void onGroupMembershipUpdated(boolean success, int groupID) {
        super.onGroupMembershipUpdated(success, groupID);
        getGroupsList();
    }

    /**
     * Update (set) progressbar visibility
     *
     * @param visible Visibility of the progress bar
     */
    private void setProgressBarVisibility(boolean visible){
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Update no group notice visibility
     *
     * @param visible The visibility of the notice
     */
    private void setNoGroupNoticeVisibility(boolean visible){
        mNoGroupNotice.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onOpenGroup(int groupID) {
        Log.v(TAG, "Open group " + groupID);
        ((OnOpenGroupListener)Objects.requireNonNull(getActivity())).onOpenGroup(groupID);
    }

    @Override
    public void onOpenGroupAccessDenied(int groupID) {
        ((OnOpenGroupListener)Objects.requireNonNull(getActivity()))
                .onOpenGroupAccessDenied(groupID);
    }
}

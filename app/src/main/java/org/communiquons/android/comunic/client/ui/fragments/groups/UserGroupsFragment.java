package org.communiquons.android.comunic.client.ui.fragments.groups;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.communiquons.android.comunic.client.ui.adapters.GroupsListAdapter;
import org.communiquons.android.comunic.client.ui.asynctasks.GetUserGroupsTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;

import java.util.ArrayList;

/**
 * User groups fragment
 *
 * @author Pierre HUBERT
 */
public class UserGroupsFragment extends AbstractGroupFragment {

    /**
     * Views
     */
    private ProgressBar mProgressBar;
    private RecyclerView mGroupsView;

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

        setProgressBarVisiblity(true);
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

        setProgressBarVisiblity(true);

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

        setProgressBarVisiblity(false);

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

        setProgressBarVisiblity(false);

        mGroupsAdapter = new GroupsListAdapter(getActivity());
        mGroupsAdapter.setList(new ArrayList<>(mGroupsList.values()));

        mGroupsView.setAdapter(mGroupsAdapter);
        mGroupsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupsView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

    }

    /**
     * Update (set) progressbar visibility
     *
     * @param visible Visibility level of the progress bar
     */
    private void setProgressBarVisiblity(boolean visible){
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}

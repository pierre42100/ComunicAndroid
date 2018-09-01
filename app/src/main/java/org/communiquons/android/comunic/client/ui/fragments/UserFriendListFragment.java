package org.communiquons.android.comunic.client.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.ui.adapters.UsersBasicAdapter;
import org.communiquons.android.comunic.client.ui.asynctasks.GetUserFriendsListTask;
import org.communiquons.android.comunic.client.ui.listeners.onOpenUsersPageListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Friend list fragment for a specific user (not the current user signed in)
 *
 * @author Pierre HUBERT
 */
public class UserFriendListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = UserFriendListFragment.class.getCanonicalName();

    /**
     * Argument to pass to the fragment to transfer user ID
     */
    public static final String ARGUMENT_USER_ID = "user_id";

    private int mUserID;

    /**
     * Views
     */
    private ProgressBar mProgress;
    private TextView mNoFriendsNotice;
    private ListView mList;

    /**
     * Users list
     */
    private ArrayList<UserInfo> mUsersList;

    /**
     * Get user task
     */
    private GetUserFriendsListTask mLoadTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        mUserID = getArguments().getInt(ARGUMENT_USER_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_friends_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgress = view.findViewById(R.id.progressBar);
        mNoFriendsNotice = view.findViewById(R.id.nofriendsNotice);
        mList = view.findViewById(R.id.list);

        mList.setOnItemClickListener(this);

        setProgressBarVisibility(true);
        setNoFriendsNoticeVisibility(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mUsersList != null)
            showFriendsList();

        load_list();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLoadTask != null)
            mLoadTask.setOnPostExecuteListener(null);
    }

    /**
     * Load friends list
     */
    private void load_list() {

        if (mLoadTask != null)
            mLoadTask.setOnPostExecuteListener(null);

        mLoadTask = new GetUserFriendsListTask(getActivity());
        mLoadTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<ArrayMap<Integer, UserInfo>>() {
            @Override
            public void OnPostExecute(ArrayMap<Integer, UserInfo> users) {
                if (getActivity() != null)
                    applyFriendsList(users);
            }
        });

        mLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUserID);
    }

    /**
     * Apply friend list
     *
     * @param list The list to apply
     */
    private void applyFriendsList(@Nullable ArrayMap<Integer, UserInfo> list) {

        setProgressBarVisibility(false);

        if (list == null) {
            Toast.makeText(getContext(), R.string.err_get_user_friends, Toast.LENGTH_SHORT).show();
            return;
        }

        this.mUsersList = new ArrayList<>(list.values());

        showFriendsList();
    }

    /**
     * Show friends list
     */
    private void showFriendsList() {

        setNoFriendsNoticeVisibility(mUsersList.size() == 0);

        UsersBasicAdapter usersBasicAdapter = new UsersBasicAdapter(
                Objects.requireNonNull(getActivity()), mUsersList);

        mList.setAdapter(usersBasicAdapter);
    }


    private void setProgressBarVisibility(boolean visible) {
        mProgress.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setNoFriendsNoticeVisibility(boolean visible){
        mNoFriendsNotice.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mUsersList.size() > position)
            ((onOpenUsersPageListener) Objects.requireNonNull(getActivity())).openUserPage(
                    mUsersList.get(position).getId());
    }
}

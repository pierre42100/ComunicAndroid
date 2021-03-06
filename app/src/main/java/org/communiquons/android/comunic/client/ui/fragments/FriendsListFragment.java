package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.FriendsList;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.FriendsListHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.Friend;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.adapters.FriendsAdapter;
import org.communiquons.android.comunic.client.ui.asynctasks.GetFriendsListTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.listeners.OnFriendListActionListener;
import org.communiquons.android.comunic.client.ui.listeners.onOpenUsersPageListener;
import org.communiquons.android.comunic.client.ui.listeners.openConversationListener;
import org.communiquons.android.comunic.client.ui.views.ScrollRecyclerView;

import java.util.Objects;

/**
 * Friends list fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/11/17.
 */

public class FriendsListFragment extends AbstractFragment implements OnFriendListActionListener,
        PopupMenu.OnMenuItemClickListener {

    /**
     * Debug tag
     */
    private static final String TAG = FriendsListFragment.class.getSimpleName();

    /**
     * Application context
     */
    private Context mContext;

    /**
     * The current list of friends
     */
    private FriendsList mList;

    /**
     * Friend list operations object
     */
    private FriendsListHelper mFriendsHelper;

    /**
     * Conversation opener
     */
    private openConversationListener mConvOpener;

    /**
     * Users page opener
     */
    private onOpenUsersPageListener mUsersPageOpener;

    /**
     * Friend adapter
     */
    private FriendsAdapter mAdapter;

    /**
     * Loading progress bar
     */
    private ProgressBar mProgressBar;

    /**
     * No friend notice
     */
    private TextView mNoFriendNotice;

    /**
     * Friends list view
     */
    private ScrollRecyclerView mFriendsList;

    /**
     * Current friend on context menu
     */
    private int mPosInContextMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Save application context
        mContext = Objects.requireNonNull(getActivity()).getApplicationContext();

        //Create friend list helper object
        mFriendsHelper = new FriendsListHelper(DatabaseHelper.getInstance(getActivity()), mContext);

        //Cast activity to mConvOpener
        try {
            mConvOpener = (openConversationListener) getActivity();
            mUsersPageOpener = (onOpenUsersPageListener) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();

            throw new RuntimeException(getActivity().getClass().getName() + " must implement" +
                    "ConversationsListHelper.openConversationListener " +
                    "and GetUsersHelper.onOpenUsersPageListener !");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friendslist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get loading progress bar
        mProgressBar = view.findViewById(R.id.fragment_friendslist_progressbar);

        //Get the no friend notice
        mNoFriendNotice = view.findViewById(R.id.no_friend_notice);
        mNoFriendNotice.setVisibility(View.GONE);

        //Get friends RecyclerView
        mFriendsList = view.findViewById(R.id.friendslist);

        //Retain the fragment
        //setRetainInstance(true);

    }

    @Override
    public void onResume() {
        super.onResume();

        //Update the title of the application
        Objects.requireNonNull(getActivity()).setTitle(R.string.fragment_friendslist_title);
        MainActivity.SetNavbarSelectedOption(getActivity(), R.id.action_friendslist);

        //Refresh the friends list
        refresh_friend_list();
    }

    /**
     * Refresh the friend list
     */
    void refresh_friend_list() {

        //Display loading bar
        display_progress_bar(true);

        GetFriendsListTask getFriendsListTask = new GetFriendsListTask(getActivity());
        getFriendsListTask.setOnPostExecuteListener(this::apply_friends_list);
        getFriendsListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        getTasksManager().addTask(getFriendsListTask);
    }

    /**
     * Set the the list of friend attached to the list adapter
     *
     * @param friendsList The friends list to apply
     */
    private void apply_friends_list(@Nullable FriendsList friendsList) {

        //Remove progress bar
        display_progress_bar(false);

        //Check for errors
        if (friendsList == null) {
            Toast.makeText(mContext, R.string.fragment_friendslist_err_refresh,
                    Toast.LENGTH_LONG).show();
            return;
        }

        //Save the list of friends
        this.mList = friendsList;

        //Update the visibility of the no friend notice
        updateNoFriendNoticeVisibility();

        //Set the adapter
        mAdapter = new FriendsAdapter(getActivity(), friendsList, this);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFriendsList.addItemDecoration(new DividerItemDecoration(mFriendsList.getContext(),
                DividerItemDecoration.VERTICAL));
        mFriendsList.setAdapter(mAdapter);

        //Register the view for the context menu
        registerForContextMenu(mFriendsList);

    }

    /**
     * Ask user to confirm friend deletion, the friend is specified by its position in the list
     *
     * @param pos the position of the friend to delete
     */
    private void delete_friend(final int pos) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.popup_deletefriend_title)
                .setMessage(R.string.popup_deletefriend_message);

        builder.setPositiveButton(R.string.popup_deletefriend_button_confirm, (dialog, which) -> {

            //Get the friend to delete
            final Friend toDelete = mList.get(pos);

            //Apply new list version
            mList.remove(pos);
            mAdapter.notifyDataSetChanged();

            //Remove the friend list on a parallel thread
            new AsyncTask<Integer, Void, Void>() {
                @Override
                protected Void doInBackground(Integer[] params) {

                    //Delete the friend from the list
                    mFriendsHelper.remove(toDelete);

                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pos);
        });
        builder.setNegativeButton(R.string.popup_deletefriend_button_cancel, null);
        builder.show();


    }


    @Override
    public void onOpenUserPage(int userID) {
        mUsersPageOpener.openUserPage(userID);
    }

    @Override
    public void onRespondFrienshipRequest(int pos, final boolean response) {

        //Get the Friend object
        Friend targetFriend = mList.get(pos);

        if (response)
            //Mark the friend as accepted
            targetFriend.setAccepted(true);
        else
            //Remove the friend from the list
            mList.remove(pos);

        //Inform the adapter the list has changed
        mAdapter.notifyDataSetChanged();

        //Accept the request on a separate thread
        new AsyncTask<Friend, Void, Void>() {
            @Override
            protected Void doInBackground(Friend... params) {
                mFriendsHelper.respondRequest(params[0], response);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, targetFriend);

    }

    @Override
    public void onOpenContextMenuForFriend(View view, int pos) {

        //Save selected position
        mPosInContextMenu = pos;

        //Initialize menu
        PopupMenu menu = new PopupMenu(getActivity(), view);
        menu.inflate(R.menu.menu_friend);
        menu.setOnMenuItemClickListener(this);


        //Update following checkbox
        menu.getMenu().findItem(R.id.action_follow).setChecked(mList.get(pos)
                .isFollowing());

        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            //To open a private conversation with the friend
            case R.id.action_private_conversation:
                mConvOpener.openPrivateConversation(mList.get(mPosInContextMenu).getId());
                return true;

            //To delete the friend
            case R.id.action_delete_friend:
                delete_friend(mPosInContextMenu);
                return true;

            case R.id.action_follow:
                onSetFollowing(mPosInContextMenu,
                        !mList.get(mPosInContextMenu).isFollowing());
                return true;
        }


        return false;
    }

    @Override
    public void onSetFollowing(int pos, boolean following) {

        Friend friend = mList.get(pos);
        friend.setFollowing(following);

        mAdapter.notifyDataSetChanged();

        //Perform update
        new SetFollowingTask(getActivity(), friend.getId(), friend.isFollowing())
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * Class used to update following status
     */
    static class SetFollowingTask extends SafeAsyncTask<Void, Void, Void> {

        private int friendID;
        private boolean follow;

        SetFollowingTask(Context context, int friendID, boolean follow) {
            super(context);
            this.friendID = friendID;
            this.follow = follow;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new FriendsListHelper(getContext()).setFollowing(friendID, follow);
            return null;
        }
    }

    /**
     * Hide (or display) progress bar
     *
     * @param display Specify whether the loading bar has to be shown or not
     */
    private void display_progress_bar(boolean display) {
        mProgressBar.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    /**
     * Update the visibility of the no friend notice
     */
    private void updateNoFriendNoticeVisibility() {
        if (mList != null)
            mNoFriendNotice.setVisibility(mList.size() == 0 ? View.VISIBLE : View.GONE);
    }
}

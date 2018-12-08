package org.communiquons.android.comunic.client.ui.fragments.groups;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.ui.asynctasks.GetGroupPostsTask;
import org.communiquons.android.comunic.client.ui.asynctasks.GetLatestPostsTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.fragments.AbstractPostsListFragment;
import org.communiquons.android.comunic.client.ui.fragments.PostsCreateFormFragment;

/**
 * Group posts fragment
 *
 * @author Pierre HUBERT
 */
public class GroupsPostsFragment extends AbstractPostsListFragment {

    /**
     * Debug tag
     */
    private static final String TAG = GroupsPostsFragment.class.getSimpleName();

    /**
     * Bundle arguments
     */
    public static final String ARGUMENT_GROUP_ID = "group_id";
    public static final String ARGUMENT_CAN_CREATE_GROUPS = "can_create_posts";

    /**
     * The ID of the current group
     */
    private int mGroupID;

    /**
     * Whether posts can be created by the user
     */
    private boolean mCanCreatePosts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get target group ID
        assert getArguments() != null;
        mGroupID = getArguments().getInt(ARGUMENT_GROUP_ID);
        mCanCreatePosts = getArguments().getBoolean(ARGUMENT_CAN_CREATE_GROUPS);

        setDisplayPostsTarget(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        enablePostFormFragment(mCanCreatePosts);
        if(mCanCreatePosts)
            init_create_post_fragment(PostsCreateFormFragment.PAGE_TYPE_GROUP, mGroupID);
    }

    @Override
    public void onLoadPosts() {
        loadPosts(-1);
    }

    @Override
    public void onLoadMorePosts(int last_post_id) {
        loadPosts(last_post_id-1);
    }

    /**
     * Load a list of posts
     *
     * @param last_post_id The id of latest post to start from. Set -1 to get the latest posts
     */
    private void loadPosts(int last_post_id){
        getTasksManager().unsetSpecificTasks(GetLatestPostsTask.class);
        GetGroupPostsTask task = new GetGroupPostsTask(getActivity());
        task.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<PostsList>() {
            @Override
            public void OnPostExecute(PostsList posts) {
                onGotNewPosts(posts);
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mGroupID, last_post_id);
        getTasksManager().addTask(task);
    }
}

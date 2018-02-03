package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.ImageLoad.ImageLoadManager;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.posts.Post;
import org.communiquons.android.comunic.client.data.posts.PostTypes;
import org.communiquons.android.comunic.client.data.posts.PostsList;
import org.communiquons.android.comunic.client.data.utils.UiUtils;
import org.communiquons.android.comunic.client.data.utils.Utilities;

/**
 * Posts adapter
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/21/18.
 */

public class PostsAdapter extends ArrayAdapter<Post>{

    /**
     * Informations about the users
     */
    private ArrayMap<Integer, UserInfo> mUsersInfos;

    /**
     * Utilities object
     */
    private Utilities utils;

    /**
     * Create the Post Adapter
     *
     * @param context The context of execution of the application
     * @param list The list of posts
     * @param usersInfos Informations about the user
     */
    public PostsAdapter(Context context, PostsList list, ArrayMap<Integer, UserInfo> usersInfos){
        super(context, 0, list);

        //Save the users info object
        mUsersInfos = usersInfos;

        //Create utilities object
        utils = new Utilities(getContext());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Check if the view has to be inflated
        if(convertView == null)
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.post_item, parent, false);

        //Get informations about the post and the user
        Post post = getItem(position);
        assert post != null;
        UserInfo userInfo = null;
        if(mUsersInfos.containsKey(post.getUserID()))
            userInfo = mUsersInfos.get(post.getUserID());

        //Get the views related to user Informations
        ImageView userAccountImage = convertView.findViewById(R.id.user_account_image);
        TextView userAccountName = convertView.findViewById(R.id.user_account_name);

        //Reset user informations
        userAccountName.setText("");
        ImageLoadManager.remove(userAccountImage);
        userAccountImage.setImageDrawable(UiUtils.getDrawable(getContext(),
                R.drawable.default_account_image));

        //Set user informations if available
        if(userInfo != null){
            userAccountName.setText(userInfo.getDisplayFullName());
            ImageLoadManager.load(getContext(), userInfo.getAcountImageURL(), userAccountImage);
        }

        //Set post creation time
        ((TextView) convertView.findViewById(R.id.post_creation_time)).setText(utils.
                timeToString(Utilities.time() - post.getPost_time()));

        //Set post content
        ((TextView) convertView.findViewById(R.id.post_content)).setText(Utilities.prepareStringTextView(post.getContent()));

        //Set post image (if any)
        ImageView postImage = convertView.findViewById(R.id.post_image);
        postImage.setVisibility(View.GONE);
        postImage.setImageDrawable(null);
        ImageLoadManager.remove(postImage);
        if(post.getType() == PostTypes.IMAGE){

            //Make image visible
            postImage.setVisibility(View.VISIBLE);

            //Load image
            ImageLoadManager.load(getContext(), post.getFile_path_url(), postImage);
        }

        return convertView;
    }
}

package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.ImageLoadHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.models.Comment;
import org.communiquons.android.comunic.client.ui.listeners.onPostUpdateListener;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.android.comunic.client.ui.views.LikeButtonView;

import java.util.ArrayList;

/**
 * Comments adapter
 *
 * @author Pierre HUBERT
 * Created by pierre on 3/11/18.
 */

class CommentsAdapter extends ArrayAdapter<Comment> {

    /**
     * Class constructor
     *
     * @param context The context of the target activity
     * @param list The comments list
     */
    public CommentsAdapter(Context context, ArrayList<Comment> list){
        super(context, 0, list);
    }

    /**
     * Inflate and return a filled comment object
     *
     * @param context The context of the application
     * @param comment The comment to fill
     * @param listener The actions update listener
     * @param user Information about the user (NULL for none)
     * @param viewGroup Target view group
     * @return Generated view
     */
    static View getInflatedView(Context context, Comment comment,
                                onPostUpdateListener listener,
                                @Nullable UserInfo user, ViewGroup viewGroup){

        //Inflate a view
        View v = LayoutInflater.from(context).inflate(R.layout.comment_item, viewGroup, false);

        //Return filled view
        return fillView(context, v, comment, user, listener);

    }

    /**
     * Fill a view with a specified comments information
     *
     * @param context The context of the activity / application
     * @param view The view to update
     * @param comment The comment to update
     * @param user Information about the user (NULL for none)
     * @param listener Posts updates listener
     * @return Updated view
     */
    private static View fillView(final Context context, final View view, final Comment comment,
                                 @Nullable UserInfo user, final onPostUpdateListener listener) {

        //Update user name and account image
        ImageView accountImage = view.findViewById(R.id.user_account_image);
        TextView accountName = view.findViewById(R.id.user_account_name);

        if (user == null) {
            accountImage.setImageDrawable(UiUtils.getDrawable(context,
                    R.drawable.default_account_image));
            accountName.setText("");
        } else {
            ImageLoadHelper.load(context, user.getAcountImageURL(), accountImage);
            accountName.setText(user.getDisplayFullName());
        }

        //Update comment content
        ((TextView) view.findViewById(R.id.comment_text)).setText(comment.getContent());

        //Update comment image (if any)
        ImageView commentImage = view.findViewById(R.id.comment_image);
        if(comment.getImage_url().length() < 5)
            commentImage.setVisibility(View.GONE);
        else {
            commentImage.setVisibility(View.VISIBLE);
            ImageLoadHelper.remove(commentImage);
            ImageLoadHelper.load(context, comment.getImage_url(), commentImage);
        }


        //Update comment likes
        LikeButtonView like = view.findViewById(R.id.like_button);
        like.setSmallButton(true);
        like.setNumberLikes(comment.getLikes());
        like.setIsLiking(comment.isLiking());
        like.setUpdateListener(new LikeButtonView.OnLikeUpdateListener() {
            @Override
            public void OnLikeUpdate(boolean isLiking) {
                listener.onCommentLikeUpdate(comment, isLiking);
            }
        });


        //Update comment actions
        ImageView actions = view.findViewById(R.id.comment_actions_btn);

        actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.showCommentActions(view, comment);
            }
        });

        //Return view
        return view;


    }
}

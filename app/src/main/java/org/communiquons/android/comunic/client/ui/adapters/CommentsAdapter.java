package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.comments.Comment;

import java.util.ArrayList;

/**
 * Comments adapter
 *
 * @author Pierre HUBERT
 * Created by pierre on 3/11/18.
 */

public class CommentsAdapter extends ArrayAdapter<Comment> {

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
     * @param user Information about the user (NULL for none)
     * @param viewGroup Target view group
     * @return Generated view
     */
    static View getInflatedView(Context context, Comment comment, @Nullable UserInfo user,
                                       ViewGroup viewGroup){

        //Inflate a view
        View v = LayoutInflater.from(context).inflate(R.layout.comment_item, viewGroup, false);

        //Return filled view
        return fillView(v, comment, user);

    }

    /**
     * Fill a view with a specified comments informations
     *
     * @param view The view to update
     * @param comment The comment to update
     * @param user Information about the user (NULL for none)
     * @return Updated view
     */
    private static View fillView(View view, Comment comment, @Nullable UserInfo user){


        //Update comment content
        ((TextView)view.findViewById(R.id.comment_text)).setText(comment.getContent());

        //Return view
        return view;

    }
}

package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.Account.AccountUtils;
import org.communiquons.android.comunic.client.data.ImageLoad.ImageLoadManager;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.comments.Comment;
import org.communiquons.android.comunic.client.data.comments.CommentsHelper;
import org.communiquons.android.comunic.client.data.utils.UiUtils;

import java.util.ArrayList;

/**
 * Comments adapter
 *
 * @author Pierre HUBERT
 * Created by pierre on 3/11/18.
 */

public class CommentsAdapter extends ArrayAdapter<Comment> {

    /**
     * Comment helper
     */
    private static CommentsHelper mCHelper = null;

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

        //Create comment helper if required
        if(mCHelper == null){
            mCHelper = new CommentsHelper(context.getApplicationContext());
        }

        //Inflate a view
        View v = LayoutInflater.from(context).inflate(R.layout.comment_item, viewGroup, false);

        //Return filled view
        return fillView(context, v, comment, user);

    }

    /**
     * Fill a view with a specified comments informations
     *
     * @param context The context of the acivitiy / application
     * @param view The view to update
     * @param comment The comment to update
     * @param user Information about the user (NULL for none)
     * @return Updated view
     */
    private static View fillView(final Context context, View view, Comment comment,
                                 @Nullable UserInfo user) {

        //Check wether the current user is the owner of the comment or not
        final boolean isOwner = AccountUtils.getID(context) == comment.getUserID();

        //Update user name and account image
        ImageView accountImage = view.findViewById(R.id.user_account_image);
        TextView accountName = view.findViewById(R.id.user_account_name);

        if (user == null) {
            accountImage.setImageDrawable(UiUtils.getDrawable(context,
                    R.drawable.default_account_image));
            accountName.setText("");
        } else {
            ImageLoadManager.load(context, user.getAcountImageURL(), accountImage);
            accountName.setText(user.getDisplayFullName());
        }

        //Update comment content
        ((TextView) view.findViewById(R.id.comment_text)).setText(comment.getContent());

        //Update comment actions
        ImageButton actions = view.findViewById(R.id.comment_actions_btn);

        //Create context menu
        actions.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                //Inflate the menu
                new MenuInflater(context).inflate(R.menu.menu_comments_actions, menu);

                //Disable some entries accordingly to ownership status
                menu.findItem(R.id.action_delete).setEnabled(isOwner);

            }
        });


        //Return view
        return view;


    }
}

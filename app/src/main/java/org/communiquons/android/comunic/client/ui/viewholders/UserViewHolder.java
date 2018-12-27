package org.communiquons.android.comunic.client.ui.viewholders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.ui.listeners.onOpenUsersPageListener;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

/**
 * User view holder
 *
 * Handles displaying of a single user
 *
 * @author Pierre HUBERT
 */
public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //Private fields
    private onOpenUsersPageListener mListener;
    private WebUserAccountImage mUserAccountImage;
    private TextView mUserName;
    private UserInfo mUserInfo;

    public UserViewHolder(@NonNull View itemView, @Nullable onOpenUsersPageListener listener) {
        super(itemView);

        mUserAccountImage = itemView.findViewById(R.id.user_account_image);
        mUserName = itemView.findViewById(R.id.user_name);

        this.mListener = listener;
        itemView.setOnClickListener(this);
    }


    /**
     * Apply a new user to this view
     *
     * @param userInfo Information about the user to apply
     */
    public void bind(UserInfo userInfo){
        this.mUserInfo = userInfo;
        mUserAccountImage.setUser(userInfo);
        mUserName.setText(userInfo.getDisplayFullName());
    }

    @Override
    public void onClick(View v) {
        if(mListener != null && v.equals(itemView))
            mListener.openUserPage(mUserInfo.getId());
    }
}

package org.communiquons.android.comunic.client.data.friendsList;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.communiquons.android.comunic.client.BuildConfig;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.ImageLoad.ImageLoadManager;
import org.communiquons.android.comunic.client.data.Utilities;

import java.util.ArrayList;

/**
 * Adapter that render the list of friend on the friends list fragment
 *
 * @author Pierre Hubert
 * Created by pierre on 11/15/17.
 */

public class FriendsAdapter extends ArrayAdapter<FriendUser> {


    /**
     * Class constructor
     *
     * @param context The context of execution of the application
     * @param friendsList The list of friends to display (with user information)
     */
    public FriendsAdapter(Activity context, ArrayList<FriendUser> friendsList){
        super(context, 0, friendsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        //Check if the view has to be created
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_friends_list_friend_item, parent, false);
        }

        //Get friend information
        FriendUser friendUser = getItem(position);

        //Update user account image
        ImageView user_image = listItemView.findViewById(R.id.fragment_friendslist_item_accountimage);
        user_image.setImageDrawable(getContext().getDrawable(R.drawable.default_account_image));
        ImageLoadManager.load(getContext(), friendUser.getUserInfo().getAcountImageURL(), user_image);

        //Update user name
        TextView user_name = listItemView.findViewById(R.id.fragment_friendslist_item_fullname);
        user_name.setText(Utilities.prepareStringTextView(friendUser.getUserInfo().getFullName()));

        //Update user status
        boolean signed_in = friendUser.getFriend().signed_in();
        TextView statusView = listItemView.findViewById(R.id.fragment_friendslist_item_status);

        //Set the text
        statusView.setText(signed_in ?
                getContext().getText(R.string.user_status_online) :
                getContext().getText(R.string.user_status_offline)
        );

        //Set the color
        int status_color = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            status_color = getContext().getResources().getColor(signed_in ? R.color.holo_green_dark : R.color.darker_gray, null);
        }
        else {
            status_color = getContext().getResources().getColor(signed_in ? R.color.holo_green_dark : R.color.darker_gray);
        }
        statusView.setTextColor(status_color);

        return listItemView;
    }

}

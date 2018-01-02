package org.communiquons.android.comunic.client.data.UsersInfo;

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
import org.communiquons.android.comunic.client.data.utils.UiUtils;

import java.util.ArrayList;

/**
 * User async infos adapter
 *
 * Similar to UsersBasicAdapter, but the information about users can be given after the users
 * themselve
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/2/18.
 */

public class UsersAsysncInfoAdapter extends ArrayAdapter<Integer> {

    /**
     * Informations about the members of the conversation
     */
    private ArrayMap<Integer, UserInfo> usersInfos;

    /**
     * Constructor
     * @param context The context of the application
     * @param IDs The list of IDs of users
     * @param usersInfos Informations about the users (can be updated asynchronously with the list
     *                   of users ID)
     */
    public UsersAsysncInfoAdapter(Context context, @NonNull ArrayList<Integer> IDs,
                                  @NonNull ArrayMap<Integer, UserInfo> usersInfos){
        super(context, 0, IDs);

        //Save user information array map
        this.usersInfos = usersInfos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.user_basic_adapter_item, parent, false);

        //Get the views
        ImageView account_image = convertView.findViewById(R.id.user_account_image);
        TextView account_name = convertView.findViewById(R.id.user_name);

        //Empty the entry
        ImageLoadManager.remove(account_image);
        account_image.setImageDrawable(UiUtils.getDrawable(getContext(),
                R.drawable.default_account_image));
        account_name.setText("");

        //Get user ID
        int userID = getItem(position);

        //Check if we go user informations
        if(usersInfos.containsKey(userID)){

            UserInfo user = usersInfos.get(userID);

            account_name.setText(user.getDisplayFullName());
            ImageLoadManager.load(getContext(), user.getAcountImageURL(), account_image);
        }

        return convertView;
    }

    /**
     * Get the ID of a position
     *
     * This method is overriden in order to remove the @Nullable tag
     *
     * @param position The position of the item
     * @return The ID of the personn or -1 in case of failure
     */
    @NonNull
    @Override
    public Integer getItem(int position) {
        Integer ID = super.getItem(position);
        return ID == null ? -1 : ID;
    }
}

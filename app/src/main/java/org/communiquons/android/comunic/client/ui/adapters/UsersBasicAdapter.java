package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

import java.util.ArrayList;

/**
 * User basic adapter
 *
 * Allow to display basic informations about a set of users in a ListView
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/1/18.
 */

public class UsersBasicAdapter extends ArrayAdapter<UserInfo> {

    /**
     * Public constructor
     *
     * @param context The context of the activity
     * @param list The dataset
     */
    public UsersBasicAdapter(@NonNull Context context, @NonNull ArrayList<UserInfo> list){
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Check if the view has to be inflated
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.user_basic_adapter_item, parent, false);

        //Get item
        UserInfo userInfos = getItem(position);

        if(userInfos != null){

            //Set user name
            ((TextView) convertView.findViewById(R.id.user_name)).
                    setText(userInfos.getDisplayFullName());

            //Set account image
            ImageView account_image = convertView.findViewById(R.id.user_account_image);
            ImageLoadHelper.remove(account_image);
            account_image.setImageDrawable(UiUtils.getDrawable(getContext(),
                    R.drawable.default_account_image));
            ImageLoadHelper.load(getContext(), userInfos.getAcountImageURL(), account_image);
        }

        return convertView;
    }
}

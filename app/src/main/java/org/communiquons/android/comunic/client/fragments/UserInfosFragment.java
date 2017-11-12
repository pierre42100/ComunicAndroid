package org.communiquons.android.comunic.client.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.Account.AccountUtils;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.ImageLoadTask;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersInfos;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;

/**
 * User informations fragment
 *
 * This fragment display informations about the user
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/11/17.
 */

public class UserInfosFragment extends Fragment {

    /**
     * Application context
     */
    Context mContext;

    /**
     * Database helper
     */
    DatabaseHelper dbHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View result =  inflater.inflate(R.layout.fragment_userinfos, container, false);

        //Get context
        mContext = getActivity().getApplicationContext();

        //Open DBHelper if required
        if(dbHelper == null)
            dbHelper = new DatabaseHelper(mContext);

        //Get required views
        final ImageView imageView = (ImageView) result.findViewById(R.id.fragments_userinfos_account_image);
        final TextView userNameView = (TextView) result.findViewById(R.id.fragments_userinfos_user_name);

        //Retrieve user informations in order to display them
        int user_id = new AccountUtils(mContext).get_current_user_id();
        new GetUsersInfos(mContext, dbHelper).get(user_id, new GetUsersInfos.getUserInfosCallback() {
            @Override
            public void callback(UserInfo info) {

                //Set the name of the user
                userNameView.setText(info.getFullName());

                //Get and show the user account image
                new ImageLoadTask(mContext, info.getAcountImageURL(), imageView).execute();
            }
        });

        return result;
    }
}

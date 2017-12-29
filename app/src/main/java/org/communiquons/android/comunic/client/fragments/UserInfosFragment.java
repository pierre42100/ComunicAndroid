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
import android.widget.Toast;

import org.communiquons.android.comunic.client.MainActivity;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.Account.AccountUtils;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.ImageLoad.ImageLoadTask;
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
        return inflater.inflate(R.layout.fragment_userinfos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get context
        mContext = getActivity().getApplicationContext();

        //Open DBHelper if required
        if(dbHelper == null)
            dbHelper = DatabaseHelper.getInstance(mContext);

        //Get required views
        final ImageView imageView = view.findViewById(R.id.fragments_userinfos_account_image);
        final TextView userNameView = view.findViewById(R.id.fragments_userinfos_user_name);

        //Retrieve user informations in order to display them
        int user_id = new AccountUtils(mContext).get_current_user_id();
        new GetUsersInfos(mContext, dbHelper).get(user_id, new GetUsersInfos.getUserInfosCallback() {
            @Override
            public void callback(UserInfo info) {

                //Check for errors
                if(info == null){
                    Toast.makeText(mContext, R.string.err_get_user_info, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Set the name of the user
                userNameView.setText(info.getFullName());

                //Get and show the user account image
                new ImageLoadTask(mContext, info.getAcountImageURL(), imageView).execute();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        //Update the title
        getActivity().setTitle(R.string.fragment_userinfos_title);

        //Update the bottom navigation menu
        ((MainActivity) getActivity())
                .setSelectedNavigationItem(R.id.main_bottom_navigation_me_view);
    }
}

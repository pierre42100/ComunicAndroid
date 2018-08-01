package org.communiquons.android.comunic.client.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.AdvancedUserInfo;
import org.communiquons.android.comunic.client.data.utils.Utilities;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

/**
 * Advanced user information fragment
 *
 * @author Pierre HUBERT
 */
public class AdvancedUserInfoFragment extends Fragment {

    /**
     * Advanced information about the user
     */
    private AdvancedUserInfo mAdvancedUserInfo;

    /**
     * User account image
     */
    private WebUserAccountImage mUserAccountImage;

    /**
     * The name of the user
     */
    private TextView mUserName;

    /**
     * Target for the time the user has been a member of the group
     */
    private TextView mMemberSinceTarget;

    /**
     * Set advanced information about the user
     *
     * Warning! This method must be called before the fragment is started, else a NullPointerException
     * will be thrown
     *
     * @param advancedUserInfo Advanced information about the user
     */
    public void setAdvancedUserInfo(AdvancedUserInfo advancedUserInfo) {
        this.mAdvancedUserInfo = advancedUserInfo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advanced_user_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get the views
        mUserAccountImage = view.findViewById(R.id.user_account_image);
        mUserName = view.findViewById(R.id.user_name);
        mMemberSinceTarget = view.findViewById(R.id.member_since_value);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Apply user information
        mUserAccountImage.setUser(mAdvancedUserInfo);
        mUserName.setText(mAdvancedUserInfo.getDisplayFullName());
        mMemberSinceTarget.setText(new Utilities(getActivity()).timeToString(
                Utilities.time() - mAdvancedUserInfo.getAccount_creation_time()));
    }
}

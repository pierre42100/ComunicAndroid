package org.communiquons.android.comunic.client.ui.fragments.userpage;

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
import org.communiquons.android.comunic.client.data.utils.AccountUtils;
import org.communiquons.android.comunic.client.data.utils.TimeUtils;
import org.communiquons.android.comunic.client.ui.views.FriendshipStatusButton;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

/**
 * Advanced user information fragment
 *
 * Warning !!! This fragment is not made to work in an autonomous way !!!
 *
 * @author Pierre HUBERT
 */
public class AdvancedUserInfoFragment extends Fragment {

    /**
     * Advanced information about the user
     */
    private AdvancedUserInfo mAdvancedUserInfo;

    /**
     * Views
     */
    private WebUserAccountImage mUserAccountImage;
    private TextView mUserName;
    private TextView mUserTag;
    private TextView mMemberSinceTarget;
    private FriendshipStatusButton mFriendshipStatus;

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
        mUserTag = view.findViewById(R.id.userTag);
        mMemberSinceTarget = view.findViewById(R.id.member_since_value);
        mFriendshipStatus = view.findViewById(R.id.friendship_status);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Apply user information
        mUserAccountImage.setUser(mAdvancedUserInfo);
        mUserName.setText(mAdvancedUserInfo.getDisplayFullName());
        mMemberSinceTarget.setText(TimeUtils.TimeToString(getActivity(),
                TimeUtils.time() - mAdvancedUserInfo.getAccount_creation_time()));
        mUserTag.setText(mAdvancedUserInfo.hasVirtualDirectory() ?
                "@" + mAdvancedUserInfo.getVirtualDirectory() : "");
    }

    @Override
    public void onResume() {
        super.onResume();

        if(AccountUtils.getID(getActivity()) == mAdvancedUserInfo.getId()) {
            mFriendshipStatus.setVisibility(View.GONE);
        }
        else {
            mFriendshipStatus.setUserID(mAdvancedUserInfo.getId());
            mFriendshipStatus.refreshIfRequired();
        }

    }
}

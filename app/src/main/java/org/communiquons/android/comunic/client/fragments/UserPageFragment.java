package org.communiquons.android.comunic.client.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.AsyncTask;
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
import org.communiquons.android.comunic.client.data.ImageLoad.ImageLoadManager;
import org.communiquons.android.comunic.client.data.UsersInfo.AdvancedUserInfo;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersHelper;
import org.communiquons.android.comunic.client.data.utils.UiUtils;

/**
 * User page fragment
 *
 * Display the page of a user
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/13/18.
 */

public class UserPageFragment extends Fragment {

    /**
     * The name of the argument that contains user id
     */
    public static final String ARGUMENT_USER_ID = "userID";

    /**
     * The ID of the current user
     */
    private int userID;

    /**
     * User informations
     */
    private AdvancedUserInfo userInfo;

    /**
     * Get user helper
     */
    private GetUsersHelper getUsersHelper;

    /**
     * Loading alert dialog
     */
    private AlertDialog loadingDialog;

    /**
     * User account name
     */
    private TextView user_name;

    /**
     * User account image
     */
    private ImageView user_image;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Save user ID
        userID = getArguments().getInt(ARGUMENT_USER_ID);

        //Get database helper
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());

        //Create getUserHelper instance
        getUsersHelper = new GetUsersHelper(getActivity(), dbHelper);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get the user
        user_image = view.findViewById(R.id.user_account_image);
        user_name = view.findViewById(R.id.user_account_name);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Check if we got informations about the user
        if(userInfo == null){

            //Show loading alert dialog
            loadingDialog = UiUtils.create_loading_dialog(getActivity());

            //Fetch user informations
            new AsyncTask<Integer, Void, AdvancedUserInfo>(){
                @Override
                protected AdvancedUserInfo doInBackground(Integer... params) {
                    return getUsersHelper.get_advanced_infos(params[0]);
                }

                @Override
                protected void onPostExecute(AdvancedUserInfo advancedUserInfo) {
                    //Continue only if the activity hasn't ended
                    if(getActivity() != null)
                        onGotUserInfo(advancedUserInfo);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userID);
        }
        else
            onGotUserInfo(userInfo);
    }

    @Override
    public void onPause() {
        super.onPause();

        //Remove loading dialog
        if(loadingDialog != null)
            loadingDialog.dismiss();
    }

    /**
     * This function is called on the main thread once we got user informations
     *
     * @param info Informations about the user
     */
    public void onGotUserInfo(@Nullable AdvancedUserInfo info){

        //Remove loading dialog
        if(loadingDialog != null)
            loadingDialog.dismiss();

        //Check for errors
        if(info == null){
            Toast.makeText(getActivity(), R.string.err_get_user_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Save user informations
        userInfo = info;

        //Set activity title
        getActivity().setTitle(userInfo.getDisplayFullName());

        //Update activity menu dock
        if(AccountUtils.getID(getActivity()) == userID)
            ((MainActivity) getActivity()).setSelectedNavigationItem(
                    R.id.main_bottom_navigation_me_view);
        else
            ((MainActivity) getActivity()).setSelectedNavigationItem(
                    R.id.main_bottom_navigation_users_view);

        //Update user name and account image
        user_name.setText(userInfo.getDisplayFullName());
        ImageLoadManager.remove(user_image);
        ImageLoadManager.load(getActivity(), userInfo.getAcountImageURL(), user_image);

    }
}
package org.communiquons.android.comunic.client.ui.fragments.accountsettings;

import android.app.AlertDialog;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.communiquons.android.comunic.client.ui.utils.UiUtils;

/**
 * Base account settings fragment
 */
public abstract class BaseAccountSettingsFragment extends PreferenceFragmentCompat {

    //Loading dialog
    private AlertDialog mLoadingDialog;

    /**
     * Remove any currently visible loading dialog
     */
    protected void removeLoadingDialog(){
        if(mLoadingDialog != null)
            mLoadingDialog.dismiss();
    }

    /**
     * Show (display) a new loading dialog
     */
    protected void showLoadingDialog(){
        removeLoadingDialog();
        mLoadingDialog = UiUtils.create_loading_dialog(getActivity());
    }

}

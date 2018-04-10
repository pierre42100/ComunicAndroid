package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.notifications.NotificationsHelper;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;

/**
 * Notifications fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/1/18.
 */

public class NotificationsFragment extends Fragment {

    /**
     * Notifications helper
     */
    private NotificationsHelper mNotificationsHelper;

    /**
     * Delete all the notifications button
     */
    private View mDeleteNotificationsBtn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //Create notifications helper
        mNotificationsHelper = new NotificationsHelper(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Delete all the notifications action
        mDeleteNotificationsBtn = view.findViewById(R.id.delete_all_notif_btn);
        mDeleteNotificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNotificationsDeletion();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //Update the title of the application
        getActivity().setTitle(R.string.fragment_notifications_title);

        //Update the bottom navigation menu
        ((MainActivity) getActivity())
                .setSelectedNavigationItem(R.id.main_bottom_navigation_notif);
    }

    /**
     * Ask the user to confirm the deletion of all the notifications
     */
    private void confirmNotificationsDeletion(){

        //Create and display a confirmation dialog
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_deleteallnotifs_title)
                .setMessage(R.string.dialog_deleteallnotifs_message)
                .setNegativeButton(R.string.dialog_deleteallnotifs_cancel, null)

                .setPositiveButton(R.string.dialog_deleteallnotifs_confirm,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processAllNotificationsDeletion();
                    }
                })

                .show();

    }

    /**
     * Do the deletion of all the notifications
     */
    private void processAllNotificationsDeletion(){

        //Perform the operation in the background
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                return mNotificationsHelper.deleteAllNotifs();
            }

            @Override
            protected void onPostExecute(Boolean result) {

                //Check if the activity has been destroyed
                if(getActivity() == null)
                    return;

                //Check for error
                if(!result) {
                    Toast.makeText(getActivity(), R.string.err_delete_all_notifs,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Success
                Toast.makeText(getActivity(), R.string.success_delete_all_notifs,
                        Toast.LENGTH_SHORT).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}
